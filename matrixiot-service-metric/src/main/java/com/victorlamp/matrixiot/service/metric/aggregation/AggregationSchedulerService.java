package com.victorlamp.matrixiot.service.metric.aggregation;

import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import com.victorlamp.matrixiot.service.metric.dao.MetricRepository;
import com.victorlamp.matrixiot.service.metric.entity.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregationSchedulerService implements ApplicationRunner {

    private final Scheduler scheduler;

    private final MetricRepository metricRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 重启定时任务
        try {
            List<Metric> metricList = metricRepository.findAll();

            for (Metric metric : metricList) {
                startAggregationTask(metric);
            }

        } catch (Exception e) {
            log.error("监控指标重启定时任务失败，原因:" + e.getMessage());
        }
    }

    public void startAggregationTask(Metric metric) throws SchedulerException {
        // 构建任务
        JobDetail jobDetail = buildJobDetail(metric);
        // 构建触发器
        Trigger trigger = buildTrigger(metric);

        if (jobDetail == null || trigger == null) {
            log.error("定时任务构造失败");
            throw new ServiceException(ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "监控指标定时任务构造失败"));
        }

        try {
            JobKey jobKey = JobKey.jobKey(getJobDetailName(metric));
            TriggerKey triggerKey = TriggerKey.triggerKey(getTriggerName(metric));

            if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
                scheduler.rescheduleJob(triggerKey, trigger);
            } else {
                scheduler.scheduleJob(jobDetail, trigger);
            }

        } catch (SchedulerException e) {
            log.error("定时任务启动失败，原因：" + e.getMessage());
            throw new ServiceException(ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "监控指标定时任务启动失败"));
        }
    }

    public void stopAggregationTask(String metricId) throws SchedulerException {
        try {
            scheduler.unscheduleJob(new TriggerKey("aggregationTrigger-" + metricId));
        } catch (SchedulerException e) {
            log.error("定时任务停止失败，原因：" + e.getMessage());
        }
    }

    private JobDetail buildJobDetail(Metric metric) {
        String jobName = getJobDetailName(metric);
        JobDetail jobDetail = null;
        try {
            jobDetail = JobBuilder.newJob(AggregationJob.class)
                    .usingJobData(AggregationJob.METRIC_ID, metric.getId())
                    .withIdentity(jobName)
                    .build();
        } catch (Exception e) {
            log.error("定时任务构造失败，原因：" + e.getMessage());
        }

        return jobDetail;
    }

    private Trigger buildTrigger(Metric metric) {
        String triggerName = getTriggerName(metric);
        Trigger trigger = null;
        try {
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMinutes(metric.getAggregationFreq())
                            .repeatForever())
                    .startNow()
                    .build();
        } catch (Exception e) {
            log.error("定时任务构造失败，原因：" + e.getMessage());
        }

        return trigger;
    }

    private String getJobDetailName(Metric metric) {
        return "aggregationJob-" + metric.getId();
    }

    private String getTriggerName(Metric metric) {
        return "aggregationTrigger-" + metric.getId();
    }
}
