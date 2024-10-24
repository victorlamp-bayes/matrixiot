package com.victorlamp.matrixiot.service.management.convert;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.victorlamp.matrixiot.service.management.constant.ThingExternalConfigItems;
import com.victorlamp.matrixiot.service.management.controller.thing.vo.*;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingCreateReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingImportReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingUpdateReqDTO;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.GeoPoint;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingExternalConfig;
import com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.management.constant.ErrorCodeConstants.THING_INVALID_EXTERNAL_TYPE;

@Mapper(uses = ProductConvert.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ThingConvert {
    ThingConvert INSTANCE = Mappers.getMapper(ThingConvert.class);

    ThingCreateReqDTO toDTO(ThingCreateReqVO reqVO);

    ThingUpdateReqDTO toDTO(ThingUpdateReqVO reqVO);

    ThingUpdateReqDTO toDTO(ThingUpdateStatusReqVO reqVO);

    Thing toEntity(ThingCreateReqDTO createReqDTO);


    @Named("jsonToString")
    public default String jsonToString(JSONObject json) {
        return JSON.toJSONString(json);
    }

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "nodeType", source = "product.nodeType")
    @Mapping(target = "externalConfigType", source = "externalConfig.type")
    @Mapping(target = "externalConfig", source = "externalConfig.config", qualifiedByName = "jsonToString")
    ThingExcelVO toExcelVO(Thing thing);

    List<ThingExcelVO> toExcelVO(List<Thing> things);

    ThingCreateReqDTO importToCreateDTO(ThingImportReqDTO importReqDTO);

    ThingUpdateReqDTO importToUpdateDTO(ThingImportReqDTO importReqDTO);

    default ThingImportReqDTO importVOToReqDTO(ThingImportExcelVO excelVO, Product product) {
        // 构造设备外部配置
        JSONObject externalConfig = new JSONObject();
        ExternalTypeEnum externalType = EnumUtil.fromStringQuietly(ExternalTypeEnum.class, StringUtils.toRootUpperCase(product.getExternalConfig().getType()));
        if (externalType == null) {
            throw exception(THING_INVALID_EXTERNAL_TYPE);
        }

        switch (externalType) {
            case NB_AEP -> {
                externalConfig.put(ThingExternalConfigItems.AEP_ID, excelVO.getId());
                externalConfig.put(ThingExternalConfigItems.AEP_NAME, excelVO.getName());
                externalConfig.put(ThingExternalConfigItems.AEP_IMEI, excelVO.getImei());
                externalConfig.put(ThingExternalConfigItems.AEP_IMSI, excelVO.getImsi());
                externalConfig.put(ThingExternalConfigItems.AEP_ELECTRONIC_NO, excelVO.getElectronicNo());
                externalConfig.put(ThingExternalConfigItems.AEP_LONGITUDE, excelVO.getLongitude());
                externalConfig.put(ThingExternalConfigItems.AEP_LATITUDE, excelVO.getLatitude());
            }
            case NB_OC -> {
                externalConfig.put(ThingExternalConfigItems.OC_ID, excelVO.getId());
                externalConfig.put(ThingExternalConfigItems.OC_NAME, excelVO.getName());
                externalConfig.put(ThingExternalConfigItems.OC_IMEI, excelVO.getImei());
                externalConfig.put(ThingExternalConfigItems.OC_IMSI, excelVO.getImsi());
                externalConfig.put(ThingExternalConfigItems.OC_ELECTRONIC_NO, excelVO.getElectronicNo());
                externalConfig.put(ThingExternalConfigItems.OC_LONGITUDE, excelVO.getLongitude());
                externalConfig.put(ThingExternalConfigItems.OC_LATITUDE, excelVO.getLatitude());
            }
            case HUAXU_LORA -> {
                externalConfig.put(ThingExternalConfigItems.LORA_ID, excelVO.getMac()); // 导入信息中没有id，使用mac替代
                externalConfig.put(ThingExternalConfigItems.LORA_NAME, excelVO.getMac()); // 导入信息中没有name，使用mac替代
                excelVO.setName(excelVO.getMac());
                externalConfig.put(ThingExternalConfigItems.LORA_MAC, excelVO.getMac());
                externalConfig.put(ThingExternalConfigItems.LORA_ELECTRONIC_NO, excelVO.getElectronicNo());
                externalConfig.put(ThingExternalConfigItems.LORA_LONGITUDE, excelVO.getLongitude());
                externalConfig.put(ThingExternalConfigItems.LORA_LATITUDE, excelVO.getLatitude());
            }
            case HUAXU_GD_HUB -> {
                externalConfig.put(ThingExternalConfigItems.HUB_ID, excelVO.getId());
                externalConfig.put(ThingExternalConfigItems.HUB_NAME, excelVO.getName());
                externalConfig.put(ThingExternalConfigItems.HUB_CODE, excelVO.getHubCode());
            }
            case HUAXU_GD -> {
                externalConfig.put(ThingExternalConfigItems.HUB_SUB_ID, excelVO.getElectronicNo()); // 导入信息中没有name，使用 electronicNo 替代
                externalConfig.put(ThingExternalConfigItems.HUB_SUB_NAME, excelVO.getElectronicNo()); // 导入信息中没有name，使用 electronicNo 替代
                excelVO.setName(excelVO.getElectronicNo());
                externalConfig.put(ThingExternalConfigItems.HUB_SUB_ELECTRONIC_NO, excelVO.getElectronicNo());
                externalConfig.put(ThingExternalConfigItems.HUB_SUB_HUB_CODE, excelVO.getHubCode());
                externalConfig.put(ThingExternalConfigItems.HUB_SUB_CHANNEL, excelVO.getChannel());
                externalConfig.put(ThingExternalConfigItems.HUB_SUB_CHANNEL_PORT, excelVO.getChannelPort());
            }
            default -> {
            }
        }

        // 构造设备地理位置
        GeoPoint position = null;
        if (NumberUtil.isValidNumber(excelVO.getLongitude()) && NumberUtil.isValidNumber(excelVO.getLatitude())) {
            position = new GeoPoint(excelVO.getLongitude(), excelVO.getLatitude());
        }

        return ThingImportReqDTO.builder()
                .name(excelVO.getName())
                .position(position)
                .enabled(true) // 导入设备默认启用
                .externalConfig(new ThingExternalConfig(product.getExternalConfig().getType(), externalConfig))
                .build();
    }

    default List<ThingImportReqDTO> importVOToReqDTO(List<ThingImportExcelVO> excelVOs, Product product) {
        return excelVOs.stream().map(excelVO -> importVOToReqDTO(excelVO, product)).toList();
    }
}
