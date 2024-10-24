package com.victorlamp.matrixiot.service.management.entity.thing;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class GeoPoint implements Serializable {
    @Serial
    private static final long serialVersionUID = 3699828802850193927L;

    private double longitude;
    private double latitude;

    public GeoPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
