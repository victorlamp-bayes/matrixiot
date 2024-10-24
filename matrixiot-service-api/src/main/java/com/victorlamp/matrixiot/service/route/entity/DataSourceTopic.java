package com.victorlamp.matrixiot.service.route.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceTopic implements Serializable {
    @Serial
    private static final long serialVersionUID = -7440325502388696176L;

    private Integer id;
    private String name;
    private String topic;
}
