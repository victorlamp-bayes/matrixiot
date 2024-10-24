package com.victorlamp.matrixiot.service.route.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataDestinationConfigTypeEnum {
    REST_API("REST API"),
    RABBITMQ("RabbitMQ");

    private final String label;
}
