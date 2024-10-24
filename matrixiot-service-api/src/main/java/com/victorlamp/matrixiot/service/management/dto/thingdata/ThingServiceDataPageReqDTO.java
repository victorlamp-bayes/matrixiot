package com.victorlamp.matrixiot.service.management.dto.thingdata;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingServiceDataPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = -8908377302451394636L;

    private String thingId;
}
