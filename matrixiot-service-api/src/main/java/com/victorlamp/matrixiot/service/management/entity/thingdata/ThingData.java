package com.victorlamp.matrixiot.service.management.entity.thingdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ThingData implements Serializable {
    @Serial
    private static final long serialVersionUID = 4166229698745500907L;

    @Id
    private String id;
    private String thingId;
    private String productId;

    private List<ThingPropertyData> properties;
    private List<ThingEventData> events;
    private ThingServiceData service;
    private String originData;

//    private Command command;

    @CreatedDate
    private Long timestamp;

//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class Command implements Serializable {
//        @Serial
//        private static final long serialVersionUID = 1092705531918867026L;
//
//        private String commandId;
//        private String status;
//        private String commandType;
//        private Map<String, Object> commandContent;
//        private String outputParams;
//        private Long timestamp;
//    }
}
