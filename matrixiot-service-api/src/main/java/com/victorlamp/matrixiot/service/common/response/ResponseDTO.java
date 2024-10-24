package com.victorlamp.matrixiot.service.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -8805297185193950895L;

    private boolean success;
    private T data;
    private Integer errcode;
    private String errmsg;

    private ResponseDTO(T data) {
        this.success = true;
        this.data = data;
    }

    private ResponseDTO(Integer errcode, String errmsg) {
        this.success = false;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public static <T> ResponseDTO<T> success() {
        return new ResponseDTO<>(null);
    }

    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(data);
    }

    public static <T> ResponseDTO<T> fail() {
        return new ResponseDTO<>(null, null);
    }

    public static <T> ResponseDTO<T> fail(ExceptionTemplate exceptionTemplate, Object... params) {
        String msg = MessageFormatter.format(exceptionTemplate.getMessageTemplate(), params).getMessage();
        return new ResponseDTO<>(exceptionTemplate.getErrorCode(), StringUtils.substring(msg, 1, -1));
    }

    public static <T> ResponseDTO<T> fail(Integer errcode, String errmsg) {
        return new ResponseDTO<>(errcode, errmsg);
    }
}
