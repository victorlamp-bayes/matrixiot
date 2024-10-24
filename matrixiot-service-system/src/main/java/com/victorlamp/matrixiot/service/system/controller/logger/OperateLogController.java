package com.victorlamp.matrixiot.service.system.controller.logger;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.system.api.logger.OperateLogService;
import com.victorlamp.matrixiot.service.system.api.user.AdminUserService;
import com.victorlamp.matrixiot.service.system.controller.logger.vo.operatelog.OperateLogPageReqVO;
import com.victorlamp.matrixiot.service.system.controller.logger.vo.operatelog.OperateLogRespVO;
import com.victorlamp.matrixiot.service.system.convert.logger.OperateLogConvert;
import com.victorlamp.matrixiot.service.system.entity.logger.OperateLogDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;
import static com.victorlamp.matrixiot.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - 操作日志")
@RestController
@RequestMapping("/api/v1/system/operate-log")
@Validated
public class OperateLogController {

    @Resource
    private OperateLogService operateLogService;
    @Resource
    private AdminUserService userService;

    @GetMapping("/list")
    @Operation(summary = "查看操作日志分页列表")
//    @PreAuthorize("@ss.hasPermission('system:operate-log:query')")
    public CommonResult<PageResult<OperateLogRespVO>> pageOperateLog(@Valid OperateLogPageReqVO pageReqVO) {
        PageResult<OperateLogDO> pageResult = operateLogService.getOperateLogPage(pageReqVO);
        // 获得拼接需要的数据
        Map<Long, AdminUserDO> userMap = userService.getUserMap(
                convertList(pageResult.getList(), OperateLogDO::getUserId));
        return success(new PageResult<>(OperateLogConvert.INSTANCE.convertList(pageResult.getList(), userMap),
                pageResult.getTotal(), pageResult.getPageNo(), pageResult.getPageSize()));
    }

}
