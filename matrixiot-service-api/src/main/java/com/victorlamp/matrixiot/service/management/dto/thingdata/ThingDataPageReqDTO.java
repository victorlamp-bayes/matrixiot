package com.victorlamp.matrixiot.service.management.dto.thingdata;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThingDataPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = -3393927761310310425L;

    private String thingId;
}
