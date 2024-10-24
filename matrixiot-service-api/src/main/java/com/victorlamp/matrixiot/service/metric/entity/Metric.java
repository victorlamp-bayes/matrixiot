package com.victorlamp.matrixiot.service.metric.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Metric implements Serializable {
    @Serial
    private static final long serialVersionUID = 934506122141185205L;

    @Id
    private String id;
    private String name;
    private String productId;
    private String thingId;
    private String propertyIdentifier;
    private String aggregationType;
    private Integer aggregationFreq;
    private String description;
    @CreatedDate
    private Long createdAt;
    private Long deletedAt;
}