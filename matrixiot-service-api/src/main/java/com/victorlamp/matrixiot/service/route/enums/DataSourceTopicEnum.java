package com.victorlamp.matrixiot.service.route.enums;

import com.victorlamp.matrixiot.common.core.IntArrayValuable;
import com.victorlamp.matrixiot.service.route.constant.DataSourceTopicName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DataSourceTopicEnum implements IntArrayValuable {

    THING_DATA_CHANGED(1000, "设备数据变化", DataSourceTopicName.THING_DATA_CHANGED),
    THING_STATUS_CHANGED(1001, "设备状态变化", DataSourceTopicName.THING_STATUS_CHANGED);

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(DataSourceTopicEnum::getId).toArray();

    private final Integer id;
    private final String name;
    private final String topic;

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
