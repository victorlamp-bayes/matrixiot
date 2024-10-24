package com.victorlamp.matrixiot.service.rule;

import com.victorlamp.matrixiot.service.common.response.PaginationResponseDTO;
import com.victorlamp.matrixiot.service.common.response.ResponseDTO;
import com.victorlamp.matrixiot.service.rule.dto.ExecutionLogDTO;

import java.util.List;
import java.util.Set;

/**
 * 规则的执行日志服务
 *
 * @author: Dylan
 * @date: 2023/10/18
 */
public interface ExecutionLogService {

    /**
     * 根据Id查询日志详情
     *
     * @param id
     * @return: com.victorlamp.matrixiot.service.common.response.ResponseDTO<com.victorlamp.matrixiot.service.rule.dto.ExecutionLogDTO>
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    ResponseDTO<ExecutionLogDTO> describeExecutionLog(String id);

    /**
     * 根据规则 Id 查询执行批次号列表
     *
     * @param ruleId
     * @return: com.victorlamp.matrixiot.service.common.response.ResponseDTO<com.victorlamp.matrixiot.service.rule.dto.ExecutionLogDTO>
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    ResponseDTO<List> listExecutionLogBatchNos(String ruleId);

    /**
     * 查询日志列表
     *
     * @param pageSize
     * @param pageNum
     * @param ruleId
     * @param startTime
     * @param endTime
     * @param statusSet
     * @return: com.victorlamp.matrixiot.service.common.response.PaginationResponseDTO<com.victorlamp.matrixiot.service.rule.dto.ExecutionLogDTO>
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    PaginationResponseDTO<ExecutionLogDTO> listExecutionLog(
            Integer pageSize,
            Integer pageNum,
            String ruleId,
            String startTime,
            String endTime,
            Set<Integer> statusSet);

    /**
     * 删除 id 匹配的日志
     *
     * @param id
     * @return: java.lang.Boolean 删除成功与否
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    ResponseDTO<ExecutionLogDTO> deleteExecutionLog(String id);

    /**
     * 删除 Id 在 idSet 的日志
     *
     * @param idSet
     * @return: java.lang.Integer 删除的数量
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    ResponseDTO<Integer> deleteExecutionLog(Set<String> idSet);
}
