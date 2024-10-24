package com.victorlamp.matrixiot.service.alarm.dto.alarmconfig;

import com.victorlamp.matrixiot.service.alarm.entity.ContactInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AlarmConfigUpdateDTO {

    @Schema(description = "告警配置描述")
    private String description;

    @Schema(description = "告警等级")
    private String level;

    @Schema(description = "联系方式")
    private List<ContactInfo> contacts;
}
