package com.victorlamp.matrixiot.service.common.exception;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.service.common.response.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
@Component("globalExceptionHandler1")
public class GlobalExceptionHandler {

    // 处理请求参数类型错误的异常
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseDTO exceptionHandler(MethodArgumentTypeMismatchException e) {
        ResponseDTO result = ResponseDTO.fail(ExceptionTemplate.INVALID_REQUEST_COMMON, "非法请求参数");
        log.error("invalid request error", e);
        return result;
    }

    // 处理请求参数绑定到java bean上失败的异常
    @ExceptionHandler(BindException.class)
    public ResponseDTO exceptionHandler(BindException e) {
        ResponseDTO result = ResponseDTO.fail(ExceptionTemplate.INVALID_REQUEST_COMMON, e.getMessage());
        log.error("invalid request error", e);
        return result;
    }

    // 处理请求体绑定到java bean上失败时异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDTO exceptionHandler(MethodArgumentNotValidException e) {
        ResponseDTO result = ResponseDTO.fail(ExceptionTemplate.INVALID_REQUEST_COMMON, e.getMessage());
        log.error("invalid request error", e);
        return result;
    }

    // 处理普通参数(非 java bean)校验出错时的异常
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseDTO exceptionHandler(ConstraintViolationException e) {
        ResponseDTO result = ResponseDTO.fail(ExceptionTemplate.INVALID_REQUEST_COMMON, getMessage(e));
        log.error("invalid request error", e);
        return result;
    }

    // 处理业务异常
    @ExceptionHandler(ServiceException.class)
    public ResponseDTO exceptionHandler(ServiceException e) {
        ResponseDTO result = ResponseDTO.fail(e.getErrorCode(), e.getMessage());
        log.error("服务异常：", e);
        return result;
    }

    @ExceptionHandler(com.victorlamp.matrixiot.common.exception.ServiceException.class)
    public CommonResult<?> exceptionHandler(com.victorlamp.matrixiot.common.exception.ServiceException e) {
        log.error("[服务异常]", e);
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseDTO exceptionHandler(DataAccessException e) {
        ResponseDTO result = ResponseDTO.fail(ExceptionTemplate.INTERNAL_FAILURE);
        log.error("数据库异常：", e);
        return result;
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public ResponseDTO exceptionHandler(Exception e) {
        ResponseDTO result = ResponseDTO.fail(ExceptionTemplate.INTERNAL_FAILURE);
        log.error("默认异常：", e);
        return result;
    }

    private String getMessage(ConstraintViolationException e) {
        return StringUtils.join(getMessages(e), ", ");
    }

    private List<String> getMessages(ConstraintViolationException e) {
        List<String> list = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            log.debug(constraintViolation.getMessage());
            list.add(constraintViolation.getMessage());
        }

        return list;
    }
}
