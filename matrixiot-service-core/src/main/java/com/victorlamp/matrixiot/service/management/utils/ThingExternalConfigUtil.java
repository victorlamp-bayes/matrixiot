package com.victorlamp.matrixiot.service.management.utils;

import com.victorlamp.matrixiot.service.management.constant.ThingExternalConfigItems;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingExternalConfig;

public class ThingExternalConfigUtil {

    public static String getExternalDeviceId(ThingExternalConfig externalConfig) {
        return externalConfig.getConfig().getString(ThingExternalConfigItems.ID);
    }
}
