package com.victorlamp.matrixiot.service.management.constant;

public interface ThingExternalConfigItems {
    // 公共字段
    String ID = "id";
    String NAME = "name";
    String ELECTRONIC_NO = "electronicNo";

    // AEP
    String AEP_ID = ID;
    String AEP_NAME = NAME;
    String AEP_IMEI = "imei";
    String AEP_IMSI = "imsi";
    String AEP_ELECTRONIC_NO = ELECTRONIC_NO;
    String AEP_LONGITUDE = "longitude";
    String AEP_LATITUDE = "latitude";

    // OC
    String OC_ID = ID;
    String OC_NAME = NAME;
    String OC_IMEI = "imei";  // nodeId
    String OC_IMSI = "imsi";
    String OC_ELECTRONIC_NO = ELECTRONIC_NO;
    String OC_LONGITUDE = "longitude";
    String OC_LATITUDE = "latitude";

    // ONENET

    // LORA
    String LORA_ID = ID;
    String LORA_NAME = NAME;
    String LORA_MAC = "mac";
    String LORA_ELECTRONIC_NO = ELECTRONIC_NO;
    String LORA_LONGITUDE = "longitude";
    String LORA_LATITUDE = "latitude";

    // HUB
    String HUB_ID = ID;
    String HUB_NAME = NAME;
    String HUB_CODE = "hub_code";

    // HUB_SUB
    String HUB_SUB_ID = ID;
    String HUB_SUB_NAME = NAME;
    String HUB_SUB_ELECTRONIC_NO = ELECTRONIC_NO;
    String HUB_SUB_HUB_CODE = "hub_code";
    String HUB_SUB_CHANNEL = "channel";
    String HUB_SUB_CHANNEL_PORT = "channelPort";
}
