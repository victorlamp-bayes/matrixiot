package com.victorlamp.matrixiot.service.route.entity;

import com.victorlamp.matrixiot.service.route.enums.DataRouteTransformerTypeEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DataTransformer implements Serializable {
    @Serial
    private static final long serialVersionUID = 6861718620933636798L;

    private DataRouteTransformerTypeEnum type;
    private String content;
}
