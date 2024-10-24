package com.victorlamp.matrixiot.service.common.constant;

import java.util.HashMap;
import java.util.Map;

public class ThingTopic {
    public static final String THING_PROPERTY_EVENT_POST = "iot_thing_property_event_post";
    public static final String THING_PROPERTY_EVENT_POST_REPLY = "iot_thing_property_event_post_reply";
    //    public static final String THING_EVENT_POST = "iot_thing_event_post";
//    public static final String THING_EVENT_POST_REPLY = "iot_thing_event_post_reply";
    public static final String THING_POST_WARNING = "iot_thing_post_warning";
    public static final String THING_PROPERTY_SET = "iot_thing_property_set";
    public static final String THING_PROPERTY_SET_REPLY = "iot_thing_property_set_reply";
    public static final String THING_SERVICE_INVOKE = "iot_thing_service_invoke";
    public static final String THING_SERVICE_INVOKE_REPLY = "iot_thing_service_invoke_reply";
    public static final String THIRD_DEVICE_REGISTER_REPLY = "iot_third_device_register_reply";
    // 用于保存合法的主题
    public static Map<String, String> TOPIC_LIST = new HashMap<>();

    static {
        TOPIC_LIST.put(THING_PROPERTY_EVENT_POST, "设备属性/事件上报");
        TOPIC_LIST.put(THING_PROPERTY_EVENT_POST_REPLY, "设备属性/事件上报回复");
        TOPIC_LIST.put(THING_POST_WARNING, "设备上报警告");
        TOPIC_LIST.put(THING_PROPERTY_SET, "设备属性设置");
        TOPIC_LIST.put(THING_PROPERTY_SET_REPLY, "设备属性设置回复");
        TOPIC_LIST.put(THING_SERVICE_INVOKE, "设备服务调用");
        TOPIC_LIST.put(THING_SERVICE_INVOKE_REPLY, "设备服务调用回复");
    }
}
