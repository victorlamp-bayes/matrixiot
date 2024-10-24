package com.victorlamp.matrixiot.service.route.dto;

import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
public class DataSourceUpdateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8057164084064738004L;

    @Schema(description = "数据源描述", example = "描述")
    @Size(max = 128, message = "数据源描述长度不能超过128位")
    private String description;

    @Schema(description = "数据源订阅主题Id")
    private String topicId;

    @Schema(description = "数据源产品Id")
    @IdHex24
    private String productId;

    @Schema(description = "数据源设备Id(不填写表示该产品下的所有设备)")
    @IdHex24
    private String thingId;
}
