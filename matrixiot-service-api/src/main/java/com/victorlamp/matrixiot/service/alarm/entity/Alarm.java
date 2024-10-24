package com.victorlamp.matrixiot.service.alarm.entity;

import com.victorlamp.matrixiot.service.alarm.enums.AlarmLevelEnum;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmSendStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Alarm implements Serializable {
    @Serial
    private static final long serialVersionUID = -7669745586530071357L;

    @Id
    private String id;
    private String thingId;
    private String productId;
    private Long timestamp;
    private AlarmLevelEnum level;
    private String message;
    private Boolean status = false; // 告警确认状态
    private AlarmSendStatusEnum sendStatus; // 告警发送状态
    @CreatedDate
    private Long createdAt;
    @LastModifiedDate
    private Long updatedAt;
    private Long deletedAt;
}
