package com.victorlamp.matrixiot.service.route.dto;

import com.victorlamp.matrixiot.service.route.enums.DataRouteStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataRouteListReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5541597647710333715L;

    private DataRouteStatusEnum status;
}
