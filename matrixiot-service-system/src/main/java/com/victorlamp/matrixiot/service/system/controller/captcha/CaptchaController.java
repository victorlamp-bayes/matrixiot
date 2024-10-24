package com.victorlamp.matrixiot.service.system.controller.captcha;

import cn.hutool.core.util.StrUtil;
import com.victorlamp.matrixiot.common.util.servlet.ServletUtils;
import com.victorlamp.matrixiot.service.framework.operatelog.core.annotation.OperateLog;
import com.xingyuv.captcha.model.common.ResponseModel;
import com.xingyuv.captcha.model.vo.CaptchaVO;
import com.xingyuv.captcha.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 验证码
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 验证码")
@RestController("adminCaptchaController")
@RequestMapping("/api/v1/system/captcha")
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    public static String getRemoteId(HttpServletRequest request) {
        String ip = ServletUtils.getClientIP(request);
        String ua = request.getHeader("user-agent");
        if (StrUtil.isNotBlank(ip)) {
            return ip + ua;
        }
        return request.getRemoteAddr() + ua;
    }

    @PostMapping({"/get"})
    @Operation(summary = "获得验证码")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public ResponseModel get(@RequestBody CaptchaVO data, HttpServletRequest request) {
        assert request.getRemoteHost() != null;
        data.setBrowserInfo(getRemoteId(request));
        return captchaService.get(data);
    }

    @PostMapping("/check")
    @Operation(summary = "校验验证码")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public ResponseModel check(@RequestBody CaptchaVO data, HttpServletRequest request) {
        data.setBrowserInfo(getRemoteId(request));
        return captchaService.check(data);
    }

}
