package com.victorlamp.matrixiot.service.management.dto.product;

import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;
import com.victorlamp.matrixiot.service.management.enums.NetTypeEnum;
import com.victorlamp.matrixiot.service.management.enums.NodeTypeEnum;
import com.victorlamp.matrixiot.service.management.enums.ProtocolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3787738417061792428L;

    private String id;
    private String rn;
    private String name;
    private String description;
    private String icon;
    private Boolean published;
    private List<String> tags;
    private NodeTypeEnum nodeType;
    private NetTypeEnum netType;
    private ProductExternalConfig externalConfig;
    private ProtocolTypeEnum protocolType;
    private String dataFormat;
    private String manufacturer;
    private String model;
    private String category;
    private Long createdAt;
}
