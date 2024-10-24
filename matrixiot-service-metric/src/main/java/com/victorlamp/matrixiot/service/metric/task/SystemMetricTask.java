package com.victorlamp.matrixiot.service.metric.task;

import cn.hutool.core.date.DateUtil;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingStatistics;
import com.victorlamp.matrixiot.service.metric.dao.SystemMetricDataRepository;
import com.victorlamp.matrixiot.service.metric.entity.SystemMetricData;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class SystemMetricTask implements SchedulingConfigurer {

    private ScheduledTaskRegistrar taskRegistrar;

    @DubboReference
    private ThingService thingService;
    @Resource
    private SystemMetricDataRepository systemMetricDataRepository;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(2)); // 配置线程池
        this.taskRegistrar = taskRegistrar;
        this.addAllTasks();
    }

    private void addAllTasks() {
        this.addTask();
    }

    private void addTask() {
        // 添加拉取设备数据任务
        CronTask cronTask = new CronTask(this::thingCountTask, "0 */1 * * * ?"); // 每分钟执行一次
        ScheduledFuture<?> future = Objects.requireNonNull(taskRegistrar.getScheduler()).schedule(cronTask.getRunnable(), cronTask.getTrigger());
        assert future != null;
        log.info("[metric_thing_online] - 定时任务设置成功:监控在线设备数");
    }

    private void thingCountTask() {
        ThingStatistics thingStatistics = thingService.getStatistics(null);
        long timestamp = DateUtil.beginOfMinute(DateUtil.date()).getTime();
        SystemMetricData data = new SystemMetricData(timestamp, thingStatistics.getOnline());
        systemMetricDataRepository.insert(data);
    }
}
