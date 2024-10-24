package com.victorlamp.matrixiot.service.management.entity.thingmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresetScript implements Serializable {
    @Serial
    private static final long serialVersionUID = 6053759645458616902L;
    private int id;
    private String name;
}
