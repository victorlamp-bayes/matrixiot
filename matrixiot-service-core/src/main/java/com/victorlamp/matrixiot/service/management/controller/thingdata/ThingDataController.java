package com.victorlamp.matrixiot.service.management.controller.thingdata;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.management.api.ThingDataService;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingEventDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingPropertyDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingServiceDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

@OpenAPIDefinition
@Tag(name = "设备管理 - 设备数据")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/thing-data", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ThingDataController {

    @Resource
    private ThingDataService thingDataService;

    @Operation(summary = "获取设备的最新物模型属性数据")
    @GetMapping("property/latest")
    CommonResult<List<ThingPropertyData>> getLatestThingPropertyData(@RequestParam("thingId") @IdHex24 @NotBlank String thingId) {
        return CommonResult.success(thingDataService.getLatestThingPropertyData(thingId));
    }

    @Operation(summary = "获取设备数据列表")
    @GetMapping("list")
    CommonResult<PageResult<ThingData>> listThingData(ThingDataPageReqDTO reqDTO) {
        return CommonResult.success(thingDataService.listThingDataPage(reqDTO));
    }

    @Operation(summary = "获取设备属性数据列表")
    @GetMapping("property/list")
    CommonResult<PageResult<ThingData>> listThingPropertyDataPage(ThingPropertyDataPageReqDTO reqDTO) {
        return CommonResult.success(thingDataService.listThingPropertyDataPage(reqDTO));
    }

    @Operation(summary = "获取设备事件数据列表")
    @GetMapping("event/list")
    CommonResult<PageResult<ThingData>> listThingEventDataPage(ThingEventDataPageReqDTO reqDTO) {
        return CommonResult.success(thingDataService.listThingEventDataPage(reqDTO));
    }

    @Operation(summary = "获取设备服务数据列表")
    @GetMapping("service/list")
    CommonResult<PageResult<ThingData>> listThingServiceDataPage(ThingServiceDataPageReqDTO reqDTO) {
        return CommonResult.success(thingDataService.listThingServiceDataPage(reqDTO));
    }

//    @Operation(summary = "列表设备的物模型服务数据")
//    @GetMapping("services/list")
//    PaginationResponseDTO<ThingDataDTO.ServiceDataDTO> listThingServicesData(
//            @RequestParam("thingId") @IdHex24 String thingId,
//            @RequestParam(value = "status", required = false) String status,
//            @RequestParam(value = "startTime", required = false) Long startTime,
//            @RequestParam(value = "endTime", required = false) Long endTime,
//            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
//            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
//        return thingDataService.listThingServicesData(thingId, status, startTime, endTime, pageSize, pageNum);
//    }
//
//    @Operation(summary = "列表设备的命令下发数据")
//    @GetMapping("commands/list")
//    PaginationResponseDTO<ThingData.CommandDTO> listThingCommandData(
//            @RequestParam("thingId") @IdHex24 String thingId,
//            @RequestParam(value = "status", required = false) String status,
//            @RequestParam(value = "startTime", required = false) Long startTime,
//            @RequestParam(value = "endTime", required = false) Long endTime,
//            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
//            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
//        return thingDataService.listThingCommandData(thingId, status, startTime, endTime, pageSize, pageNum);
//    }
//


}
