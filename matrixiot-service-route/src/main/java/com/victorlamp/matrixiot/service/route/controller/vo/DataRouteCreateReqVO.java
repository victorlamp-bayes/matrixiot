package com.victorlamp.matrixiot.service.route.controller.vo;

import com.victorlamp.matrixiot.service.common.constant.ParamPattern;
import com.victorlamp.matrixiot.service.route.dto.DataDestinationCreateDTO;
import com.victorlamp.matrixiot.service.route.dto.DataSourceCreateDTO;
import com.victorlamp.matrixiot.service.route.entity.DataTransformer;
import com.victorlamp.matrixiot.service.route.enums.DataRouteStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Schema(description = "数据路由 - 路由创建 Request VO")
@Data
public class DataRouteCreateReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7498349957142171972L;

    @Schema(description = "数据路由名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "数据路由")
    @NotBlank(message = "数据路由名称不能为空")
    @Size(min = 2, max = 32, message = "数据路由名称长度为2-32个字符")
    @Pattern(regexp = ParamPattern.NAME, message = ParamPattern.NAME_MESSAGE)
    private String name;

    @Schema(description = "数据路由描述", example = "描述")
    @Size(max = 128, message = "数据路由描述长度不能超过128位")
    private String description;

    @Schema(description = "数据源")
    @Valid
    private DataSourceCreateDTO source;

    @Schema(description = "数据目的")
    @Valid
    private DataDestinationCreateDTO destination;

    @Schema(description = "数据路由转换脚本")
    private DataTransformer transformer;

    @Schema(description = "数据路由状态", example = "RUNNING/STOP") // ABNORMAL状态由后台处理，前端无法修改为ABNORMAL
    private DataRouteStatusEnum status = DataRouteStatusEnum.STOP;
}
