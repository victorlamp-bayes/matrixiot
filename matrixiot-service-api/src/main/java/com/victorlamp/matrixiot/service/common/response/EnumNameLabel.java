package com.victorlamp.matrixiot.service.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class EnumNameLabel implements Serializable {
    @Serial
    private static final long serialVersionUID = 5504672710643680239L;

    private String name;
    private String label;
}
