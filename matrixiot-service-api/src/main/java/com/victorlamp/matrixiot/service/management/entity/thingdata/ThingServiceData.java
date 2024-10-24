package com.victorlamp.matrixiot.service.management.entity.thingdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThingServiceData implements Serializable {
    @Serial
    private static final long serialVersionUID = 7250823972854978067L;
    private String commandId;
    private String identifier;
    private Boolean status;
    private List<Arg> args;
    private Long timestamp;
    private List<CallbackRecord> callbackRecords;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Arg implements Serializable {
        @Serial
        private static final long serialVersionUID = -2205865760534292889L;
        private String identifier;
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CallbackRecord implements Serializable {
        @Serial
        private static final long serialVersionUID = 3809360520850460552L;
        private String status;
        private String message;
        private String originData;
        private Long timestamp;
    }
}
