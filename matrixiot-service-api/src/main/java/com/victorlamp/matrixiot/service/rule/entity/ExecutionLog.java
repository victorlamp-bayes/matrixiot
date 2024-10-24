package com.victorlamp.matrixiot.service.rule.entity;

import com.victorlamp.matrixiot.service.common.constant.SceneRuleConstant;
import com.victorlamp.matrixiot.service.rule.obj.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 场景联动规则的执行日志
 * 1个场景联动 -> n * 动作 -> n * 执行日志
 *
 * @author: Dylan
 * @date: 2023/10/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ExecutionLog implements Serializable {

    private static final long serialVersionUID = 1307114854917360784L;

    private String id;

    /**
     * 执行的规则
     */
    @Indexed
    private String ruleId;

    /**
     * 规则名的冗余字段
     */
    @Indexed
    private String ruleName;

    /**
     * 批次号
     * 同个规则执行1次，每个动作对应执行日志的批次号相同
     * 同个规则执行2次，批次号不同
     */
    @Indexed
    private String batchNo;

    /**
     * 执行的动作副本
     * 说明：执行时，已将 TRIGGER_DEVICE 替换为了实际的触发设备
     */
    private Action action;

    /**
     * 动作执行的状态
     * 0：未执行
     * 1: 执行中
     * 2: 执行成功
     * 9: 执行失败
     * 10:已重试
     */
    private Integer status;

    /**
     * 执行失败时的错误记录信息
     */
    private String errorMessage;

    /**
     * 执行时间
     */
    private Long executeTime;

    /**
     * 创建时间
     */
    @CreatedDate
    private Long createAt;

    /**
     * 逻辑删除字段: 0-未删除；1-已删除
     */
    @Builder.Default
    private int deleted = SceneRuleConstant.NOT_DELETED;

    /**
     * 删除的时间戳
     */
    private Long deletedAt;
}
