package com.victorlamp.matrixiot.service.rule.configuration;

import com.victorlamp.matrixiot.service.rule.mapper.ExecutionLogMapper;
import com.victorlamp.matrixiot.service.rule.mapper.RuleContentMapper;
import com.victorlamp.matrixiot.service.rule.mapper.SceneRuleMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    public SceneRuleMapper getSceneRuleMapper() {

        return Mappers.getMapper(SceneRuleMapper.class);
    }

    @Bean
    public RuleContentMapper getRuleContentMapper() {

        return Mappers.getMapper(RuleContentMapper.class);
    }

    @Bean
    public ExecutionLogMapper getExecutionLogMapper() {

        return Mappers.getMapper(ExecutionLogMapper.class);
    }
}
