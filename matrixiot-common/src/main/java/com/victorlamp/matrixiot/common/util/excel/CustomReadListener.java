package com.victorlamp.matrixiot.common.util.excel;

import cn.hutool.core.util.ObjUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomReadListener<T> implements ReadListener<T> {

    private static final int BATCH_COUNT = 100;
    private final Map<String, Integer> fieldToHeadIndexMap = new HashMap<>();
    public List<Object> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    public Class<T> entityClassType;

    @SuppressWarnings("unchecked")
    public CustomReadListener() {
        this.entityClassType = (Class<T>) ObjUtil.getTypeArgument(this);
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Map<Integer, String> newHeadMap = ConverterUtils.convertToStringMap(headMap, context);

        Map<String, Integer> fieldToHeadIndexMap = EasyExcelParser.getFieldToHeadIndexMap(newHeadMap, entityClassType);
        this.fieldToHeadIndexMap.putAll(fieldToHeadIndexMap);

        ReadListener.super.invokeHead(headMap, context);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        if (fieldToHeadIndexMap.isEmpty()) {
            throw new ExcelAnalysisException("模板错误");
        }

        EasyExcelParser.setFieldValue(data, fieldToHeadIndexMap, context);

        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            doAfterAllAnalysed(context);
            cachedDataList.clear();
        }
    }

    public void dataDeal() {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        dataDeal();
    }
}
