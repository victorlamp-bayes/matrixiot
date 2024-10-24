package com.victorlamp.matrixiot.service.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyInvokeThingServiceRequestDTO implements Serializable {
    private static final long serialVersionUID = 3626096355297929032L;

    private String productId;
    private String payload;
}
