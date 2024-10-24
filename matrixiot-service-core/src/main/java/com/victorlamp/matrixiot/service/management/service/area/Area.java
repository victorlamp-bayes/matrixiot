package com.victorlamp.matrixiot.service.management.service.area;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class Area {

    @Id
    private String id;

    private int code;

    private JSONObject bound;
}
