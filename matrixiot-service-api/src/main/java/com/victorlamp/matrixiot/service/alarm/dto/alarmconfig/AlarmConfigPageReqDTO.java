package com.victorlamp.matrixiot.service.alarm.dto.alarmconfig;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmConfigPageReqDTO extends PageParam {

    @Serial
    private static final long serialVersionUID = 5946309630569985504L;

    @Schema(description = "搜索关键字，包含字段：id, 产品名称, 描述, 联系人, 电话")
    private String keywords;

    @Schema(description = "告警等级")
    private String level;
}
