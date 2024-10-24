package com.victorlamp.matrixiot.service.route.dto;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataRoutePageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = 1532112973856263939L;

    @Schema(description = "搜索关键字，包含字段：id, name, description, status")
    private String keywords;
}
