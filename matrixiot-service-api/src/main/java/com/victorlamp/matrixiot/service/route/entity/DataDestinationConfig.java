package com.victorlamp.matrixiot.service.route.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.victorlamp.matrixiot.service.route.constant.DestinationConfigType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DataDestinationConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -7631930403765974420L;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonTypeName(DestinationConfigType.REST_API)
    public static class RestApi extends DataDestinationConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -8747998616363759548L;

        private String url;
        private String method;
        private String verifyCode;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonTypeName(DestinationConfigType.RABBITMQ)
    public static class RabbitMQ extends DataDestinationConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -3495045241415062345L;

        private String host;
        private String port;
        private String username;
        private String password;
        private String topic;
    }
}
