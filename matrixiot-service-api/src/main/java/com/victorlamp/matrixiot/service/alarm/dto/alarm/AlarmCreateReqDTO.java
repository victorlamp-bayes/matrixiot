package com.victorlamp.matrixiot.service.alarm.dto.alarm;

import com.victorlamp.matrixiot.service.alarm.enums.AlarmLevelEnum;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmSendStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmCreateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7373056361953658357L;

    private String thingId;
    private String productId;
    private AlarmLevelEnum level = AlarmLevelEnum.INFO;
    private String message;
    private Boolean status;
    private AlarmSendStatusEnum sendStatus = AlarmSendStatusEnum.PENDING;
    private Long timestamp;
}