package com.victorlamp.matrixiot.service.management.entity.thing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingStatistics implements Serializable {
    @Serial
    private static final long serialVersionUID = 3572935593198631553L;
    
    private Integer total;
    private Integer enabled;
    private Integer online;
}
