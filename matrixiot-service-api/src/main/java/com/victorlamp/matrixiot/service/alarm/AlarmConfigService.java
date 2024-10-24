package com.victorlamp.matrixiot.service.alarm;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigCreateReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigPageReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigUpdateDTO;
import com.victorlamp.matrixiot.service.alarm.entity.AlarmConfig;

public interface AlarmConfigService {
    AlarmConfig createAlarmConfig(AlarmConfigCreateReqDTO alarmConfigCreateReqDTO);

    PageResult<AlarmConfig> listAlarmConfigs(AlarmConfigPageReqDTO reqDTO);

    AlarmConfig getAlarmConfig(String id);

    void updateAlarmConfig(String id, AlarmConfigUpdateDTO alarmConfigUpdateDTO);

    void deleteAlarmConfig(String id);
}
