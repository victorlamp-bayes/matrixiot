package com.victorlamp.matrixiot.service.route.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSource implements Serializable {
    @Serial
    private static final long serialVersionUID = -5764047197554737353L;

    private Integer topicId;
    private String productId;
    private String thingId;
    private String description;
}
