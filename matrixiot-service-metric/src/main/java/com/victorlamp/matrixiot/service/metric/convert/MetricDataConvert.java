package com.victorlamp.matrixiot.service.metric.convert;

import com.victorlamp.matrixiot.service.metric.controller.vo.MetricDataReqVO;
import com.victorlamp.matrixiot.service.metric.controller.vo.SystemMetricDataRespVO;
import com.victorlamp.matrixiot.service.metric.dto.SystemMetricDataReqDTO;
import com.victorlamp.matrixiot.service.metric.entity.SystemMetricData;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MetricDataConvert {
    MetricDataConvert INSTANCE = Mappers.getMapper(MetricDataConvert.class);

    SystemMetricDataReqDTO toDTO(MetricDataReqVO reqVO);

    SystemMetricDataRespVO toVO(SystemMetricData entity);

    List<SystemMetricDataRespVO> toVO(List<SystemMetricData> list);
}
