package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NodeTypeEnum {
    DIRECT(1, "直连设备"), GATEWAY(2, "网关设备"), SUBDEVICE(3, "网关子设备");

    private final Integer id;
    private final String label;
}
