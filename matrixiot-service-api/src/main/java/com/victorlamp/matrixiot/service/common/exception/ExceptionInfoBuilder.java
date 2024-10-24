package com.victorlamp.matrixiot.service.common.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 提供业务异常相关公共方法，如构建异常类
 *
 * @author: Dylan
 * @date: 2023/10/25
 */
@Slf4j
public class ExceptionInfoBuilder {

    /**
     * 构建 ServiceException.ExceptionInfo
     * params 字符串数组，会按序一一替换 {@link ExceptionTemplate.messageTemplate} 里的 {}。
     *
     * @param exceptionTemplate
     * @param params            按顺序替换 exceptionTemplate.errorMessage 里的 {}
     * @return: com.victorlamp.matrixiot.service.common.exception.ServiceException
     * @author: Dylan-孙林
     * @Date: 2023/10/25
     */
    public static Optional<ServiceException.ExceptionInfo> build(ExceptionTemplate exceptionTemplate, String... params) {

        if (exceptionTemplate == null) {
            return Optional.empty();
        }

        String message = getMessage(exceptionTemplate, params);

        ServiceException.ExceptionInfo exceptionInfo = ServiceException.ExceptionInfo
                .builder()
                .errorCode(exceptionTemplate.getErrorCode())
                .errorMessage(message)
                .build();

        return Optional.of(exceptionInfo);
    }

    /**
     * 构建 ServiceException.ExceptionInfo，code 采用 {@link ExceptionTemplate.errorCode}，
     * 不采用模板，而是用 message 代替
     *
     * @param exceptionTemplate
     * @param message           完整的错误提示消息
     * @return: com.victorlamp.matrixiot.service.common.exception.ServiceException
     * @author: Dylan-孙林
     * @Date: 2023/10/25
     */
    public static Optional<ServiceException.ExceptionInfo> buildWithCustomMsg(ExceptionTemplate exceptionTemplate, String message) {

        if (exceptionTemplate == null) {
            return Optional.empty();
        }

        ServiceException.ExceptionInfo exceptionInfo = ServiceException.ExceptionInfo
                .builder()
                .errorCode(exceptionTemplate.getErrorCode())
                .errorMessage(message)
                .build();

        return Optional.of(exceptionInfo);
    }

    private static String getMessage(ExceptionTemplate exceptionTemplate, String... params) {

        if (params == null || params.length == 0) {

            return exceptionTemplate.getMessageTemplate();
        }

        String msg = exceptionTemplate.getMessageTemplate();

        for (int i = 0; i < params.length; i++) {

            msg = msg.replaceFirst("[{][}]", params[i]);
        }

        if (msg.contains("{}")) {

            log.warn("消息模板占位符数量 != 传参数量, template=[{}],params=[{}]", exceptionTemplate.getMessageTemplate(),
                    Arrays.stream(params).collect(Collectors.joining(",")));
        }

        return msg;
    }
}
