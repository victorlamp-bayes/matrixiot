package com.victorlamp.matrixiot.service.system.service.logger;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.object.BeanUtils;
import com.victorlamp.matrixiot.service.system.api.logger.LoginLogService;
import com.victorlamp.matrixiot.service.system.controller.logger.vo.loginlog.LoginLogPageReqVO;
import com.victorlamp.matrixiot.service.system.dao.logger.LoginLogMapper;
import com.victorlamp.matrixiot.service.system.dto.logger.LoginLogCreateReqDTO;
import com.victorlamp.matrixiot.service.system.entity.logger.LoginLogDO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        loginLogMapper.insert(loginLog);
    }

}
