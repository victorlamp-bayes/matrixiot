package com.victorlamp.matrixiot.service.rule.mapper;

import com.victorlamp.matrixiot.service.rule.dto.ExecutionLogDTO;
import com.victorlamp.matrixiot.service.rule.entity.ExecutionLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 规则内容转换映射器
 *
 * @author: Dylan-孙林
 * @Date: 2023/8/30
 */
@Mapper
public interface ExecutionLogMapper {

    /**
     * DTO -> entity
     * @param executionLogDTO
     * @return: com.victorlamp.matrixiot.service.rule.entity.ExecutionLog
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "ruleId", target = "ruleId")
    @Mapping(source = "ruleName", target = "ruleName")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "errorMessage", target = "errorMessage")
    @Mapping(target = "executeTime", expression = "java(executionLogDTO.getExecuteTime().getTime())")
    @Mapping(ignore = true, target = "createAt")
    @Mapping(ignore = true, target = "deleted")
    @Mapping(ignore = true, target = "deletedAt")
    ExecutionLog toEntity(ExecutionLogDTO executionLogDTO);

    /**
     * entity -> DTO
     * @param executionLog
     * @return: com.victorlamp.matrixiot.service.rule.dto.ExecutionLogDTO
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "ruleId", target = "ruleId")
    @Mapping(source = "ruleName", target = "ruleName")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "errorMessage", target = "errorMessage")
    @Mapping(target = "executeTime", expression = "java(new java.util.Date(executionLog.getExecuteTime()))")
    ExecutionLogDTO toDTO(ExecutionLog executionLog);

    /**
     * List<entity> -> List<DTO>
     * @param executionLog
     * @return: com.victorlamp.matrixiot.service.rule.dto.ExecutionLogDTO
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    List<ExecutionLogDTO> toDTO(List<ExecutionLog> executionLog);
}
