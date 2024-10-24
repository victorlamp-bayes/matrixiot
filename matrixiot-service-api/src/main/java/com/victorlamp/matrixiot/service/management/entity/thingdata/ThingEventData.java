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
public class ThingEventData implements Serializable {
    @Serial
    private static final long serialVersionUID = -1846098005127119220L;

    private String identifier;
    private String type;
    private String data;
    private Long timestamp;
}
