package com.victorlamp.matrixiot.service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

public class ServiceException extends RuntimeException {
    public static final ServiceException INVALID_REQUEST = new ServiceException(ExceptionType.INVALID_REQUEST);
    public static final ServiceException UNAUTHORIZED = new ServiceException(ExceptionType.UNAUTHORIZED);
    public static final ServiceException RESOURCE_NOT_FOUND = new ServiceException(ExceptionType.RESOURCE_NOT_FOUND);
    public static final ServiceException RESOURCE_ALREADY_EXISTS = new ServiceException(ExceptionType.RESOURCE_ALREADY_EXISTS);
    public static final ServiceException INTERNAL_FAILURE = new ServiceException(ExceptionType.INTERNAL_FAILURE);
    public static final ServiceException SERVICE_UNAVAILABLE = new ServiceException(ExceptionType.SERVICE_UNAVAILABLE);

    private static final long serialVersionUID = 8229214251358432235L;
    @Getter
    private int code;

    @Getter
    private int errorCode;

    private ServiceException(ExceptionType exceptionType) {
        super(exceptionType.getMsg());
        this.code = exceptionType.getCode();
    }

    public ServiceException(ExceptionType exceptionType, Optional<ExceptionInfo> exceptionInfo) {

        super(exceptionInfo.isPresent() ? exceptionInfo.get().getErrorMessage() : exceptionType.getMsg());

        this.code = exceptionType.getCode();

        exceptionInfo.ifPresent(e -> this.errorCode = e.getErrorCode());
    }

    public enum ExceptionType {
        INVALID_REQUEST(400, "The request is not valid."),
        UNAUTHORIZED(401, "You are not authorized to perform this operation."),
        RESOURCE_NOT_FOUND(404, "The specified resource does not exist."),
        RESOURCE_ALREADY_EXISTS(409, "The resource already exists."),
        INTERNAL_FAILURE(500, "An unexpected error has occurred."),
        SERVICE_UNAVAILABLE(503, "The service is temporarily unavailable.");

        @Getter
        private final int code;

        @Getter
        private final String msg;

        ExceptionType(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ExceptionInfo {
        private int errorCode;
        private String errorMessage;
    }
}
