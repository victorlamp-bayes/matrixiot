package com.victorlamp.matrixiot.service.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryEnum {
    WM_NB(1, "NBIoT水表"),
    WM_LORA(2, "LoRaWAN水表"),
    WM_HUB(3, "集中器"),
    WM_PDRR(4, "光电直读远传水表");

    private final Integer id;
    private final String label;
}
