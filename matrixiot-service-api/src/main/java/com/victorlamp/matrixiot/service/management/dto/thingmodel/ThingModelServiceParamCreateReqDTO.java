package com.victorlamp.matrixiot.service.management.dto.thingmodel;

import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelServiceParam;
import com.victorlamp.matrixiot.service.management.enums.ThingModelServiceParamDirectionEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelServiceParamCreateReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7584675801280372837L;
    private String productId;
    private String serviceIdentifier;
    private ThingModelServiceParam serviceParam;
    private ThingModelServiceParamDirectionEnum paramDirection;
}
