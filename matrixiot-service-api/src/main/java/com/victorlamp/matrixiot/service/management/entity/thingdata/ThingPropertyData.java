package com.victorlamp.matrixiot.service.management.entity.thingdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThingPropertyData implements Serializable {
    @Serial
    private static final long serialVersionUID = 974770886659408780L;

    private String identifier;
    private String value;
    private Long timestamp;
}
