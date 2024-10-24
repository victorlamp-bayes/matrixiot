package com.victorlamp.matrixiot.service.management.entity.product;

import com.victorlamp.matrixiot.service.management.enums.NetTypeEnum;
import com.victorlamp.matrixiot.service.management.enums.NodeTypeEnum;
import com.victorlamp.matrixiot.service.management.enums.ProtocolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 4781729098605182711L;

    @Id
    private String id;
    private String rn;
    private String name;
    private String description;
    private String icon;
    private Boolean published;
    private List<String> tags;
    private NodeTypeEnum nodeType;
    private NetTypeEnum netType;
    private ProtocolTypeEnum protocolType;
    private ProductExternalConfig externalConfig;
    private String dataFormat;
    private String manufacturer;
    private String model;
    private String category;

    @CreatedDate
    private Long createdAt;
    private Long deletedAt;
}
