package com.victorlamp.matrixiot.service.management.utils.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.fastjson2.JSON;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public class ListConverter implements Converter<List<T>> {
    @Override
    public List<T> convertToJavaData(ReadConverterContext<?> context) {
        String content = context.getReadCellData().getStringValue();
        return JSON.parseArray(content).toList(T.class);
    }
}
