package com.victorlamp.matrixiot.service.metric.convert;

import com.victorlamp.matrixiot.service.metric.dto.MetricCreateReqDTO;
import com.victorlamp.matrixiot.service.metric.entity.Metric;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MetricConvert {
    MetricConvert INSTANCE = Mappers.getMapper(MetricConvert.class);

    Metric toEntity(MetricCreateReqDTO reqDTO);
}
