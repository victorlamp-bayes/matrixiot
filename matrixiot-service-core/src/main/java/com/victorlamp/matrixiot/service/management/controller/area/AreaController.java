package com.victorlamp.matrixiot.service.management.controller.area;

import com.alibaba.fastjson2.JSONObject;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.service.management.service.area.AreaService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@OpenAPIDefinition
@Tag(name = "设备管理 - 区域管理")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/area", produces = MediaType.APPLICATION_JSON_VALUE)
public class AreaController {

    @Resource
    private AreaService areaService;

    @Operation(summary = "获取行政区划范围GeoJson")
    @GetMapping("bound/geojson")
    CommonResult<JSONObject> getBound(@RequestParam("code") @NotNull int code) {
        JSONObject bound = areaService.getAreaBound(code);
        return success(bound);
    }
}
