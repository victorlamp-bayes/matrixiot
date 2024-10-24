package com.victorlamp.matrixiot.service.alarm.dto.alarmconfig;

import com.victorlamp.matrixiot.service.alarm.entity.ContactInfo;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmConfigCreateReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7669749456861071357L;

    @Schema(description = "产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "产品Id不能为空")
    @IdHex24
    private String productId;

    @NotBlank(message = "告警级别不能为空")
    private String level;

    private String description;

    private List<ContactInfo> contacts;
}
