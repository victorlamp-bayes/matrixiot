package com.victorlamp.matrixiot.service.management.dto.thing;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThingPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = 4285470238578288368L;

    @Schema(description = "搜索关键字，包含字段：id, name")
    private String keywords;

//    @Schema(description = "设备节点类型，参见 NodeTypeEnum 枚举类")
//    private String nodeType;

    @Schema(description = "设备启用状态")
    private Boolean enabled;

    @Schema(description = "设备在线状态")
    private Boolean online;

//    @Schema(description = "设备状态，参见 ThingStatusEnum 枚举类")
//    @InEnum(value = ThingStatusEnum.class, message = "设备状态必须是 {value}")
//    private Integer status;

    @Schema(description = "设备所属产品")
    private String productId;
}
