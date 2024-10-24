package com.victorlamp.matrixiot.service.management.dto.thing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.victorlamp.matrixiot.service.common.constant.ParamPattern;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.management.entity.thing.GeoPoint;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingExternalConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThingCreateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 559823292239194429L;

    @Schema(description = "产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @IdHex24
    private String productId;

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备名称不能为空")
    @Size(min = 2, max = 32, message = "设备名称长度为2-32个字符")
    @Pattern(regexp = ParamPattern.NAME, message = ParamPattern.NAME_MESSAGE)
    private String name;

    @Schema(description = "设备描述")
    @Size(max = 128, message = "设备描述最大长度为128个字符")
    private String description;

    @Schema(description = "设备位置(经纬度坐标)")
    private GeoPoint position;

    @Schema(description = "设备启用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "设备启用状态不能为空")
    private Boolean enabled;

    @Schema(description = "设备在线状态", defaultValue = "false")
    @Builder.Default
    private Boolean online = false;

//    @Schema(description = "设备状态，参见 ThingStatusEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "状态不能为空")
//    @InEnum(value = ThingStatusEnum.class, message = "设备状态必须是 {value}")
//    private Integer status;

    @Schema(description = "子设备连接的网关设备Id")
    @IdHex24
    private String gatewayId;

    @Schema(description = "设备标签")
    private List<String> tags;

    @Schema(description = "设备外部配置")
    private ThingExternalConfig externalConfig;
}
