package com.victorlamp.matrixiot.service.management.entity.thingmodel;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public abstract class ThingModelBase implements Serializable {
    @Serial
    private static final long serialVersionUID = -800915350313992147L;
    
    private String identifier;
    private String name;
}
