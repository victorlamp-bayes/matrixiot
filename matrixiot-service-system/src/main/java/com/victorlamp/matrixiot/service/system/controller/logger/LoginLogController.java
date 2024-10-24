package com.victorlamp.matrixiot.service.system.controller.logger;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.object.BeanUtils;
import com.victorlamp.matrixiot.service.system.api.logger.LoginLogService;
import com.victorlamp.matrixiot.service.system.controller.logger.vo.loginlog.LoginLogPageReqVO;
import com.victorlamp.matrixiot.service.system.controller.logger.vo.loginlog.LoginLogRespVO;
import com.victorlamp.matrixiot.service.system.entity.logger.LoginLogDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 登录日志")
@RestController
@RequestMapping("/api/v1/system/login-log")
@Validated
public class LoginLogController {

    @Resource
    private LoginLogService loginLogService;

    @GetMapping("/list")
    @Operation(summary = "获得登录日志分页列表")
    @PreAuthorize("@ss.hasPermission('system:login-log:query')")
    public CommonResult<PageResult<LoginLogRespVO>> getLoginLogPage(@Valid LoginLogPageReqVO pageReqVO) {
        PageResult<LoginLogDO> pageResult = loginLogService.getLoginLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LoginLogRespVO.class));
    }

}
