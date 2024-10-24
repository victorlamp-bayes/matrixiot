package com.victorlamp.matrixiot.service.system.api.logger;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.system.controller.logger.vo.operatelog.OperateLogPageReqVO;
import com.victorlamp.matrixiot.service.system.dto.logger.OperateLogCreateReqDTO;
import com.victorlamp.matrixiot.service.system.entity.logger.OperateLogDO;

/**
 * 操作日志 Service 接口
 *
 * @author 芋道源码
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 操作日志请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO);

}
