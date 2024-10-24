package com.victorlamp.matrixiot.service.management.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatistics implements Serializable {
    @Serial
    private static final long serialVersionUID = -8893220919518377571L;

    private Integer total;
    private Integer published;
}
