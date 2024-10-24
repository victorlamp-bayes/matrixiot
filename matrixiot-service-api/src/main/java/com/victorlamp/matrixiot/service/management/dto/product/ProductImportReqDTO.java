package com.victorlamp.matrixiot.service.management.dto.product;

import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImportReqDTO {

    private String name;

    private String description;

    private String icon;

    private Boolean published = false;

    private Set<String> tags;

    private String nodeType;

    private String netType;

    private ProductExternalConfig externalConfig;

    private String protocolType;

    private String dataFormat;

    private String manufacturer;

    private String model;

    private String category;

    private ThingModel thingModel;

}
