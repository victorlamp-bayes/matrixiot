package com.victorlamp.matrixiot.service.rule.mapper;

import com.victorlamp.matrixiot.service.rule.dto.SceneRuleDTO;
import com.victorlamp.matrixiot.service.rule.obj.RuleContent;
import com.victorlamp.matrixiot.service.rule.vo.SceneRuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 规则内容转换映射器
 *
 * @author: Dylan-孙林
 * @Date: 2023/8/30
 */
@Mapper
public interface RuleContentMapper {

    /**
     * DTO -> 对象
     * @param ruleContentDTO  规则内容数据传输对象
     * @return: com.victorlamp.matrixiot.service.rule.obj.RuleContent
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    @Mapping(source = "type", target = "type")
    @Mapping(source = "trigger", target = "trigger")
    @Mapping(source = "condition", target = "condition")
    @Mapping(source = "action", target = "action")
    @Mapping(ignore = true, target = "drl")
    RuleContent toEntity(SceneRuleDTO.RuleContentDTO ruleContentDTO);

    /**
     * 对象 -> VO
     * @param ruleContent  规则内容
     * @return: com.victorlamp.matrixiot.service.rule.vo.SceneRuleVO.RuleContentVO
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    @Mapping(source = "type", target = "type")
    @Mapping(source = "trigger", target = "trigger")
    @Mapping(source = "condition", target = "condition")
    @Mapping(source = "action", target = "action")
    SceneRuleVO.RuleContentVO toVO(RuleContent ruleContent);
}
