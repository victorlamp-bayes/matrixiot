package com.victorlamp.matrixiot.service.framework.apilog.core.service;

import cn.hutool.core.bean.BeanUtil;
import com.victorlamp.matrixiot.service.infra.dto.ApiAccessLogCreateReqDTO;
import com.victorlamp.matrixiot.service.infra.service.logger.ApiAccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

/**
 * API 访问日志 Framework Service 实现类
 *
 * @author 芋道源码
 */
@RequiredArgsConstructor
public class ApiAccessLogFrameworkServiceImpl implements ApiAccessLogFrameworkService {

    private final ApiAccessLogService apiAccessLogService;

    @Override
    @Async
    public void createApiAccessLog(ApiAccessLog apiAccessLog) {
        ApiAccessLogCreateReqDTO reqDTO = BeanUtil.copyProperties(apiAccessLog, ApiAccessLogCreateReqDTO.class);
        apiAccessLogService.createApiAccessLog(reqDTO);
    }

}
