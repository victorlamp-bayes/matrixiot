package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelEvent;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelEventCreateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3832751023476093115L;

    private String productId;
    private ThingModelEvent event;
}
