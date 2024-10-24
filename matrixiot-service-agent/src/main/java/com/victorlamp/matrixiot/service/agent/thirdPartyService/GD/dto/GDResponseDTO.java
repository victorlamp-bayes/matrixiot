package com.victorlamp.matrixiot.service.agent.thirdPartyService.GD.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class GDResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1595188971387500268L;

    private int status;
    private String msg;
    private List<Map<String,String>> results;
}
