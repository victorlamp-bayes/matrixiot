package com.victorlamp.matrixiot.service.management.entity.product;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductExternalConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 4003339158444543445L;

    private String type;
    private JSONObject config;
}
