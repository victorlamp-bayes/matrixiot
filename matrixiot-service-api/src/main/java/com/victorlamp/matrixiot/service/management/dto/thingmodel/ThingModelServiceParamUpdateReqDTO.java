package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelServiceParam;
import com.victorlamp.matrixiot.service.management.enums.ThingModelServiceParamDirectionEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelServiceParamUpdateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8544804655480931598L;
    private String productId;
    private String serviceIdentifier;
    private String paramIdentifier;
    private ThingModelServiceParam serviceParam;
    private ThingModelServiceParamDirectionEnum paramDirection;
}
