package com.victorlamp.matrixiot.service.rule.vo;

import com.victorlamp.matrixiot.service.rule.enums.RuleType;
import com.victorlamp.matrixiot.service.rule.obj.Action;
import com.victorlamp.matrixiot.service.rule.obj.Condition;
import com.victorlamp.matrixiot.service.rule.obj.Trigger;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneRuleVO {

    /**
     * id
     */
    private String ruleId;

    /**
     * 资源名称
     */
    private String ruleRn;

    /**
     * 规则名
     */
    private String ruleName;

    /**
     * 规则内容
     */
    private RuleContentVO ruleContent;

    /**
     * 规则描述
     */
    private String ruleDescription;

    /**
     * 启用状态：0-启用；1-禁用
     */
    @Schema(description = "启用状态：0-启用；1-禁用", allowableValues = {"0", "1"})
    private Integer ruleStatus;

    /**
     * 规则内容的显示对象
     *
     * @author: Dylan
     * @date: 2023/8/30
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RuleContentVO {

        /**
         * 规则类型
         */
        private RuleType type;

        /**
         * 规则触发器
         */
        @NotNull
        private Trigger trigger;

        /**
         * 规则执行条件
         */
        @NotNull
        private Condition condition;

        /**
         * 规则执行动作
         */
        @NotNull
        private List<Action> action;
    }
}
