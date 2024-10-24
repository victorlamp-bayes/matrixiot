package com.victorlamp.matrixiot.service.route.dto;

import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataRouteUpdateStatusReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7501941902059538882L;

    @Schema(description = "数据路由ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据路由ID不能为空")
    @IdHex24
    private String id;

    @Schema(description = "数据路由状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "RUNNING/STOP")
    @NotNull(message = "数据路由状态不能为空")
    private String status;
}
