package com.victorlamp.matrixiot.service.alarm.enums;

import com.victorlamp.matrixiot.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AlarmLevelEnum implements IntArrayValuable {
    
    INFO(1, "提示"),
    WARNING(2, "警告"),
    MINOR(3, "次要"),
    MAJOR(4, "重要"),
    CRITICAL(5, "紧急");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(AlarmLevelEnum::getLevel).toArray();

    private final Integer level;
    private final String label;

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
