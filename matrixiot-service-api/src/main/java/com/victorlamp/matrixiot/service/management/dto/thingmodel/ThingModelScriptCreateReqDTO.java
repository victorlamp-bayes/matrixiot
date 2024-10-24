package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelScript;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelScriptCreateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8212803319857425513L;

    @Schema(description = "产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "产品Id不能为空")
    @IdHex24
    private String productId;
    
    private ThingModelScript script;
}
