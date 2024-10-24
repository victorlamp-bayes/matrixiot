package com.victorlamp.matrixiot.service.management.dto.thing;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubThingPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = 5946309609386985504L;

    @Schema(description = "搜索关键字，包含字段：id, name")
    private String keywords;

    @Schema(description = "设备在线状态")
    private Boolean online;

    @Schema(description = "网关设备Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @IdHex24
    private String gatewayId;
}
