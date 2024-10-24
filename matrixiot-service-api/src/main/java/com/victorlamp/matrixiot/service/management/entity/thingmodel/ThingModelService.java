package com.victorlamp.matrixiot.service.management.entity.thingmodel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThingModelService extends ThingModelBase {
    @Serial
    private static final long serialVersionUID = 5684482901473762039L;

    private String callType;
    private List<ThingModelServiceParam> inputParams;
    private List<ThingModelServiceParam> outputParams;
}
