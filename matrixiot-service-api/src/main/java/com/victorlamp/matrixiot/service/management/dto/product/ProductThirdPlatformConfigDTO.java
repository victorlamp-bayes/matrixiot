package com.victorlamp.matrixiot.service.management.dto.product;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ProductThirdPlatformConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2494707903094372541L;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonTypeName("AEP")
    public static class Aep extends ProductThirdPlatformConfigDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 3837428758248751569L;
        private String appId;
        private String appSecret;
        private String masterKey;
        private Integer thirdProductId;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonTypeName("NBIoT")
    public static class NBIoT extends ProductThirdPlatformConfigDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = -4347054842565710084L;
        private String appId;
        private String appSecret;
        private String registerUrl;
        private String appAuthUrl;
        private Boolean isSecure = false;
        private Boolean isSubscribe = false;
        private String notifyType = "deviceDataChanged";
        private String subscribeUrl;
        private String subscribeCallbackUrl;
        private String commandUrl;
        private String commandCallbackUrl;
        private String thirdProductId;
        private DeviceInfo deviceInfo;

        @Data
        public static class DeviceInfo implements Serializable {
            @Serial
            private static final long serialVersionUID = -2208434007810428677L;
            private String deviceType;
            private String model;
            private String protocolType;
            private String manufacturerId;
            private String manufacturerName;

        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonTypeName("OneNET")
    public static class OneNET extends ProductThirdPlatformConfigDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = -7890656763314596299L;
        private String appId;
        private String thirdProductId;
        private String res; // 访问资源信息: userid/{userid}
        private String registerUrl;
        private String commandUrl;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonTypeName("LoRa")
    public static class LoRa extends ProductThirdPlatformConfigDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 4701560657573257680L;
        private String appeui;
        private String token;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonTypeName("Concentrator")
    public static class Concentrator extends ProductThirdPlatformConfigDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = -4624030167905336201L;
        private String ip;
        private Integer port;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonTypeName("ConcentratorSubThing")
    public static class ConcentratorSubThing extends ProductThirdPlatformConfigDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = -8332263906569647215L;
        private String ip;
        private Integer port;
    }
}
