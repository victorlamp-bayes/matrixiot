package com.victorlamp.matrixiot.service.management.dto.thing;

import com.victorlamp.matrixiot.service.management.entity.thing.GeoPoint;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingExternalConfig;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ThingImportReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3920442264803677478L;

    private String productId;
    private String name;
    private String description;
    private GeoPoint position;
    private String gatewayId;
    private Boolean enabled;
    private List<String> tags;
    private ThingExternalConfig externalConfig;
}
