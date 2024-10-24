package com.victorlamp.matrixiot.service.management.controller.thing.vo;

import com.victorlamp.matrixiot.service.common.constant.ParamPattern;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.management.entity.thing.GeoPoint;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingExternalConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ThingUpdateReqVO {

    @Schema(description = "设备Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备Id不能为空")
    @IdHex24
    private String id;

    @Schema(description = "设备名称")
    @Size(min = 2, max = 32, message = "设备名称长度为2-32个字符")
    @Pattern(regexp = ParamPattern.NAME, message = ParamPattern.NAME_MESSAGE)
    private String name;

    @Schema(description = "设备描述")
    @Size(max = 128, message = "设备描述最大长度为128个字符")
    private String description;

    @Schema(description = "设备地理位置(经纬度坐标)")
    private GeoPoint position;

    @Schema(description = "设备启用状态")
    private Boolean enabled;

    @Schema(description = "设备在线状态")
    private Boolean online;

    @Schema(description = "设备标签")
    private List<String> tags;

    @Schema(description = "设备外部配置")
    private ThingExternalConfig externalConfig;
}
