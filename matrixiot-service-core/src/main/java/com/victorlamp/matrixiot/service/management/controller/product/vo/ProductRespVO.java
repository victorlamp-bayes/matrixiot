package com.victorlamp.matrixiot.service.management.controller.product.vo;

import com.victorlamp.matrixiot.service.common.response.EnumNameLabel;
import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ProductRespVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2372517125201454204L;

    private String id;
    private String rn;
    private String name;
    private String description;
    private String icon;
    private Boolean published;
    private List<String> tags;
    private EnumNameLabel nodeType;
    private EnumNameLabel netType;
    private EnumNameLabel protocolType;
    private ProductExternalConfig externalConfig;
    private String dataFormat;
    private String manufacturer;
    private String model;
    private EnumNameLabel category;

    @CreatedDate
    private Long createdAt;
}
