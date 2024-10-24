package com.victorlamp.matrixiot.service.alarm.controller;

import cn.hutool.core.map.MapUtil;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.enums.EnumUtils;
import com.victorlamp.matrixiot.service.alarm.AlarmService;
import com.victorlamp.matrixiot.service.alarm.config.AlarmEmailProperty;
import com.victorlamp.matrixiot.service.alarm.controller.vo.AlarmUpdateStatusReqVO;
import com.victorlamp.matrixiot.service.alarm.convert.AlarmConvert;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmPageReqDTO;
import com.victorlamp.matrixiot.service.alarm.entity.Alarm;
import com.victorlamp.matrixiot.service.alarm.entity.AlarmStatistics;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmLevelEnum;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmSendStatusEnum;
import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;
import static com.victorlamp.matrixiot.service.alarm.constant.DictTypeConstant.ALARM_LEVEL;
import static com.victorlamp.matrixiot.service.alarm.constant.DictTypeConstant.ALARM_SEND_STATUS;

@OpenAPIDefinition
@Tag(name = "Alarm", description = "告警管理")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/alarm", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AlarmController {
    @Resource
    private AlarmService alarmService;
    @Resource
    private AlarmEmailProperty alarmEmailProperty;

    @Operation(summary = "列表告警")
    @GetMapping("list")
    CommonResult<PageResult<Alarm>> listAlarms(AlarmPageReqDTO reqDTO) {
        PageResult<Alarm> alarmPage = alarmService.listAlarms(reqDTO);
        return success(alarmPage);
    }

    @Operation(summary = "更新告警")
    @PatchMapping("update")
    public CommonResult<Boolean> updateAlarm(@RequestParam("id") @IdHex24 @NotBlank String id, @RequestBody @Valid AlarmUpdateStatusReqVO reqVO) {
        alarmService.updateAlarmStatus(id, AlarmConvert.INSTANCE.toDTO(reqVO));
        return success(true);
    }

    @Operation(summary = "查询告警详情")
    @GetMapping("describe")
    CommonResult<Alarm> describeAlarm(@RequestParam("id") @IdHex24 @NotBlank(message = "id 不能为空") String id) {
        Alarm alarm = alarmService.getAlarm(id);
        return success(alarm);
    }

    @Operation(summary = "删除告警")
    @DeleteMapping("/delete/{id}")
    CommonResult<Boolean> deleteAlarm(@PathVariable("id") @IdHex24 @NotBlank(message = "id 不能为空") String id) {
        alarmService.deleteAlarm(id);
        return success(true);
    }

    @Operation(summary = "批量删除告警")
    @DeleteMapping("/delete")
    CommonResult<Integer> deleteAlarms(@Parameter(name = "要删除的告警 ID 集合", description = "至少需要 1 个 ID") @RequestBody @Size(min = 1, message = "至少删除1个") HashSet<String> idSet) {
        Integer successCount = alarmService.deleteAlarms(idSet);
        return success(successCount);
    }

    @Operation(summary = "获取告警数据词典")
    @GetMapping("dict-data/list")
    CommonResult<Map<String, List<Object>>> listDictData() {
        Map<String, List<Object>> map = MapUtil.newHashMap();
        map.put(ALARM_LEVEL, EnumUtils.listEnumValue(AlarmLevelEnum.class));
        map.put(ALARM_SEND_STATUS, EnumUtils.listEnumValue(AlarmSendStatusEnum.class));
        return success(map);
    }

    @Operation(summary = "获取告警统计信息")
    @GetMapping("statistics")
    CommonResult<AlarmStatistics> getAlarmStatistics() {
        return success(alarmService.getAlarmStatistics());
    }

    // ========== V1.0_Old_Controller ========== //

//    @Operation(summary = "列表告警")
//    @GetMapping("list")
//    CommonResult<PageResult<OldAlarm>> listOldAlarms(OldAlarmPageReqDTO reqDTO) {
//        PageResult<OldAlarm> alarmPageResult = alarmService.listOldAlarms(reqDTO);
//        return success(alarmPageResult);
//    }
//
//    @Operation(summary = "查询告警详情")
//    @GetMapping("describe")
//    CommonResult<OldAlarm> describeOldAlarm(@RequestParam("id") @IdHex24 @NotBlank(message = "id 不能为空") String id) {
//        OldAlarm oldAlarm = alarmService.getOldAlarm(id);
//        return success(oldAlarm);
//    }
//
//    @Operation(summary = "删除告警")
//    @DeleteMapping("/delete/{id}")
//    CommonResult<Boolean> deleteOldAlarm(@PathVariable("id") @IdHex24 @NotBlank(message = "id 不能为空") String id) {
//        alarmService.deleteOldAlarm(id);
//        return success(true);
//    }
//
//    /**
//     * 删除 Id 在 idSet 的告警数据
//     *
//     * @param idSet
//     * @return: java.lang.Integer 删除的数量
//     * @author: Dylan-孙林
//     * @Date: 2023/10/18
//     */
//    @Operation(summary = "批量删除告警")
//    @DeleteMapping("/delete")
//    CommonResult<Integer> deleteOldAlarms(@Parameter(name = "要删除的告警 ID 集合", description = "至少需要 1 个 ID") @RequestBody @Size(min = 1, message = "至少删除1个") HashSet<String> idSet) {
//        Integer successCount = alarmService.deleteOldAlarms(idSet);
//        return success(successCount);
//    }

    @Operation(hidden = true)
    @RequestMapping("/test-send-mail")
    public void email() throws Exception {

        Boolean apiEnabled = alarmEmailProperty.getTestApiEnabled();

        if (!apiEnabled) {

            throw new ServiceException(ServiceException.ExceptionType.INVALID_REQUEST, ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST));
        }

//        Alarm alarm = new Alarm();
//
//        alarm.setId("1");
//        alarm.setAlarmMethod(AlarmMethod.EMAIL.getCode());
//        alarm.setAlarmLevel(1);
//        alarm.setAlarmContacts(CollUtil.newLinkedHashSet("yinghuozhipin@126.com", "sunlin@hxiswater.com"));
//        alarm.setAlarmTime(System.currentTimeMillis());
//        alarm.setAlarmStatus(AlarmStatus.TO_BE_CONFIRMED.getCode());
//
//        msgUtil.asyncSendMessage(alarm);
    }
}
