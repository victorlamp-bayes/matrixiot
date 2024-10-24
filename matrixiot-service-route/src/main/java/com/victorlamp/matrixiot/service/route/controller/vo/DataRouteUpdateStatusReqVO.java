package com.victorlamp.matrixiot.service.route.controller.vo;

import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Schema(description = "数据路由 - 数据路由更新状态 Request VO")
@Data
public class DataRouteUpdateStatusReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4363740648085043671L;

    @Schema(description = "数据路由ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据路由ID不能为空")
    @IdHex24
    private String id;

    @Schema(description = "数据路由状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "RUNNING/STOP/ABNORMAL")
    @NotNull(message = "数据路由状态不能为空")
    private String status;
}
