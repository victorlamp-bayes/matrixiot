package com.victorlamp.matrixiot.service.alarm.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.alarm.AlarmService;
import com.victorlamp.matrixiot.service.alarm.convert.AlarmConvert;
import com.victorlamp.matrixiot.service.alarm.dao.AlarmRepository;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmCreateReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmPageReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmUpdateStatusReqDTO;
import com.victorlamp.matrixiot.service.alarm.entity.Alarm;
import com.victorlamp.matrixiot.service.alarm.entity.AlarmStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.validation.MethodValidated;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.alarm.constant.ErrorCodeConstants.ALARM_NOT_EXISTS;

@DubboService(validation = "true")
@Service("alarmService")
@RequiredArgsConstructor
@Slf4j
public class AlarmServiceImpl implements AlarmService {
    @Resource
    private AlarmRepository alarmRepository;

    @Override
    public PageResult<Alarm> listAlarms(AlarmPageReqDTO reqDTO) {
        return alarmRepository.findPage(reqDTO);
    }

    @Override
    @MethodValidated
    public Alarm createAlarm(AlarmCreateReqDTO reqDTO) {
        if (reqDTO.getTimestamp() == null || reqDTO.getTimestamp() == 0L) {
            reqDTO.setTimestamp(DateUtil.current());
        }

        // TODO 发送告警通知

        return alarmRepository.insert(AlarmConvert.INSTANCE.toEntity(reqDTO));
    }

    @Override
    @MethodValidated
    public void updateAlarmStatus(String id, AlarmUpdateStatusReqDTO reqDTO) {
        Alarm alarm = alarmRepository.findById(id).orElse(null);
        if (ObjUtil.isNull(alarm)) {
            throw exception(ALARM_NOT_EXISTS);
        }

        if (ObjUtil.isNotNull(reqDTO.getStatus()) && ObjUtil.notEqual(reqDTO.getStatus(), alarm.getStatus())) {
            alarm.setStatus(reqDTO.getStatus());
            alarmRepository.save(alarm);
        }

        // TODO 更新告警发送状态
    }

    @Override
    public Alarm getAlarm(String id) {
        return alarmRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteAlarm(String id) {
        alarmRepository.deleteById(id);
    }

    @Override
    public Integer deleteAlarms(HashSet<String> idSet) {

        int successCount = 0;

        if (CollectionUtils.isEmpty(idSet)) {
            return successCount;
        }

        for (String id : idSet) {
            try {
                deleteAlarm(id);
                successCount++;
            } catch (Exception e) {
                log.warn("删除告警失败，id=[{}],原因：{}", id, e.getMessage());
            }
        }

        if (successCount != idSet.size()) {
            log.warn("批量删除告警，传入id数={},成功删除数={}", idSet.size(), successCount);
        }

        return successCount;
    }

    @Override
    public AlarmStatistics getAlarmStatistics() {
        AlarmPageReqDTO reqDTO = new AlarmPageReqDTO();
        int total = (int) alarmRepository.count(reqDTO);

        // 今日新增:存储今天零点开始到当前时间的时间范围;
        List<Long> timeRange = new ArrayList<>();
        ZoneId shanghaiZone = ZoneId.of("Asia/Shanghai");
        long startTime = LocalDate.now(shanghaiZone).atStartOfDay(shanghaiZone).toInstant().toEpochMilli();
        long endTime = Instant.now().toEpochMilli();
        timeRange.add(startTime);
        timeRange.add(endTime);
        reqDTO.setTimeRange(timeRange);
        int newAdd = (int) alarmRepository.count(reqDTO);

        //待处理告警总数;
        reqDTO.setStatus(false);
        reqDTO.setTimeRange(null);
        int pending = (int) alarmRepository.count(reqDTO);

        return new AlarmStatistics(total, newAdd, pending);
    }

}