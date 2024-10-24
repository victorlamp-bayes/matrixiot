package com.victorlamp.matrixiot.service.management.dto.product;

import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "设备管理 - 产品更新发布状态 Request DTO")
@Data
public class ProductUpdateStatusReqDTO {
    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "产品ID不能为空")
    @IdHex24
    private String id;

    @Schema(description = "发布状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "发布状态不能为空")
//    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Boolean published;
}
