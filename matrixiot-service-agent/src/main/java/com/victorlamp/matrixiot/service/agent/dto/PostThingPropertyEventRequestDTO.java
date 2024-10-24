package com.victorlamp.matrixiot.service.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostThingPropertyEventRequestDTO implements Serializable {
    private static final long serialVersionUID = 498923530990547838L;

    private String productId;
    private String payload;
}
