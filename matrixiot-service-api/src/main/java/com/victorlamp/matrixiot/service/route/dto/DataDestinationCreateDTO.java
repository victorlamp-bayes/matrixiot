package com.victorlamp.matrixiot.service.route.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.victorlamp.matrixiot.service.route.constant.DestinationConfigType;
import com.victorlamp.matrixiot.service.route.entity.DataDestinationConfig;
import com.victorlamp.matrixiot.service.route.enums.DataDestinationConfigTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDestinationCreateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6630868218174882202L;

    @Schema(description = "数据目的描述", example = "描述")
    @Size(max = 128, message = "数据目的描述长度不能超过128位")
    private String description;

    @Schema(description = "数据目的配置类型")
    @Valid
    @NotNull(message = "数据目的配置类型不能为空")
    private DataDestinationConfigTypeEnum type;

    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DataDestinationConfig.RestApi.class, name = DestinationConfigType.REST_API),
            @JsonSubTypes.Type(value = DataDestinationConfig.RabbitMQ.class, name = DestinationConfigType.RABBITMQ)
    })
    @Schema(description = "数据目的配置")
    private DataDestinationConfig config;
}
