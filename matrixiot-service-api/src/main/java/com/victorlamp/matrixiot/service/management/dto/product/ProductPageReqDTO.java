package com.victorlamp.matrixiot.service.management.dto.product;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageReqDTO extends PageParam {

    @Serial
    private static final long serialVersionUID = 2397496889523646857L;

    @Schema(description = "搜索关键字，包含字段：id, name, description, manufacturer, model")
    private String keywords;

    @Schema(description = "产品发布状态")
    private Boolean published;

    @Schema(description = "产品节点类型，参见 NodeTypeEnum 枚举类")
    private String nodeType;

    @Schema(description = "产品联网方式，参见 NetTypeEnum 枚举类")
    private String netType;

    @Schema(description = "产品分类")
    private String category;
}
