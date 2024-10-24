package com.victorlamp.matrixiot.service.management.dto.product;

import com.victorlamp.matrixiot.service.common.constant.ParamPattern;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
public class ProductUpdateReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5572061423029586231L;

    @Schema(description = "产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "产品Id不能为空")
    @IdHex24
    private String id;

    @Schema(description = "产品名称")
    @Size(min = 2, max = 32, message = "产品名称长度为2-32个字符")
    @Pattern(regexp = ParamPattern.NAME, message = ParamPattern.NAME_MESSAGE)
    private String name;

    @Schema(description = "产品描述", example = "描述")
    @Size(max = 128, message = "产品描述最大长度为128个字符")
    private String description;

    @Schema(description = "产品图片", example = "https://www.zoniot.com/xxx.png")
    private String icon;

    @Schema(description = "产品发布状态")
    private Boolean published;

    @Schema(description = "产品标签")
    private Set<String> tags;

    @Schema(description = "节点类型")
    private String nodeType;

    @Schema(description = "连网方式")
    private String netType;

    @Schema(description = "连网配置")
    private ProductExternalConfig externalConfig;

    @Schema(description = "子设备连接网关协议")
    private String protocolType;

    @Schema(description = "数据格式")
    private String dataFormat;

    @Schema(description = "产品制造商")
    private String manufacturer;

    @Schema(description = "产品型号")
    private String model;

    @Schema(description = "产品品类")
    private String category;
}
