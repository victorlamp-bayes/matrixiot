package com.victorlamp.matrixiot.service.management.entity.thing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridCellThing {
    private GeoPoint position;
    private int thingCount;
}
