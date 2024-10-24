package com.victorlamp.matrixiot.service.alarm.controller;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.alarm.AlarmConfigService;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigCreateReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigPageReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigUpdateDTO;
import com.victorlamp.matrixiot.service.alarm.entity.AlarmConfig;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@OpenAPIDefinition
@Tag(name = "AlarmConfig", description = "告警配置管理")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/alarm-config", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AlarmConfigController {

    @Resource
    private AlarmConfigService alarmConfigService;

    @Operation(summary = "创建告警配置")
    @PostMapping("create")
    CommonResult<AlarmConfig> createAlarmConfig(@RequestBody @Valid AlarmConfigCreateReqDTO reqDTO) {
        AlarmConfig alarmConfigResult = alarmConfigService.createAlarmConfig(reqDTO);
        return success(alarmConfigResult);
    }

    @Operation(summary = "告警配置列表")
    @GetMapping("list")
    CommonResult<PageResult<AlarmConfig>> listAlarmConfigs(AlarmConfigPageReqDTO reqDTO) {
        PageResult<AlarmConfig> alarmConfigDTOPageResult = alarmConfigService.listAlarmConfigs(reqDTO);
        return success(alarmConfigDTOPageResult);
    }

    @Operation(summary = "查询告警配置")
    @GetMapping("describe")
    CommonResult<AlarmConfig> describeNewAlarm(@RequestParam("id") @IdHex24 @NotBlank(message = "id 不能为空") String id) {
        AlarmConfig alarmConfig = alarmConfigService.getAlarmConfig(id);
        return success(alarmConfig);
    }

    @Operation(summary = "更新告警配置")
    @PatchMapping("update")
    public CommonResult<Boolean> updateAlarmConfig(@RequestParam("id") @IdHex24 @NotBlank String id,
                                                   @RequestBody @Valid AlarmConfigUpdateDTO reqDTO) {
        alarmConfigService.updateAlarmConfig(id, reqDTO);
        return success(true);
    }

    @Operation(summary = "删除告警配置")
    @DeleteMapping("/delete")
    CommonResult<Boolean> deleteAlarmConfig(@RequestParam("id") @IdHex24 @NotBlank(message = "id 不能为空") String id) {
        alarmConfigService.deleteAlarmConfig(id);
        return success(true);
    }


}
