package com.victorlamp.matrixiot.service.metric.aggregation;

import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import com.victorlamp.matrixiot.service.metric.dao.MetricRepository;
import com.victorlamp.matrixiot.service.metric.entity.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationJob implements Job {
    public final static String METRIC_ID = "metricId";
    private final MetricRepository metricRepository;
    private final AggregationService aggregationService;
    private final AggregationSchedulerService aggregationSchedulerService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String metricId = jobDataMap.getString(METRIC_ID);

        Metric metric;
        try {
            metric = metricRepository.findById(metricId).orElse(null);
        } catch (Exception e) {
            log.error("监控指标查询Id异常，原因：" + e.getMessage());
            throw new ServiceException(ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_DATABASE, "监控指标查询Id"));
        }
        if (metric != null) {
            aggregationService.aggregateData(metric);
        } else {
            // metricId查询失败，清除原有的定时任务
            try {
                aggregationSchedulerService.stopAggregationTask(metricId);
            } catch (SchedulerException e) {
                log.error("停止定时任务失败，原因：" + e.getMessage());
            }
        }

    }
}
