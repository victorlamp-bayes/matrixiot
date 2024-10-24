package com.victorlamp.matrixiot.service.alarm;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmCreateReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmPageReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmUpdateStatusReqDTO;
import com.victorlamp.matrixiot.service.alarm.entity.Alarm;
import com.victorlamp.matrixiot.service.alarm.entity.AlarmStatistics;

import java.util.HashSet;

public interface AlarmService {

    PageResult<Alarm> listAlarms(AlarmPageReqDTO reqDTO);

    Alarm createAlarm(AlarmCreateReqDTO reqDTO);

    void updateAlarmStatus(String id, AlarmUpdateStatusReqDTO reqDTO);

    Alarm getAlarm(String id);

    void deleteAlarm(String id);

    Integer deleteAlarms(HashSet<String> idSet);

    AlarmStatistics getAlarmStatistics();

    // ========== V1.0_Old_Service ========== //
//    PageResult<OldAlarm> listOldAlarms(OldAlarmPageReqDTO reqDTO);
//
//    /**
//     * 新增告警
//     *
//     * @param oldAlarmRequestDTO
//     * @return: com.victorlamp.matrixiot.service.common.response.ResponseDTO<com.victorlamp.matrixiot.service.alarm.dto.Alarm.AlarmDTO>
//     * @author: Dylan-孙林
//     * @Date: 2023/12/6
//     */
//    OldAlarm createOldAlarm(OldAlarmRequestDTO oldAlarmRequestDTO);
//
//    OldAlarm getOldAlarm(String id);
//
//    /**
//     * 删除 id 匹配的告警
//     *
//     * @param id
//     * @return: java.lang.Boolean 删除成功与否
//     * @author: Dylan-孙林
//     * @Date: 2023/10/18
//     */
//    void deleteOldAlarm(String id);
//
//    /**
//     * 删除 Id 在 idSet 的告警数据
//     *
//     * @param idSet
//     * @return: java.lang.Integer 删除的数量
//     * @author: Dylan-孙林
//     * @Date: 2023/10/18
//     */
//    Integer deleteOldAlarms(HashSet<String> idSet);
}