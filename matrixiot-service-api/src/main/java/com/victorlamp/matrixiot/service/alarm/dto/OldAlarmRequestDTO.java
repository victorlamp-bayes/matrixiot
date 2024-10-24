package com.victorlamp.matrixiot.service.alarm.dto;

import com.victorlamp.matrixiot.service.alarm.enums.AlarmMethod;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmScene;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmStatus;
import com.victorlamp.matrixiot.service.management.dto.thing.SimpleThing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OldAlarmRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7373056361953658357L;

    @Size(min = 1, message = "至少需要一个设备")
    @Valid
    private LinkedHashSet<SimpleThing> things;

    private Long alarmTime;

    @NotNull(message = "告警等级不能为空")
    private Integer alarmLevel;

    private AlarmScene alarmScene;

    private AlarmStatus alarmStatus;

    @NotBlank(message = "规则ID不能为空")
    private String ruleId;

    @NotBlank(message = "规则名不能为空")
    private String ruleName;

    private Set<String> alarmContacts;

    private AlarmMethod alarmMethod;
}