package com.victorlamp.matrixiot.service.route.dto;

import com.victorlamp.matrixiot.service.common.constant.ParamPattern;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.route.entity.DataTransformer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
public class DataRouteUpdateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7690488833875878906L;

    @Schema(description = "数据路由Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据路由Id不能为空")
    @IdHex24
    private String id;

    @Schema(description = "数据路由名称", example = "数据路由")
    @Size(min = 2, max = 32, message = "数据路由名称长度为2-32个字符")
    @Pattern(regexp = ParamPattern.NAME, message = ParamPattern.NAME_MESSAGE)
    private String name;

    @Schema(description = "数据路由描述", example = "描述")
    @Size(max = 128, message = "数据路由描述长度不能超过128位")
    private String description;

    @Schema(description = "数据源")
    @Valid
    private DataSourceUpdateDTO source;

    @Schema(description = "数据目的")
    @Valid
    private DataDestinationUpdateDTO destination;

    @Schema(description = "数据路由转换脚本")
    private DataTransformer transformer;
}
