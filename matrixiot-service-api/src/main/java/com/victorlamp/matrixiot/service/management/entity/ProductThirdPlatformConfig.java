package com.victorlamp.matrixiot.service.management.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ProductThirdPlatformConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -7412122360406811254L;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Aep extends ProductThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 6929626475329213324L;
        private String appId;
        private String appSecret;
        private String masterKey;
        private Integer thirdProductId;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class NBIoT extends ProductThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 6496518565500556052L;
        private String appId;
        private String appSecret;
        private String registerUrl;
        private String appAuthUrl;
        private Boolean isSecure;
        private Boolean isSubscribe;
        private String notifyType;
        private String subscribeUrl;
        private String subscribeCallbackUrl;
        private String commandUrl;
        private String commandCallbackUrl;
        private String thirdProductId;
        private NBIoT.DeviceInfo deviceInfo;

        @Data
        public static class DeviceInfo implements Serializable {
            @Serial
            private static final long serialVersionUID = -7670519779681126585L;
            private String deviceType;
            private String model;
            private String protocolType;
            private String manufacturerId;
            private String manufacturerName;

        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class OneNET extends ProductThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 1319088527974748720L;
        private String appId;
        private String thirdProductId;
        private String res;
        private String registerUrl;
        private String commandUrl;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class LoRa extends ProductThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -1710443558638018089L;
        private String appeui;
        private String token;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Concentrator extends ProductThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 995764905870132942L;
        private String ip;
        private Integer port;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ConcentratorSubThing extends ProductThirdPlatformConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -5310703613011536101L;
        private String ip;
        private Integer port;
    }
}
