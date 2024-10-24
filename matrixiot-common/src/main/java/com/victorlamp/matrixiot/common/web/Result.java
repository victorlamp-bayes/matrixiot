package com.victorlamp.matrixiot.common.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result implements Serializable {
    @Serial
    private static final long serialVersionUID = 7645648078360550452L;

    private static final Result SUCCESS = new Result(true);
    private static final Result FAIL = new Result(false);

    private boolean success;
    private Integer code;
    private String errcode;
    private String errmsg;
    private Object data;

    private Result(boolean success) {
        this.success = success;
    }

    private Result(Object data) {
        this.success = true;
        this.data = data;
    }

    private Result(int code, int errcode, String errmsg) {
        this.success = false;
        this.code = code;
        this.errcode = String.valueOf(errcode);
        this.errmsg = errmsg;
    }

    private Result(int code, String errcode, String errmsg) {
        this.success = false;
        this.code = code;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public static Result success() {
        return SUCCESS;
    }

    public static Result success(Object data) {
        return new Result(data);
    }

    public static Result fail() {
        return FAIL;
    }

    public static Result fail(int code, int errcode, String errmsg) {
        return new Result(code, errcode, errmsg);
    }
}
