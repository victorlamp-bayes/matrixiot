package com.victorlamp.matrixiot.service.management.controller.product.vo;

import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "产品管理 - 产品精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSimpleRespVO {
    @Schema(description = "产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "节点类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nodeType;

    @Schema(description = "外部配置类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private ProductExternalConfig externalConfig;
}
