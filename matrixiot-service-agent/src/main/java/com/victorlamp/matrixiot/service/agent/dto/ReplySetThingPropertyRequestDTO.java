package com.victorlamp.matrixiot.service.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplySetThingPropertyRequestDTO implements Serializable {
    private static final long serialVersionUID = -9194669722093869740L;

    private String productId;
    private String payload;
}
