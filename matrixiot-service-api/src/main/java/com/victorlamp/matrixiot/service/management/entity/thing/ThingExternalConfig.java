package com.victorlamp.matrixiot.service.management.entity.thing;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingExternalConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -7782931673748603292L;

    private String type;
    private JSONObject config;
}
