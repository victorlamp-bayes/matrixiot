package com.victorlamp.matrixiot.service.alarm.enums;

import cn.hutool.core.util.ArrayUtil;
import com.victorlamp.matrixiot.common.core.StringArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmSendStatusEnum implements StringArrayValuable {
    PENDING("1", "待发送"),
    SENT("2", "已发送"),
    FAILED("3", "发送失败");

    public static final String[] ARRAYS = ArrayUtil.map(values(), String.class, AlarmSendStatusEnum::getId);

    private final String id;
    private final String label;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
