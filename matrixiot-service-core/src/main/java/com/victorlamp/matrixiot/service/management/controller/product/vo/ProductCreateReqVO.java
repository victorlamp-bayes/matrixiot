package com.victorlamp.matrixiot.service.management.controller.product.vo;

import com.victorlamp.matrixiot.service.common.constant.ParamPattern;
import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Schema(description = "产品管理 - 产品创建 Request VO")
@Data
public class ProductCreateReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5665801066497902814L;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "产品")
    @NotBlank(message = "产品名称不能为空")
    @Size(min = 2, max = 32, message = "产品名称长度为2-32个字符")
    @Pattern(regexp = ParamPattern.NAME, message = ParamPattern.NAME_MESSAGE)
    private String name;

    @Schema(description = "产品描述", example = "描述")
    @Size(max = 128, message = "产品描述最大长度为128个字符")
    private String description;

    @Schema(description = "产品图片", example = "https://www.zoniot.com/xxx.png")
    private String icon;

    @Schema(description = "产品发布状态", example = "true", defaultValue = "false")
    private Boolean published = false;

    @Schema(description = "产品标签")
    private Set<String> tags;

    @Schema(description = "节点类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nodeType;

    @Schema(description = "连网方式")
    private String netType;

    @Schema(description = "连网配置")
    private ProductExternalConfig externalConfig;

    @Schema(description = "子设备连接网关协议")
    private String protocolType;

    @Schema(description = "数据格式")
    private String dataFormat;

    @Schema(description = "设备制造商")
    private String manufacturer;

    @Schema(description = "设备型号")
    private String model;

    @Schema(description = "设备分类")
    private String category;
}
