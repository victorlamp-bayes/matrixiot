package com.victorlamp.matrixiot.service.management.enums;

import cn.hutool.core.util.ObjUtil;
import com.victorlamp.matrixiot.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ThingStatusEnum implements IntArrayValuable {

    UNACTIVATED(0, "未激活"),
    DISABLED(1, "已停用"),
    OFFLINE(2, "离线"),
    ONLINE(3, "在线");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ThingStatusEnum::getId).toArray();

    private final Integer id;
    private final String label;

    public static boolean isEnabled(Integer status) {
        return ObjUtil.equal(ONLINE.id, status) || ObjUtil.equal(OFFLINE.id, status);
    }

    public static boolean isEnabled(ThingStatusEnum statusEnum) {
        return statusEnum == ONLINE || statusEnum == OFFLINE;
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
