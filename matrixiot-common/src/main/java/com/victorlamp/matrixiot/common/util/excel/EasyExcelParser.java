package com.victorlamp.matrixiot.common.util.excel;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EasyExcelParser {

    /**
     * 字段值赋值
     *
     * @param data                表头位置:表中的值
     * @param entity              实体类
     * @param fieldToHeadIndexMap 字段名:表头位置
     */
    public static void setFieldValue(Map<Integer, String> data, Map<String, Integer> fieldToHeadIndexMap, Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            //遍历每个属性
            if (field.isAnnotationPresent(ExcelProperty.class) && fieldToHeadIndexMap.containsKey(field.getName())) {
                field.setAccessible(true);
                try {
                    field.set(entity, data.get(fieldToHeadIndexMap.get(field.getName())));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 字段值赋值
     *
     * @param data                表数据
     * @param entity              实体类
     * @param fieldToHeadIndexMap 字段名:表头位置
     */
    public static <T> void setFieldValue(T data, Map<String, Integer> fieldToHeadIndexMap, T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            //遍历每个属性
            if (field.isAnnotationPresent(ExcelProperty.class) && fieldToHeadIndexMap.containsKey(field.getName())) {
                field.setAccessible(true);
                try {
                    field.set(entity, ReflectUtil.getFieldValue(data, field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 补齐缺失的字段值
     *
     * @param data                对象数据：由于easyexcel无法正确处理@ExcelProperty中多个值的问题，从而缺失字段值
     * @param fieldToHeadIndexMap 字段名:表头位置
     * @param context
     * @param <T>
     */
    public static <T> void setFieldValue(T data, Map<String, Integer> fieldToHeadIndexMap, AnalysisContext context) {
        ReadRowHolder rowHolder = context.readRowHolder();
        Map<Integer, Cell> cellMap = rowHolder.getCellMap();

        for (Map.Entry<String, Integer> entry : fieldToHeadIndexMap.entrySet()) {
            String fieldName = entry.getKey();
            if (ReflectUtil.getFieldValue(data, fieldName) == null) {
                Integer headIndex = entry.getValue();
                ReadCellData readCellData = (ReadCellData) cellMap.get(headIndex);
                ReflectUtil.setFieldValue(data, fieldName, getCellDataValue(readCellData));
            }
        }
    }

    private static Object getCellDataValue(ReadCellData cellData) {
        if (ObjUtil.isNull(cellData)) {
            return null;
        }
        
        switch (cellData.getType()) {
            case STRING -> {
                return cellData.getStringValue();
            }
            case NUMBER -> {
                return cellData.getNumberValue();
            }
            case BOOLEAN -> {
                return cellData.getBooleanValue();
            }
            default -> {
                return null;
            }
        }
    }


    /**
     * 获取对象字段对应表头位置
     *
     * @param headMap 表头map
     * @param clazz   对应解析类
     * @return
     */
    public static Map<String, Integer> getFieldToHeadIndexMap(Map<Integer, String> headMap, Class<?> clazz) {
        // 对象字段:表头位置
        Map<String, Integer> fieldToHeadIndexMap = new HashMap<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //遍历每个属性
            if (field.isAnnotationPresent(ExcelProperty.class)) {
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
                    if (Arrays.asList(excelProperty.value()).contains(entry.getValue())) {
                        fieldToHeadIndexMap.put(field.getName(), entry.getKey());
                    }
                }
            }
        }
        return fieldToHeadIndexMap;
    }

}
