package com.victorlamp.matrixiot.service.management.entity.thingmodel;

import com.victorlamp.matrixiot.service.management.enums.ThingModelEventTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThingModelEvent extends ThingModelBase {
    @Serial
    private static final long serialVersionUID = 3959256807909166775L;

    private ThingModelEventTypeEnum type;
    private Boolean required;
    private String data;
}
