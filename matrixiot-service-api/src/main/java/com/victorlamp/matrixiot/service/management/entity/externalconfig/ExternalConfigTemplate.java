package com.victorlamp.matrixiot.service.management.entity.externalconfig;

import com.alibaba.fastjson2.JSONObject;
import com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ExternalConfigTemplate implements Serializable {
    @Serial
    private static final long serialVersionUID = -544557887170797879L;
    @Id
    private String id;
    private ExternalTypeEnum type;
    private List<ExternalConfigItem> product;
    private List<ExternalConfigItem> thing;
    private JSONObject jsonSchema;
}
