package com.victorlamp.matrixiot.service.rule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.victorlamp.matrixiot.service.common.constant.SceneRuleConstant;
import com.victorlamp.matrixiot.service.rule.obj.Action;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * 场景联动规则的执行日志
 *
 * @author: Dylan
 * @date: 2023/10/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionLogDTO {

    /**
     * Id，创建时无需传入
     */
    private String id;

    /**
     * 执行的规则
     */
    @Schema(description = "场景联动ID")
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
    @Schema(description = "执行批次号；\n规则执行1次，如有n个动作，则对应n个执行日志，它们拥有相同批次号；对该批执行动作中，失败的项进行重试，仍然是同个批次号；\n同一规则执行2次，对应2批执行日志，有2个不同的批次号；")
    private String batchNo;

    /**
     * 执行的动作
     */
    @Schema(description = "执行的动作(单个)")
    private Action action;

    /**
     * 动作执行的状态
     * 0：未执行
     * 1: 执行中
     * 2: 执行成功
     * 9: 执行失败
     */
    @Schema(description = "执行状态：\n0：未执行\n1: 执行中\n2: 执行成功\n9: 执行失败", allowableValues = {"0", "1", "2", "9"})
    private Integer status;

    /**
     * 执行失败时的错误提示
     */
    @Schema(description = "执执行失败时的错误提示")
    private String errorMessage;

    /**
     * 执行时间（开始执行时的时间）
     */
    @Schema(description = "动作开始执行的时间，格式：yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = SceneRuleConstant.DEFAULT_TIME_ZONE)
    private Date executeTime;
}
