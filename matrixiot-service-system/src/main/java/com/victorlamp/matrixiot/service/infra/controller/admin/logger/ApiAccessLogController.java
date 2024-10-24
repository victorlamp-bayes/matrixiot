package com.victorlamp.matrixiot.service.infra.controller.admin.logger;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.object.BeanUtils;
import com.victorlamp.matrixiot.service.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import com.victorlamp.matrixiot.service.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import com.victorlamp.matrixiot.service.infra.dal.dataobject.logger.ApiAccessLogDO;
import com.victorlamp.matrixiot.service.infra.service.logger.ApiAccessLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - API 访问日志")
@RestController
@RequestMapping("/api/v1/infra/api-access-log")
@Validated
public class ApiAccessLogController {

    @Resource
    private ApiAccessLogService apiAccessLogService;

    @GetMapping("/list")
    @Operation(summary = "获得API 访问日志分页")
    public CommonResult<PageResult<ApiAccessLogRespVO>> getApiAccessLogPage(@Valid ApiAccessLogPageReqVO pageReqVO) {
        PageResult<ApiAccessLogDO> pageResult = apiAccessLogService.getApiAccessLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ApiAccessLogRespVO.class));
    }
}
