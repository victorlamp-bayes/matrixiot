package com.victorlamp.matrixiot.service.management.utils;

import com.victorlamp.matrixiot.service.management.entity.thing.GridCellThing;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingGeoReqDTO;
import com.victorlamp.matrixiot.service.management.entity.thing.GeoPoint;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ThingGeoUtils {
    public static List<GridCellThing> getGridCellThings(ThingGeoReqDTO reqVO, List<Thing> things) {
        double[] topRight = reqVO.getTopRight();
        double[] bottomLeft = reqVO.getBottomLeft();

        // 计算X和Y的差值(视图长和宽)
        double deltaX = topRight[0] - bottomLeft[0];
        double deltaY = topRight[1] - bottomLeft[1];

        // 计算X和Y的平均值
        double avgX = deltaX / 4;
        double avgY = deltaY / 4;

        // 使用右上角作为起始点
        double x = topRight[0];
        double y = topRight[1];

        List<GridCellThing> gridCellThings = new ArrayList<>();

        // 循环生成4*4=16网格
        for (int a = 0; a < 4; a++) {
            for (int i = 0; i < 4; i++) {
                GridCellThing gridCellThing = new GridCellThing();
                // 计算网格边界
                double minX = x - (i + 1) * avgX;
                double maxX = x - i * avgX;
                double minY = y - (a + 1) * avgY;
                double maxY = y - a * avgY;

                double centerTotalX = 0;
                double centerTotalY = 0;
                int count = 0;

                // 遍历设备列表
                for (Thing thing : things) {
                    double longitude = thing.getPosition().getLongitude();
                    double latitude = thing.getPosition().getLatitude();

                    // 检查设备是否在当前网格内，根据最大值最小值区分来做到去重边界相交的设备;
                    if (longitude > minX && longitude <= maxX && latitude > minY && latitude <= maxY) {
                        count++;

                        //得到有效设备的经纬度总和
                        centerTotalX += longitude;
                        centerTotalY += latitude;
                    }
                }

                // 如果有符合条件的设备，则记录到响应对象列表中
                if (count > 0) {
                    //分别把经度总和、纬度总和 除以count(网格区域内设备总数)得到经纬度的加权平均值
                    double centerLongitude = centerTotalX / count;
                    double centerLatitude = centerTotalY / count;

                    GeoPoint geoPoint = new GeoPoint();
                    geoPoint.setLongitude(centerLongitude);
                    geoPoint.setLatitude(centerLatitude);
                    gridCellThing.setPosition(geoPoint);
                    gridCellThing.setThingCount(count);
                    gridCellThings.add(gridCellThing);
                }
            }
        }
        return gridCellThings;
    }
}
