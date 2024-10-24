package com.victorlamp.matrixiot.service.metric;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.metric.dto.MetricCreateReqDTO;
import com.victorlamp.matrixiot.service.metric.dto.MetricPageReqDTO;
import com.victorlamp.matrixiot.service.metric.dto.SystemMetricDataReqDTO;
import com.victorlamp.matrixiot.service.metric.entity.Metric;
import com.victorlamp.matrixiot.service.metric.entity.SystemMetricData;

import java.util.List;

public interface MetricService {

    Metric createMetric(MetricCreateReqDTO reqDTO);

    PageResult<Metric> listMetricPage(MetricPageReqDTO reqDTO);

//    void updateMetric(String id);

    void deleteMetric(String Id);

    List<SystemMetricData> listSystemMetricData(SystemMetricDataReqDTO reqDTO);

//    ResponseDTO<MetricDataDTO> getLatestMetricData(String metricId);
}