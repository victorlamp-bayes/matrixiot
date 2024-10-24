package com.victorlamp.matrixiot.service.management.controller.product.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import com.victorlamp.matrixiot.service.management.utils.excel.ListConverter;
import com.victorlamp.matrixiot.service.management.utils.excel.ProductExternalConfigConverter;
import com.victorlamp.matrixiot.service.management.utils.excel.ThingModelConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false)
public class ProductImportExcelVO {
    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("描述")
    private String description;

    @ExcelProperty("图标")
    private String icon;

    @ExcelProperty("发布状态")
    private Boolean published = false;

    @ExcelProperty(value = "标签", converter = ListConverter.class)
    private List<String> tags;

    @ExcelProperty("节点类型")
    private String nodeType;

    @ExcelProperty("连网协议")
    private String netType;

    @ExcelProperty(value = "连网配置", converter = ProductExternalConfigConverter.class)
    private ProductExternalConfig externalConfig;

    @ExcelProperty("接入网关协议")
    private String protocolType;

    @ExcelProperty("数据格式")
    private String dataFormat;

    @ExcelProperty("制造商")
    private String manufacturer;

    @ExcelProperty("型号")
    private String model;

    @ExcelProperty("品类")
    private String category;

    @ExcelProperty(value = "物模型", converter = ThingModelConverter.class)
    private ThingModel thingModel;

}
