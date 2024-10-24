package com.victorlamp.matrixiot.service.rule.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.victorlamp.matrixiot.service.rule.enums.RuleConditionType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;

/**
 * TCA 模型里的条件类
 *
 * @author: Dylan
 * @date: 2023/8/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Condition {

    /**
     * uri
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\-/]+$")
    private String uri;

    /**
     * 参数
     * 当条件为组合条件  {@link RuleConditionType#LOGICAL_AND} 时为空
     * 其他情况不能为空
     */
    @JsonIgnoreProperties
    @Size(min = 1)

    @ArraySchema(minItems = 1, arraySchema = @Schema(description = "当条件为组合条件时，params 为空，其他情况不能为空"))
    private HashMap params;

    /**
     * 条件列表，内部为其他条件
     * 当条件为组合条件 {@link RuleConditionType#LOGICAL_AND} 时不能为空
     * 其他情况为空
     */
    @JsonIgnoreProperties
    @Size(min = 1)

    @ArraySchema(minItems = 1, arraySchema = @Schema(description = "当条件为组合条件时，items 不能为空，其他情况为空", allOf = Condition.class))
    private List<Condition> items;
}
