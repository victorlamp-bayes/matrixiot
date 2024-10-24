package com.victorlamp.matrixiot.service.metric.controller;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.service.metric.MetricService;
import com.victorlamp.matrixiot.service.metric.controller.vo.MetricDataReqVO;
import com.victorlamp.matrixiot.service.metric.controller.vo.SystemMetricDataRespVO;
import com.victorlamp.matrixiot.service.metric.convert.MetricDataConvert;
import com.victorlamp.matrixiot.service.metric.entity.SystemMetricData;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@OpenAPIDefinition
@Tag(name = "实时监控 - 监控数据")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/metric-data", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class MetricDataController {

    @Resource
    private MetricService metricService;

    @Operation(summary = "获取系统监控数据")
    @GetMapping("/list")
    CommonResult<List<SystemMetricDataRespVO>> listSystemMetricData(MetricDataReqVO reqVO) {
        List<SystemMetricData> list = metricService.listSystemMetricData(MetricDataConvert.INSTANCE.toDTO(reqVO));
        return success(MetricDataConvert.INSTANCE.toVO(list));
    }
}
