package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelScript;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelScriptUpdateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8211834445024821891L;
    
    private String productId;
    private ThingModelScript script;
}
