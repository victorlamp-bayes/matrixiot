package com.victorlamp.matrixiot.service.alarm.impl;

import cn.hutool.core.util.ObjUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.alarm.AlarmConfigService;
import com.victorlamp.matrixiot.service.alarm.convert.AlarmConfigConvert;
import com.victorlamp.matrixiot.service.alarm.dao.AlarmConfigRepository;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigCreateReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigPageReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigUpdateDTO;
import com.victorlamp.matrixiot.service.alarm.entity.AlarmConfig;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmLevelEnum;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.validation.MethodValidated;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.alarm.constant.ErrorCodeConstants.*;

@DubboService(validation = "true")
@Service("alarmConfigService")
@RequiredArgsConstructor
@Slf4j
public class AlarmConfigServiceImpl implements AlarmConfigService {

    @Resource
    private AlarmConfigRepository alarmConfigRepository;

    @DubboReference
    private ProductService productService;

    @Override
    @MethodValidated
    public AlarmConfig createAlarmConfig(AlarmConfigCreateReqDTO reqDTO) {
        validateProductExists(reqDTO.getProductId());
        AlarmConfig existAlarmConfig = alarmConfigRepository.findByProductId(reqDTO.getProductId());
        if (existAlarmConfig != null) {
            throw exception(ALARM_CONFIG_ALREADY_EXISTS);
        }
        AlarmConfig alarmConfig = AlarmConfigConvert.INSTANCE.toEntity(reqDTO);
        alarmConfigRepository.insert(alarmConfig);
        return alarmConfig;
    }

    @Override
    public PageResult<AlarmConfig> listAlarmConfigs(AlarmConfigPageReqDTO reqDTO) {
        return alarmConfigRepository.findPage(reqDTO);
    }

    @Override
    public AlarmConfig getAlarmConfig(String id) {
        return alarmConfigRepository.findById(id).orElse(null);
    }

    @Override
    @MethodValidated
    public void updateAlarmConfig(String id, AlarmConfigUpdateDTO alarmConfigUpdateDTO) {
        AlarmConfig alarmConfig = alarmConfigRepository.findById(id).orElse(null);
        if (ObjUtil.isNull(alarmConfig)) {
            throw exception(ALARM_CONFIG_NOT_EXISTS);
        }

        if (alarmConfigUpdateDTO.getDescription() != null) {
            alarmConfig.setDescription(alarmConfigUpdateDTO.getDescription());
        }

        if (alarmConfigUpdateDTO.getLevel() != null) {
            alarmConfig.setLevel(AlarmLevelEnum.valueOf(alarmConfigUpdateDTO.getLevel()));
        }

        if (alarmConfigUpdateDTO.getContacts() != null) {
            alarmConfig.setContacts(alarmConfigUpdateDTO.getContacts());
        }
        alarmConfigRepository.save(alarmConfig);
    }

    @Override
    public void deleteAlarmConfig(String id) {
        alarmConfigRepository.deleteById(id);
    }

    private void validateProductExists(String productId) {
        Product product = productService.getProduct(productId);
        if (product == null) {
            throw exception(ALARM_PRODUCT_NOT_EXISTS);
        }
    }

}
