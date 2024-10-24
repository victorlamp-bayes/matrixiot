package com.victorlamp.matrixiot.service.alarm.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleRule implements Serializable {
    @Serial
    private static final long serialVersionUID = -7669745586530069357L;

    private String ruleId;
    private String ruleName;
}
