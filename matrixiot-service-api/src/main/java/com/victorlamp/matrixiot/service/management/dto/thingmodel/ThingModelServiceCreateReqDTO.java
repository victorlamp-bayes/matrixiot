package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelService;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelServiceCreateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3452091576621908204L;

    private String productId;
    private ThingModelService service;
}
