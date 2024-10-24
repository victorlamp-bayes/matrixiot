package com.victorlamp.matrixiot.service.management.controller.thing;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.enums.EnumUtils;
import com.victorlamp.matrixiot.common.util.excel.ExcelUtils;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.common.validation.groups.Create;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingGeo;
import com.victorlamp.matrixiot.service.management.controller.thing.vo.*;
import com.victorlamp.matrixiot.service.management.convert.ThingConvert;
import com.victorlamp.matrixiot.service.management.dto.thing.InvokeServiceReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingGeoReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thing.SubThingPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigItem;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingStatistics;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum;
import com.victorlamp.matrixiot.service.management.utils.excel.ThingImportExcelVOListener;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;
import static com.victorlamp.matrixiot.service.management.constant.ErrorCodeConstants.THING_IMPORT_FILE_SIZE_LT_10M;

@OpenAPIDefinition
@Tag(name = "设备管理 - 设备")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/thing", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ThingController {

    @Resource
    private ThingService thingService;
    @Resource
    private ProductService productService;

    @Operation(summary = "创建设备")
    @PostMapping("create")
    @Validated(Create.class)
    CommonResult<Thing> createThing(@RequestBody @Valid ThingCreateReqVO reqVO) {
        Thing thing = thingService.createThing(ThingConvert.INSTANCE.toDTO(reqVO));
        return success(thing);
    }

    @Operation(summary = "查询设备详情")
    @GetMapping("describe")
    CommonResult<Thing> describeThing(@RequestParam("id") @IdHex24 @NotBlank String id) {
        Thing thing = thingService.getThing(id);
        return success(thing);
    }

    @Operation(summary = "更新设备")
    @PatchMapping("update")
    CommonResult<Boolean> updateThing(@RequestParam("id") @IdHex24 @NotBlank String id,
                                      @RequestBody @Valid ThingUpdateReqVO reqVO) {
        thingService.updateThing(id, ThingConvert.INSTANCE.toDTO(reqVO));
        return success(true);
    }

    @Operation(summary = "更新设备状态")
    @PatchMapping("update-status")
    CommonResult<Boolean> updateThingStatus(@RequestParam("id") @IdHex24 @NotBlank String id,
                                            @RequestBody @Valid ThingUpdateStatusReqVO reqVO) {
        thingService.updateThing(id, ThingConvert.INSTANCE.toDTO(reqVO));
        return success(true);
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("delete")
    CommonResult<Boolean> deleteThing(@RequestParam("id") @IdHex24 @NotBlank String id) {
        thingService.deleteThing(id);
        return success(true);
    }

    @Operation(summary = "列表/查询设备")
    @GetMapping("list")
    @Validated
    CommonResult<PageResult<Thing>> listThingPage(ThingPageReqDTO reqDTO) {
        PageResult<Thing> thingPageResult = thingService.listThingPage(reqDTO);
        return success(thingPageResult);
    }

    @Operation(summary = "导入设备")
    @PostMapping("import")
    @Parameters({
            @Parameter(name = "file", description = "Excel 文件", required = true),
            @Parameter(name = "productId", description = "产品ID", required = true),
            @Parameter(name = "updateSupport", description = "是否支持更新，默认为 false", example = "true")
    })
    public CommonResult<Boolean> importThings(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "productId") @IdHex24 @NotBlank String productId,
                                              @RequestParam(value = "updateSupport", required = false, defaultValue = "false") boolean updateSupport) throws Exception {
        if (file.getSize() > 10 * 1024 * 1024) {
            throw exception(THING_IMPORT_FILE_SIZE_LT_10M);
        }

        Product product = productService.getProduct(productId);

        List<ThingImportExcelVO> excelList = ExcelUtils.read(file, ThingImportExcelVO.class, 1, new ThingImportExcelVOListener());
        thingService.importThings(ThingConvert.INSTANCE.importVOToReqDTO(excelList, product), productId, updateSupport);
        return success(true);
    }


    @Operation(summary = "导出设备")
    @GetMapping("/export")
    public void exportThing(@RequestParam("productId") @IdHex24 String productId, HttpServletResponse response) throws IOException {
        // 获取设备列表
        List<Thing> things = thingService.listAll(productId);
        // 拼接数据
        List<ThingExcelVO> excelThings = ThingConvert.INSTANCE.toExcelVO(things);
        // 输出
        ExcelUtils.write(response, "设备数据.xls", "设备列表", ThingExcelVO.class, excelThings);
    }

    @Operation(summary = "添加子设备到网关设备")
    @PostMapping("add-sub")
    CommonResult<Boolean> addSubThing(@RequestParam("gatewayId") @IdHex24 @NotBlank String gatewayId,
                                      @RequestParam("subThingId") String subThingId) {
        thingService.addSubThing(gatewayId, subThingId);
        return success(true);
    }

    @Operation(summary = "批量添加子设备到网关设备")
    @PostMapping("add-sub-batch")
    CommonResult<Boolean> batchAddSubThing(@RequestParam("gatewayId") @IdHex24 @NotBlank String gatewayId,
                                           @RequestBody @Valid List<String> subThingId) {
        thingService.batchAddSubThing(gatewayId, subThingId);
        return success(true);
    }

    @Operation(summary = "添加产品所有子设备到网关设备")
    @PostMapping("add-sub-all")
    CommonResult<Boolean> addAllSubThing(@RequestParam("gatewayId") @IdHex24 @NotBlank String gatewayId,
                                         @RequestParam("productId") @IdHex24 @NotBlank String productId) {
        thingService.addAllSubThing(gatewayId, productId);
        return success(true);
    }

    @Operation(summary = "删除网关设备下的子设备")
    @PostMapping("remove-sub")
    CommonResult<Boolean> removeSubThing(@RequestParam("gatewayId") @IdHex24 @NotBlank String gatewayId,
                                         @RequestParam("subThingId") String subThingId) {
        thingService.removeSubThing(gatewayId, subThingId);
        return success(true);
    }

    @Operation(summary = "批量删除网关设备下的子设备")
    @PostMapping("remove-sub-batch")
    CommonResult<Boolean> batchRemoveSubThing(@RequestParam("gatewayId") @IdHex24 @NotBlank String gatewayId,
                                              @RequestBody @Valid List<String> subThingId) {
        thingService.batchRemoveSubThing(gatewayId, subThingId);
        return success(true);
    }

    @Operation(summary = "删除网关设备下的所有子设备")
    @PostMapping("remove-sub-all")
    CommonResult<Boolean> removeAllSubThing(@RequestParam("gatewayId") @IdHex24 @NotBlank String gatewayId) {
        thingService.removeAllSubThing(gatewayId);
        return success(true);
    }

    @Operation(summary = "列出/查询网关设备的子设备")
    @GetMapping("list-sub")
    CommonResult<PageResult<Thing>> listSubThing(@RequestParam("gatewayId") @IdHex24 @NotBlank String gatewayId, SubThingPageReqDTO reqDTO) {
        PageResult<Thing> pageResult = thingService.listSubThingPage(gatewayId, reqDTO);
        return success(pageResult);
    }

    @Operation(summary = "获取设备外部配置类型列表")
    @GetMapping("external-type/list")
    CommonResult<List<Object>> listExternalType() {
        return success(EnumUtils.listEnumValue(ExternalTypeEnum.class));
    }

    @Operation(summary = "获取设备外部配置项列表")
    @GetMapping("external-config-item/list")
    CommonResult<List<ExternalConfigItem>> listExternalConfigItem(@RequestParam("type") String type) {
        return success(thingService.listExternalConfigItem(type));
    }

    @Operation(summary = "获取设备统计信息")
    @GetMapping("statistics")
    CommonResult<ThingStatistics> getThingStatistics(@RequestParam("productId") @IdHex24 String productId) {
        return success(thingService.getStatistics(productId));
    }

    @Operation(summary = "获取行政区划范围内所有设备聚合地理位置")
    @PostMapping("aggregated-geo")
    CommonResult<ThingGeo> getAggregatedThingGeo(
            @RequestBody @Valid ThingGeoReqDTO reqDTO) {
        ThingGeo thingGeo = thingService.getAggregatedThingGeo(reqDTO);
        return success(thingGeo);
    }

    // TODO 属性设置接口

    @Operation(summary = "调用设备服务")
    @PostMapping("invoke-service")
    CommonResult<Boolean> invokeThingService(@RequestBody @Valid ThingData thingData) {
        thingService.invokeThingService(thingData);
        return success(true);
    }

    // TODO 权限认证
    @Operation(summary = "三方设备调用服务")
    @PostMapping("open/invoke-service")
    CommonResult<Boolean> invokeService(@RequestBody @Valid InvokeServiceReqDTO reqDTO) {
        thingService.thirdInvokeThingService(reqDTO);
        return success(true);
    }

//    @Operation(summary = "批量设置设备属性")
//    @PostMapping("property/create")
//    ResponseDTO setThingProperties(
//            @RequestParam("thingId") @IdHex24 String thingId,
//            @RequestBody @Valid ThingData.CommandDTO requestDTO) {
//        thingService.setThingProperties(thingId, requestDTO);
//
//        return ResponseDTO.success();
//    }
//
//    @Operation(summary = "调用设备服务")
//    @PostMapping("service/create")
//    ResponseDTO invokeThingService(
//            @RequestParam("thingId") @IdHex24 String thingId,
//            @RequestBody @Valid ThingData.CommandDTO.ServiceCommandDTO requestDTO) {
//        thingService.invokeThingService(thingId, requestDTO);
//
//        return ResponseDTO.success();
//    }

}
