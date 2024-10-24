package com.victorlamp.matrixiot.service.alarm.convert;

import com.victorlamp.matrixiot.service.alarm.controller.vo.AlarmUpdateStatusReqVO;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmCreateReqDTO;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmUpdateStatusReqDTO;
import com.victorlamp.matrixiot.service.alarm.entity.Alarm;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlarmConvert {
    AlarmConvert INSTANCE = Mappers.getMapper(AlarmConvert.class);

    Alarm toEntity(AlarmCreateReqDTO dto);

    AlarmUpdateStatusReqDTO toDTO(AlarmUpdateStatusReqVO reqVO);
}
