package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelEvent;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelEventUpdateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3443040461217341762L;

    private String productId;
    private String identifier;
    private ThingModelEvent event;
}
