package com.victorlamp.matrixiot.service.system.controller.sms;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.object.BeanUtils;
import com.victorlamp.matrixiot.service.system.api.sms.SmsLogService;
import com.victorlamp.matrixiot.service.system.controller.sms.vo.log.SmsLogPageReqVO;
import com.victorlamp.matrixiot.service.system.controller.sms.vo.log.SmsLogRespVO;
import com.victorlamp.matrixiot.service.system.entity.sms.SmsLogDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 短信日志")
@RestController
@RequestMapping("/api/v1/system/sms-log")
@Validated
public class SmsLogController {

    @Resource
    private SmsLogService smsLogService;

    @GetMapping("/list")
    @Operation(summary = "获得短信日志分页")
    //@PreAuthorize("@ss.hasPermission('system:sms-log:query')")
    public CommonResult<PageResult<SmsLogRespVO>> getSmsLogPage(@Valid SmsLogPageReqVO pageReqVO) {
        PageResult<SmsLogDO> pageResult = smsLogService.getSmsLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SmsLogRespVO.class));
    }

}
