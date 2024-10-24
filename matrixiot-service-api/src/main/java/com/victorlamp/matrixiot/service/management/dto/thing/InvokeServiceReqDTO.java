package com.victorlamp.matrixiot.service.management.dto.thing;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvokeServiceReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7631175206490564413L;

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备编号不能为空")
    private String deviceNo;

    @Schema(description = "服务调用标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "服务调用标识不能为空")
    private String identifier;

    @Schema(description = "服务调用数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Map<String, Object>> inputParams;
}
