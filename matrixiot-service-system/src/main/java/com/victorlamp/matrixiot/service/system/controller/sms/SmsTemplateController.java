package com.victorlamp.matrixiot.service.system.controller.sms;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.object.BeanUtils;
import com.victorlamp.matrixiot.service.system.api.sms.SmsSendService;
import com.victorlamp.matrixiot.service.system.api.sms.SmsTemplateService;
import com.victorlamp.matrixiot.service.system.controller.sms.vo.template.SmsTemplatePageReqVO;
import com.victorlamp.matrixiot.service.system.controller.sms.vo.template.SmsTemplateRespVO;
import com.victorlamp.matrixiot.service.system.controller.sms.vo.template.SmsTemplateSaveReqVO;
import com.victorlamp.matrixiot.service.system.controller.sms.vo.template.SmsTemplateSendReqVO;
import com.victorlamp.matrixiot.service.system.entity.sms.SmsTemplateDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 短信模板")
@RestController
@RequestMapping("/api/v1/system/sms-template")
public class SmsTemplateController {

    @Resource
    private SmsTemplateService smsTemplateService;
    @Resource
    private SmsSendService smsSendService;

    @PostMapping("/create")
    @Operation(summary = "创建短信模板")
    //@PreAuthorize("@ss.hasPermission('system:sms-template:create')")
    public CommonResult<Long> createSmsTemplate(@Valid @RequestBody SmsTemplateSaveReqVO createReqVO) {
        return success(smsTemplateService.createSmsTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新短信模板")
    //@PreAuthorize("@ss.hasPermission('system:sms-template:update')")
    public CommonResult<Boolean> updateSmsTemplate(@Valid @RequestBody SmsTemplateSaveReqVO updateReqVO) {
        smsTemplateService.updateSmsTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除短信模板")
    @Parameter(name = "id", description = "编号", required = true)
    //@PreAuthorize("@ss.hasPermission('system:sms-template:delete')")
    public CommonResult<Boolean> deleteSmsTemplate(@RequestParam("id") Long id) {
        smsTemplateService.deleteSmsTemplate(id);
        return success(true);
    }

    @GetMapping("/query")
    @Operation(summary = "获得短信模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    //@PreAuthorize("@ss.hasPermission('system:sms-template:query')")
    public CommonResult<SmsTemplateRespVO> getSmsTemplate(@RequestParam("id") Long id) {
        SmsTemplateDO template = smsTemplateService.getSmsTemplate(id);
        return success(BeanUtils.toBean(template, SmsTemplateRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得短信模板分页")
    //@PreAuthorize("@ss.hasPermission('system:sms-template:query')")
    public CommonResult<PageResult<SmsTemplateRespVO>> getSmsTemplatePage(@Valid SmsTemplatePageReqVO pageVO) {
        PageResult<SmsTemplateDO> pageResult = smsTemplateService.getSmsTemplatePage(pageVO);
        return success(BeanUtils.toBean(pageResult, SmsTemplateRespVO.class));
    }

    @PostMapping("/send-sms")
    @Operation(summary = "发送短信")
    //@PreAuthorize("@ss.hasPermission('system:sms-template:send-sms')")
    public CommonResult<Long> sendSms(@Valid @RequestBody SmsTemplateSendReqVO sendReqVO) {
        return success(smsSendService.sendSingleSmsToAdmin(sendReqVO.getMobile(), null,
                sendReqVO.getTemplateCode(), sendReqVO.getTemplateParams()));
    }

}
