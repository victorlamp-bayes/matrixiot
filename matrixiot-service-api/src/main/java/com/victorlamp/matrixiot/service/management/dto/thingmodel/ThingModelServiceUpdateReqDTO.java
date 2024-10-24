package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelService;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelServiceUpdateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 289867243698108059L;

    private String productId;
    private String identifier;
    private ThingModelService service;
}
