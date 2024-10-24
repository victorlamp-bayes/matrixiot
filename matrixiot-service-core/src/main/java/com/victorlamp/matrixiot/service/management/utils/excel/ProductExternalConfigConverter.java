package com.victorlamp.matrixiot.service.management.utils.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.fastjson2.JSON;
import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;

public class ProductExternalConfigConverter implements Converter<ProductExternalConfig> {
    @Override
    public ProductExternalConfig convertToJavaData(ReadConverterContext<?> context) {
        String content = context.getReadCellData().getStringValue();
        return JSON.parseObject(content, ProductExternalConfig.class);
    }
}
