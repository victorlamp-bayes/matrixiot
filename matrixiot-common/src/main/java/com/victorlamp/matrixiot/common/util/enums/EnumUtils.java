package com.victorlamp.matrixiot.common.util.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Slf4j
public class EnumUtils {
    public static <E extends Enum<E>> List<Object> listEnumValue(Class<E> clazz) {
        final List<Object> list = CollUtil.newArrayList();

        Field[] fields = ReflectUtil.getFields(clazz);
        for (Map.Entry<String, E> entry : EnumUtil.getEnumMap(clazz).entrySet()) {
            E enumObj = entry.getValue();
            Map<String, Object> fieldNameValueMap = MapUtil.newHashMap();
            for (Field field : fields) {
                // 排除静态字段和Enum父类字段ordinal
                if (ModifierUtil.isStatic(field) || StrUtil.equals(field.getName(), "ordinal")) {
                    continue;
                }
                fieldNameValueMap.put(field.getName(), ReflectUtil.getFieldValue(enumObj, field.getName()));
            }
            list.add(fieldNameValueMap);
        }

        return list;
    }
}
