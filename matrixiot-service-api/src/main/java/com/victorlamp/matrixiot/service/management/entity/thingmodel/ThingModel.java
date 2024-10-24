package com.victorlamp.matrixiot.service.management.entity.thingmodel;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Document
public class ThingModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -667587195266835971L;

    @Id
    private String id;

    private List<ThingModelProperty> properties;
    private List<ThingModelService> services;
    private List<ThingModelEvent> events;
    private ThingModelScript script;

    private Long deletedAt;
}
