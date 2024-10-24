package com.victorlamp.matrixiot.service.common.exception;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

public class ExceptionResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 4307376400693492342L;

    @Getter
    private boolean success = false;
    @Getter
    private int code;
    @Getter
    private int errcode;
    @Getter
    private String errmsg;

    public ExceptionResponse(ServiceException exception) {
        this.code = exception.getCode();
        this.errcode = exception.getErrorCode();
        this.errmsg = exception.getMessage();
    }

    public ExceptionResponse(int code, int errcode, String errmsg) {
        this.code = code;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }
}
