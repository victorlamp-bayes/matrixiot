package com.victorlamp.matrixiot.service.agent.thirdPartyService.OC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6415280023078635214L;
    private String appId;
    private String notifyType;
    private String callbackUrl;
}
