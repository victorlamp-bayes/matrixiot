package com.victorlamp.matrixiot.service.route.dto;

import com.victorlamp.matrixiot.common.validation.InEnum;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.route.enums.DataSourceTopicEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceCreateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3956281445183592940L;

    @Schema(description = "数据源描述", example = "描述")
    @Size(max = 128, message = "数据源描述长度不能超过128位")
    private String description;

    @Schema(description = "数据源订阅主题Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源订阅主题Id不能为空")
    @InEnum(DataSourceTopicEnum.class)
    private Integer topicId;

    @Schema(description = "数据源产品Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据源产品Id不能为空")
    @IdHex24
    private String productId;

    @Schema(description = "数据源设备Id(不填写表示该产品下的所有设备)")
    @IdHex24
    private String thingId;
}
