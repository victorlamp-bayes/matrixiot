package com.victorlamp.matrixiot.service.management.entity.thing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingGeo {
    private List<GridCellThing> gridCellList; //各个网格单元内的设备总数
    private GridCellThing  noGeoThings; // 编码区域内没有地理位置的设备总数
}
