package com.victorlamp.matrixiot.service.common.enums;

/**
 * 资源的类型，用于资源编号 RN
 *
 * @author: Dylan
 * @date: 2023/10/18
 */
public enum ResourceType {

    /**
     * 设置设备属性
     */
    PRODUCT("product"),
    THING("thing"),
    ALARM("alarm"),
    THING_GROUP("thing-group"),
    METRIC("metric"),
    DATA_ROUTE("data-route"),
    DATA_ROUTE_DESTINATION("data-route-destination"),
    DATA_ROUTE_SOURCE("data-route-source"),
    SCENE_RULE("scene-rule"),
    ;

    private String value;

    ResourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
