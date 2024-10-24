package com.victorlamp.matrixiot.service.rule.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 场景规则的列表页面 VO
 * @author: Dylan
 * @date: 2023/8/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneRulePageVO {

    /** 场景规则列表 */
    private List<SceneRuleVO> rules;

    /** 每页记录数 */
    private Integer pageSize;

    /** 页数 */
    private Integer pageNum;

    /** 记录总数 */
    private Integer total;
}
