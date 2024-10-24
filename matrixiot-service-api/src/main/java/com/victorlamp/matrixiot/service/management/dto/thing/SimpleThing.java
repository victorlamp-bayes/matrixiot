package com.victorlamp.matrixiot.service.management.dto.thing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleThing implements Serializable {

    @Serial
    private static final long serialVersionUID = 1955329240953576542L;

    @NotBlank(message = "产品ID不能为空")
    private String productId;

    @NotBlank(message = "产品名不能为空")
    private String productName;

    @NotBlank(message = "设备ID不能为空")
    private String thingId;

    @NotBlank(message = "设备名不能为空")
    private String thingName;
}
