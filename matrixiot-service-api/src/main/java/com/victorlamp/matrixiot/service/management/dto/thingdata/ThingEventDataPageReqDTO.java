package com.victorlamp.matrixiot.service.management.dto.thingdata;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThingEventDataPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = 5644276834654786391L;

    private String thingId;
}
