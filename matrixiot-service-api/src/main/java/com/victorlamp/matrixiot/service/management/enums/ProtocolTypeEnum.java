package com.victorlamp.matrixiot.service.management.enums;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum ProtocolTypeEnum {
    OPCUA(1, "OPC-UA"),
    BLE(2, "BLE"),
    ZIGBEE(3, "ZigBee"),
    MODBUS(4, "Modbus"),
    CUSTOM(99, "自定义");

    private final Integer id;
    private final String label;

    public static boolean isValidProtocolType(String protocolType) {
        return ObjUtil.isNotNull(EnumUtil.fromStringQuietly(ProtocolTypeEnum.class, StringUtils.toRootUpperCase(protocolType)));
    }
}
