package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelPropertyUpdateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8755612081496049691L;

    @Schema(description = "产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "产品Id不能为空")
    @IdHex24
    private String productId;

    @Schema(description = "属性标识符", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "属性标识符不能为空")
    private String identifier;

    @NotNull
    private ThingModelProperty property;
}
