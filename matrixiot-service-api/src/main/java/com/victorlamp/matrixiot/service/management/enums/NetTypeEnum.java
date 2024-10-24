package com.victorlamp.matrixiot.service.management.enums;

import cn.hutool.core.util.ArrayUtil;
import com.victorlamp.matrixiot.common.core.StringArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NetTypeEnum implements StringArrayValuable {

    NB("1", "NBIoT"),
    LORA("2", "LoRaWAN"),
    WIFI("3", "WiFi"),
    CELLULAR("4", "蜂窝网络(2G/3G/4G/5G)"),
    ETHERNET("5", "以太网"),
    CUSTOM("9", "自定义");
    
    public static final String[] ARRAYS = ArrayUtil.map(values(), String.class, NetTypeEnum::getId);

    private final String id;
    private final String label;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
