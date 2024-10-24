package com.victorlamp.matrixiot.service.common.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import java.io.Serial;

@Getter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2650295246340399299L;

    private final Integer code;
    private final String message;

    private BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static BusinessException build(ExceptionTemplate exceptionTemplate, Object... params) {
        return new BusinessException(
                exceptionTemplate.getErrorCode(),
                StringUtils.substringBetween(MessageFormatter.format(exceptionTemplate.getMessageTemplate(), params).getMessage(), "[", "]"));
    }
}
