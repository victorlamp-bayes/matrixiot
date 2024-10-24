package com.victorlamp.matrixiot.service.framework.apilog.core.service;

import cn.hutool.core.bean.BeanUtil;
import com.victorlamp.matrixiot.service.infra.dto.ApiErrorLogCreateReqDTO;
import com.victorlamp.matrixiot.service.infra.service.logger.ApiErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

/**
 * API 错误日志 Framework Service 实现类
 *
 * @author 芋道源码
 */
@RequiredArgsConstructor
public class ApiErrorLogFrameworkServiceImpl implements ApiErrorLogFrameworkService {

    private final ApiErrorLogService apiErrorLogService;

    @Override
    @Async
    public void createApiErrorLog(ApiErrorLog apiErrorLog) {
        ApiErrorLogCreateReqDTO reqDTO = BeanUtil.copyProperties(apiErrorLog, ApiErrorLogCreateReqDTO.class);
        apiErrorLogService.createApiErrorLog(reqDTO);
    }

}
