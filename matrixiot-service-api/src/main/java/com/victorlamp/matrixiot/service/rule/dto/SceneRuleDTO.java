package com.victorlamp.matrixiot.service.rule.dto;

import com.victorlamp.matrixiot.service.common.constant.ParamPattern;
import com.victorlamp.matrixiot.service.common.validation.groups.Create;
import com.victorlamp.matrixiot.service.common.validation.groups.Patch;
import com.victorlamp.matrixiot.service.rule.enums.RuleType;
import com.victorlamp.matrixiot.service.rule.obj.Action;
import com.victorlamp.matrixiot.service.rule.obj.Condition;
import com.victorlamp.matrixiot.service.rule.obj.Trigger;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 场景规则 DTO
 *
 * @author: Dylan
 * @date: 2023/8/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneRuleDTO {

    /**
     * 规则 ID
     */
    @NotBlank(message = "规则ID不能为空", groups = Patch.class)
    @Schema(description = "新增时不传；更新时必传；")
    private String ruleId;

    /**
     * 规则名
     */
    @NotBlank(message = "规则名称不能为空", groups = {Create.class})
    @Pattern(regexp = ParamPattern.NAME, groups = {Create.class, Patch.class})
    private String ruleName;

    /**
     * 规则内容
     */
    @NotNull(message = "规则内容不能为空", groups = {Create.class})
    @Valid
    private RuleContentDTO ruleContent;

    /**
     * 规则描述
     */
    @Size(message = "规则描述长度不能超过128位", max = 128, groups = {Create.class, Patch.class})
    private String ruleDescription;

    /**
     * 规则内容 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class RuleContentDTO {

        /**
         * 规则类型
         */
        @NotNull(message = "规则类型不能为空")
        private RuleType type;

        /**
         * 规则触发器
         */
        @NotNull(message = "触发器不能为空")
        private Trigger trigger;

        /**
         * 规则执行条件
         */
        @NotNull(message = "条件不能为空")
        private Condition condition;

        /**
         * 规则执行动作
         */
        @NotNull(message = "动作不能为空")
        @Size(message = "至少需要一个动作", min = 1)
        @ArraySchema(minItems = 1, arraySchema = @Schema(required = true))
        private List<Action> action;
    }
}
