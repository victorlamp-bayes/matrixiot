package com.victorlamp.matrixiot.service.route.entity;

import com.victorlamp.matrixiot.service.route.enums.DataDestinationConfigTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDestination implements Serializable {
    @Serial
    private static final long serialVersionUID = -8307851928158160312L;

    private DataDestinationConfigTypeEnum type;
    private DataDestinationConfig config;
    private String description;
}
