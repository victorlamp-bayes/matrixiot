package com.victorlamp.matrixiot.service.rule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SceneRuleResponseDTO {

    /** 规则 ID */
    private String ruleId;

    /** 规则的资源名称 */
    private String ruleRn;

    /** 规则名称 */
    private String ruleName;
}
