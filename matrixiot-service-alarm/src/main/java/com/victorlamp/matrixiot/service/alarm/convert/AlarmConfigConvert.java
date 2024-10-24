package com.victorlamp.matrixiot.service.alarm.convert;

import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigCreateReqDTO;
import com.victorlamp.matrixiot.service.alarm.entity.AlarmConfig;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlarmConfigConvert {

    AlarmConfigConvert INSTANCE = Mappers.getMapper(AlarmConfigConvert.class);
    
    AlarmConfig toEntity(AlarmConfigCreateReqDTO dto);
}
