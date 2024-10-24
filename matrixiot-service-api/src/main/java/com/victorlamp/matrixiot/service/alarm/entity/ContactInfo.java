package com.victorlamp.matrixiot.service.alarm.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7669745586740069357L;

    /**
     * 告警消息发送目的地集合
     * 当 phone,email均为NULL 时，无需发送目的；
     * 当 它们不为NULL时，必须设置发送目的地
     */
    private String person;
    private String phone;
    private String email;
}
