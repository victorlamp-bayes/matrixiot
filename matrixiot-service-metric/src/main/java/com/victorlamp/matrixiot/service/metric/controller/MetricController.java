package com.victorlamp.matrixiot.service.metric.controller;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.enums.EnumUtils;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.metric.MetricService;
import com.victorlamp.matrixiot.service.metric.dto.MetricCreateReqDTO;
import com.victorlamp.matrixiot.service.metric.dto.MetricPageReqDTO;
import com.victorlamp.matrixiot.service.metric.entity.Metric;
import com.victorlamp.matrixiot.service.metric.enums.AggregationFreqEnum;
import com.victorlamp.matrixiot.service.metric.enums.AggregationTypeEnum;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@OpenAPIDefinition
@Tag(name = "实时监控 - 监控项管理")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/metric", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class MetricController {

    @Resource
    private MetricService metricService;

    @Operation(summary = "创建度量指标")
    @PostMapping("create")
    CommonResult<Metric> createMetric(@RequestBody @Valid MetricCreateReqDTO reqDTO) {
        return success(metricService.createMetric(reqDTO));
    }

    @Operation(summary = "删除度量指标")
    @DeleteMapping("delete")
    CommonResult<Boolean> deleteMetric(@RequestParam @IdHex24 @NotBlank String id) {
        metricService.deleteMetric(id);
        return success(true);
    }

    @Operation(summary = "获取度量指标分页列表")
    @GetMapping("list")
    CommonResult<PageResult<Metric>> listMetricPage(MetricPageReqDTO reqDTO) {
        return success(metricService.listMetricPage(reqDTO));
    }

    @Operation(summary = "获取聚合类型列表")
    @GetMapping("/aggregation-type/list")
    CommonResult<List<Object>> listAggregationType() {
        return success(EnumUtils.listEnumValue(AggregationTypeEnum.class));
    }

    @Operation(summary = "获取聚合频率列表")
    @GetMapping("/aggregation-freq/list")
    CommonResult<List<Object>> listAggregationFreq() {
        return success(EnumUtils.listEnumValue(AggregationFreqEnum.class));
    }

}
