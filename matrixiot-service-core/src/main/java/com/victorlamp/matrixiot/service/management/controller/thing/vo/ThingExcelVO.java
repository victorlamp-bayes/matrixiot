package com.victorlamp.matrixiot.service.management.controller.thing.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingExcelVO {

    @ExcelProperty("设备Id")
    private String id;

    @ExcelProperty("设备名称")
    private String name;

    @ExcelProperty("所属产品Id")
    private String productId;

    @ExcelProperty("所属产品名称")
    private String productName;

    @ExcelProperty("设备类型")
    private String nodeType;

    @ExcelProperty("外部配置类型")
    private String externalConfigType;

    @ExcelProperty("外部配置")
    private String externalConfig;

    @ExcelProperty("设备描述")
    private String description;
}
