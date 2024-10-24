package com.victorlamp.matrixiot.service.alarm.dto;

import com.victorlamp.matrixiot.service.alarm.enums.AlarmMethod;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmScene;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmStatus;
import com.victorlamp.matrixiot.service.alarm.enums.SendingStatus;
import com.victorlamp.matrixiot.service.management.dto.thing.SimpleThing;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class OldAlarmDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6048304050006776614L;

    private String alarmId;

    @Size(min = 1, message = "至少需要一个设备")
    @Valid
    @Schema(name = "设备集合", description = "新增时不传；更新时必传；")
    private LinkedHashSet<SimpleThing> things;

    @Schema(name = "告警发生时刻时间戳")
    private Long alarmTime;

    @NotBlank(message = "告警等级不能为空")
    @Schema(name = "告警等级")
    private Integer alarmLevel;

    @Schema(name = "告警场景", description = "枚举值：\nDEFAULT")
    private AlarmScene alarmScene;

    @Schema(name = "告警状态", description = "枚举值：\n0-待确认；\n1-已确认；")
    private AlarmStatus alarmStatus;

    @Schema(name = "告警消息发送状态", description = "枚举值：0-待发送；1-已发送（成功）；2-发送失败；3-取消发送")
    private SendingStatus sendingStatus;

    @NotBlank(message = "规则ID不能为空")
    @Schema(name = "场景联动 ID")
    private String ruleId;

    @NotBlank(message = "规则名不能为空")
    @Schema(name = "场景联动名称")
    private String ruleName;

    @Size(min = 1, message = "至少需要一个发送目的地")
    @Schema(name = "消息发送目的集合", description = "当 alarmMethod = NULL 时，无需发送目的；\n当 alarmMethod in [SMS, EMAIL] 时，必须设置发送目的地")
    private Set<String> alarmContacts;

    @NotNull(message = "告警方法不能为空")
    @Schema(name = "告警消息渠道", description = "枚举值：\nNULL-站内消息；\nSMS-短信；\nEMAIL-邮件；\nWEBHOOK-自定义回调")
    private AlarmMethod alarmMethod;
}