package com.victorlamp.matrixiot.service.rule.entity;

import com.victorlamp.matrixiot.service.common.constant.SceneRuleConstant;
import com.victorlamp.matrixiot.service.rule.obj.RuleContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 场景规则实体类
 * TODO  未预留用户、组织等字段，列入系统层面的操作日志（等操作日志写完后删掉该 TODO）
 *
 * @author: Dylan
 * @date: 2023/8/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class SceneRule implements Serializable {

    private static final long serialVersionUID = 4531119530224355324L;

    /**
     * id
     */
    private String id;

    /**
     * 资源名称
     */
    @Indexed
    private String rn;

    /**
     * 规则名,只能为英文
     */
    @Indexed(unique = true)
    private String name;

    /**
     * 规则内容
     */
    private RuleContent content;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 启用状态：0-启用；1-停用
     */
    @Builder.Default
    private int status = SceneRuleConstant.ENABLED;

    /**
     * 逻辑删除字段: 0-未删除；1-已删除
     */
    @Builder.Default
    private int deleted = SceneRuleConstant.NOT_DELETED;

    @CreatedDate
    private Long createdAt;

    /**
     * 删除的时间戳
     */
    private Long deletedAt;
}
