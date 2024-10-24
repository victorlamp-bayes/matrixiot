package com.victorlamp.matrixiot.service.rule.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.victorlamp.matrixiot.service.rule.enums.RuleTriggerType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.List;

/**
 * TCA 模型里的触发器类
 *
 * @author: Dylan
 * @date: 2023/8/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Trigger {

    /**
     * uri
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\-/]+$")
    private String uri;

    /**
     * 参数
     * 当触发器为组合触发器 {@link RuleTriggerType#LOGICAL_OR} 时为空
     * 其他情况不能为空
     */
    @JsonIgnoreProperties
    @ArraySchema(minItems = 1, arraySchema = @Schema(description = "当触发器为组合触发器时，params 为空，其他情况不能为空"))

    private HashMap params;

    /**
     * 触发器列表
     * 当触发器为组合触发器 {@link RuleTriggerType#LOGICAL_OR} 时不能为空
     * 其他情况为空
     */
    @JsonIgnoreProperties
    @ArraySchema(minItems = 1, arraySchema = @Schema(description = "当触发器为组合触发器时，items 不能为空，其他情况为空", requiredMode = Schema.RequiredMode.REQUIRED, allOf = Trigger.class))
    private List<Trigger> items;
}
