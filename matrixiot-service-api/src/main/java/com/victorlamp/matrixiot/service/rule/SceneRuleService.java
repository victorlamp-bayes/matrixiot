package com.victorlamp.matrixiot.service.rule;

import com.victorlamp.matrixiot.service.common.response.PaginationResponseDTO;
import com.victorlamp.matrixiot.service.common.response.ResponseDTO;
import com.victorlamp.matrixiot.service.rule.dto.SceneRuleDTO;
import com.victorlamp.matrixiot.service.rule.dto.SceneRuleResponseDTO;
import com.victorlamp.matrixiot.service.rule.entity.ExecutionLog;
import com.victorlamp.matrixiot.service.rule.vo.SceneRuleVO;

import java.util.Set;

public interface SceneRuleService {

    /**
     * 创建场景规则
     *
     * @param dto 场景规则传输对象
     * @return: com.victorlamp.matrixiot.service.rule.dto.SceneRuleResponseDTO
     * @throw: ServiceException
     * @author: Dylan-孙林
     * @Date: 2023/8/23
     */
    ResponseDTO<SceneRuleResponseDTO> createSceneRule(SceneRuleDTO dto);

    /**
     * 根据Id查询场景规则
     *
     * @param ruleId 场景规则ID
     * @return: com.victorlamp.matrixiot.service.rule.vo.SceneRuleVO
     * @throw: ServiceException
     * @author: Dylan-孙林
     * @Date: 2023/8/23
     */
    ResponseDTO<SceneRuleVO> describeSceneRule(String ruleId);

    /**
     * 查询场景规则列表
     *
     * @param pageSize 页面记录数
     * @param pageNum  页数，从1开始
     * @return: List
     * @throw: ServiceException
     * @author: Dylan-孙林
     * @Date: 2023/8/23
     */
    PaginationResponseDTO<SceneRuleVO> listSceneRule(
            Integer pageSize,
            Integer pageNum,
            String ruleName,
            Integer ruleStatus);

    /**
     * 更新1条场景规则
     *
     * @param sceneRuleDTO 场景规则ID
     * @return: com.victorlamp.matrixiot.service.rule.dto.SceneRuleResponseDTO
     * @throws: ServiceException
     * @author: Dylan-孙林
     * @Date: 2023/8/24
     */
    ResponseDTO<SceneRuleResponseDTO> updateSceneRule(SceneRuleDTO sceneRuleDTO);

    /**
     * 删除1条场景规则
     *
     * @param ruleId 场景规则ID
     * @return: java.lang.Boolean  true: 成功删除
     * @throw: ServiceException
     * @author: Dylan-孙林
     * @Date: 2023/8/24
     */
    ResponseDTO deleteSceneRule(String ruleId);

    /**
     * 启用场景规则
     *
     * @param ruleId 规则ID
     * @return: java.lang.Boolean  true: 成功
     * @throws:
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    ResponseDTO enableSceneRule(String ruleId);

    /**
     * 禁用场景规则，如果有正在调度的定时器规则，会停止调度
     *
     * @param ruleId 规则ID
     * @return: java.lang.Boolean  true: 成功
     * @throws:
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    ResponseDTO disableSceneRule(String ruleId);

    /**
     * 立刻触发一次场景规则
     *
     * @param ruleId 规则ID
     * @return: java.lang.Boolean  true: 成功
     * @throws: ServiceException   规则不存在或者触发场景规则失败
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    ResponseDTO triggerSceneRule(String ruleId);

    /**
     * 重新执行失败的动作，需要指定规则执行的批次
     * 1 * 场景联动 -> n * 动作，动作是独立执行，失败不会影响其他动作，对于失败的动作，可以进行重试；
     * 规则动作的执行日志见 {@link ExecutionLog}
     *
     * @param ruleId  规则ID
     * @param batchNo 执行日志批次号，从执行日志中获取
     * @return: java.lang.Boolean  true: 成功
     * @throws: ServiceException   规则不存在或者未执行过失败
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    ResponseDTO retrySceneRule(String ruleId, String batchNo);

    /**
     * 指定要重试的执行 id 集合,重新执行失败的动作
     * 1 * 场景联动 -> n * 动作，动作是独立执行，失败不会影响其他动作，对于失败的动作，可以进行重试；
     * 规则动作的执行日志见 {@link ExecutionLog}
     *
     * @param executionLogIds 执行日志 id 集合
     * @return: java.lang.Boolean  true: 成功
     * @throws: ServiceException   规则不存在或者未执行过失败
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    ResponseDTO retrySceneRule(Set<String> executionLogIds);
}
