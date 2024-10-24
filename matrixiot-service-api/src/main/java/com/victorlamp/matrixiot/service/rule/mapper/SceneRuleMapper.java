package com.victorlamp.matrixiot.service.rule.mapper;

import com.victorlamp.matrixiot.service.rule.dto.SceneRuleDTO;
import com.victorlamp.matrixiot.service.rule.dto.SceneRuleResponseDTO;
import com.victorlamp.matrixiot.service.rule.entity.SceneRule;
import com.victorlamp.matrixiot.service.rule.vo.SceneRuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface SceneRuleMapper {

    /**
     * DTO -> 实体对象
     *
     * @param sceneRuleDTO 场景规则数据传输对象
     * @return: com.victorlamp.matrixiot.service.rule.entity.SceneRule
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    @Mapping(source = "ruleName", target = "name")
    @Mapping(source = "ruleContent", target = "content")
    @Mapping(source = "ruleDescription", target = "description")
    @Mapping(source = "ruleId", target = "id")
    @Mapping(ignore = true, target = "rn")
    @Mapping(ignore = true, target = "status", defaultValue = "0")
    @Mapping(ignore = true, target = "deleted", defaultValue = "0")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "deletedAt")
    SceneRule toEntity(SceneRuleDTO sceneRuleDTO);

    /**
     * 实体对象 -> DTO
     *
     * @param sceneRule 场景规则实体对象
     * @return: com.victorlamp.matrixiot.service.rule.dto.SceneRuleResponseDTO
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    @Mapping(source = "id", target = "ruleId")
    @Mapping(source = "rn", target = "ruleRn")
    @Mapping(source = "name", target = "ruleName")
    SceneRuleResponseDTO toResponseDTO(SceneRule sceneRule);

    /**
     * 实体对象 -> vo
     *
     * @param sceneRule 实体对象
     * @return: com.victorlamp.matrixiot.service.rule.vo.SceneRuleVO
     * @author: Dylan-孙林
     * @Date: 2023/8/25
     */
    @Mapping(source = "id", target = "ruleId")
    @Mapping(source = "rn", target = "ruleRn")
    @Mapping(source = "name", target = "ruleName")
    @Mapping(source = "content", target = "ruleContent")
    @Mapping(source = "description", target = "ruleDescription")
    @Mapping(source = "status", target = "ruleStatus")
    SceneRuleVO toVO(SceneRule sceneRule);

    List<SceneRuleVO> toVO(List<SceneRule> sceneRulePage);
}
