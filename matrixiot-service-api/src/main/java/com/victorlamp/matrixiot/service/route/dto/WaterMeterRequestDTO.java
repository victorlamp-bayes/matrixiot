package com.victorlamp.matrixiot.service.route.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class WaterMeterRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4291448534956018760L;
    private List<WaterMeterDataDTO> data;

    @Data
    public static class WaterMeterDataDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = -635168974636920026L;
        private PushData pushData;
        private OtherParameters otherParameters;
    }

    @Data
    public static class PushData implements Serializable {
        @Serial
        private static final long serialVersionUID = 5118077474608388131L;

        private double dayMaxQuantity;
        private int successTimes;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private String collectTime = "";
        private double RSPR;
        private String valveState;
        private double batteryQuantity;
        private int failTimes;
        private int overCurrentWarning;
        private int lowVoltageWarning;
        private double overCurrentQuantity;
        @JsonProperty("Version")
        private String Version = "";
        private int valveWaring;
        private double SNR;
        private int refluxWarningQuantity;
        private int refluxWarning;
        private double currentQuantity;
        private int magneticInterWarning;
        private int continuedHighTraffic;
    }

    @Data
    @AllArgsConstructor
    public static class OtherParameters implements Serializable {
        @Serial
        private static final long serialVersionUID = 8077758212370549444L;
        private String code;
    }
}
