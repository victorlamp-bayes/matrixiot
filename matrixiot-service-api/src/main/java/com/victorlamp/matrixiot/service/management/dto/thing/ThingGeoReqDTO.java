package com.victorlamp.matrixiot.service.management.dto.thing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThingGeoReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3128208733416950351L;

    @Size(min = 2, max = 2, message = "bottomLeft必须包含2个元素")
    @NotNull(message = "bottomLeft不能为空")
    private double[] bottomLeft;

    @Size(min = 2, max = 2, message = "topRight必须包含2个元素")
    @NotNull(message = "topRight不能为空")
    private double[] topRight;
}
