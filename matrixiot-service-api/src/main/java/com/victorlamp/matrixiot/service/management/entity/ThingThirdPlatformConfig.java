package com.victorlamp.matrixiot.service.management.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ThingThirdPlatformConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -8608534864335690312L;
    private String thirdDeviceId;
    private String electronicNo;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Aep extends ThingThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -7791459994998147645L;
        private String deviceSn;
        private String imei;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class NBIoT extends ThingThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -7777805541877782813L;
        private String imsi;
        private String endUserId;
        private String nodeId;
        private String psk;
        private Integer timeOut;
        private String verifyCode;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class OneNET extends ThingThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -5184957440322106752L;
        private String imei;
        private String imsi;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class LoRa extends ThingThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -6688070903040398781L;
        private String mac;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Concentrator extends ThingThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 3568468289714230929L;
        private String concentratorCode;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ConcentratorSubThing extends ThingThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 292942713107579792L;
        private String concentratorCode;
        private String channelPort;
    }
}
