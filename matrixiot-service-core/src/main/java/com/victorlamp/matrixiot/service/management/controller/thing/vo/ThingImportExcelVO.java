package com.victorlamp.matrixiot.service.management.controller.thing.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class ThingImportExcelVO {
    // AEP/OC
    @ExcelProperty({"设备ID", "id"})
    private String id;

    @ExcelProperty({"设备名称", "名称"})
    private String name;

    @ExcelProperty("IMEI")
    private String imei;

    @ExcelProperty("IMSI")
    private String imsi;

    @ExcelProperty("水表电子号")
    private String electronicNo;

    @ExcelProperty("经度")
    private Double longitude;

    @ExcelProperty("纬度")
    private Double latitude;

    // 集中器
    @ExcelProperty({"编号", "集中器编号"})
    private String hubCode;

    // 集中器子设备
    @ExcelProperty("通道号")
    private String channel;

    @ExcelProperty("测量点")
    private String channelPort;

    // LoRa
    @ExcelProperty("设备编号")
    private String mac;
}
