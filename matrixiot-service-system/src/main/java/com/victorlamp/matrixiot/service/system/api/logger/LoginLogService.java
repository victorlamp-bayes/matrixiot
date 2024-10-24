package com.victorlamp.matrixiot.service.system.api.logger;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.system.controller.logger.vo.loginlog.LoginLogPageReqVO;
import com.victorlamp.matrixiot.service.system.dto.logger.LoginLogCreateReqDTO;
import com.victorlamp.matrixiot.service.system.entity.logger.LoginLogDO;

import javax.validation.Valid;

/**
 * 登录日志 Service 接口
 */
public interface LoginLogService {

    /**
     * 获得登录日志分页
     *
     * @param pageReqVO 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO);

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateReqDTO reqDTO);

}
