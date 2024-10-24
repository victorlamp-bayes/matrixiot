package com.victorlamp.matrixiot.service.route.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class DataRoute implements Serializable {
    @Serial
    private static final long serialVersionUID = -3888163558064956539L;

    @Id
    private String id;
    private String name;
    private String description;
    private DataSource source;
    private DataDestination destination;
    private DataTransformer transformer;
    private String status;
    @CreatedDate
    private Long createdAt;
    @LastModifiedDate
    private Long updatedAt;
    private Long deletedAt;
}
