package com.victorlamp.matrixiot.service.management.controller.thing.vo;

import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ThingUpdateStatusReqVO {

    @Schema(description = "设备Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备Id不能为空")
    @IdHex24
    private String id;

    @Schema(description = "设备启用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "设备启用状态不能为空")
    private Boolean enabled;
}
