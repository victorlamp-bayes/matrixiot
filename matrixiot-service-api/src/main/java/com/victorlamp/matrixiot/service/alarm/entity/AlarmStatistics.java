package com.victorlamp.matrixiot.service.alarm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmStatistics implements Serializable {
    @Serial
    private static final long serialVersionUID = 3572937713198631553L;

    private Integer total;   //总数
    private Integer newAdd;  //今日新增
    private Integer pending; //待处理

}
