package com.victorlamp.matrixiot.service.management.entity.externalconfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalConfigItem implements Serializable {
    @Serial
    private static final long serialVersionUID = -2688409431621590726L;
    private String key;
    private String label;
}
