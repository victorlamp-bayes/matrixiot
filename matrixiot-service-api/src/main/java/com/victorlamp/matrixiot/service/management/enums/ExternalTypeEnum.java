package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ExternalTypeEnum {
    NB_AEP("1", "电信AEP平台"),
    NB_OC("2", "电信OC平台"),
    NB_ONENET("3", "移动OneNET平台"),
    HUAXU_GD_HUB("4", "华旭光电集中器"),
    HUAXU_GD("5", "华旭光电直读"),
    HUAXU_LORA("6", "华旭LoRaWAN平台"),
    SANCHUAN_DB("7", "三川数据库"),
    DAIAO_SUBMETER("8", "代傲REST API"),
    DUYUN_MQTT("9", "都匀MQTT");

    private final String id;
    private final String label;
}
