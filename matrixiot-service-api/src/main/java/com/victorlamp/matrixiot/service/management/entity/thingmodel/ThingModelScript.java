package com.victorlamp.matrixiot.service.management.entity.thingmodel;

import com.victorlamp.matrixiot.service.management.enums.ThingModelScriptTypeEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingModelScript implements Serializable {
    @Serial
    private static final long serialVersionUID = -1395382495466668138L;

    private ThingModelScriptTypeEnum type;
    private String content;
}
