package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class ThingModelUpdateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2456865398630300076L;

    @Schema(description = "产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "产品Id不能为空")
    @IdHex24
    private String productId;

    @NotNull
    private ThingModel thingModel;
}
