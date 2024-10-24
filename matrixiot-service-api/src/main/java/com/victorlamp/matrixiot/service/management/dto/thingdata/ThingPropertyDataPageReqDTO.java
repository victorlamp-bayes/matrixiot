package com.victorlamp.matrixiot.service.management.dto.thingdata;

import com.victorlamp.matrixiot.common.pojo.PageParam;
import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThingPropertyDataPageReqDTO extends PageParam {
    @Serial
    private static final long serialVersionUID = 1858725207620520909L;
    
    private String thingId;
}
