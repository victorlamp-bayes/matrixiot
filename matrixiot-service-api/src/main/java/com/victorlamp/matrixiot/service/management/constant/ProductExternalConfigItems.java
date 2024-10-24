package com.victorlamp.matrixiot.service.management.constant;

public interface ProductExternalConfigItems {
    // AEP
    String AEP_APP_ID = "appId";
    String AEP_APP_SECRET = "appSecret";
    String AEP_MASTER_KEY = "masterKey";
    String AEP_PRODUCT_ID = "productId";

    // OC
    String OC_APP_ID = "appId";
    String OC_APP_SECRET = "appSecret";
    String OC_REGISTER_URL = "registerUrl";
    String OC_APP_AUTH_URL = "appAuthUrl";
    String OC_IS_SECURE = "isSecure";
    String OC_IS_SUBSCRIBE = "isSubscribe";
    String OC_NOTIFY_TYPE = "notifyType";// = "deviceDataChanged";
    String OC_SUBSCRIBE_URL = "subscribeUrl";
    String OC_SUBSCRIBE_CALLBACK_URL = "subscribeCallbackUrl";
    String OC_COMMAND_URL = "commandUrl";
    String OC_COMMAND_CALLBACK_URL = "commandCallbackUrl";
    String OC_PRODUCT_ID = "productId";
    String OC_DEVICE_INFO = "deviceInfo";
    String OC_DEVICE_INFO_DEVICE_TYPE = "deviceType";
    String OC_DEVICE_INFO_MODEL = "model";
    String OC_DEVICE_INFO_PROTOCOL_TYPE = "protocolType";
    String OC_DEVICE_INFO_MANUFACTURER_ID = "manufacturerId";
    String OC_DEVICE_INFO_MANUFACTURER_NAME = "manufacturerName";

    // ONENET
    String ONENET_APP_ID = "appId";
    String ONENET_PRODUCT_ID = "productId";
    String ONENET_RES = "res"; // 访问资源信息: userid/{userid}
    String ONENET_REGISTER_URL = "registerUrl";
    String ONENENT_COMMAND_URL = "commandUrl";

    // LORA
    String LORA_APP_EUI = "appeui";
    String LORA_TOKEN = "token";

    // HUB,HUB_SUB
    String HUB_IP = "ip";
    String HUB_PORT = "port";
}
