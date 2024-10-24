package com.victorlamp.matrixiot.service.management.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ThingMonitorTask implements ApplicationRunner {

    @Resource
    private ThingService thingService;
    @Resource
    private TaskExecutor taskExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startScheduledTask();
    }

    // 暂定每小时执行一次，保证实时性，又不会对系统造成太大负担，异步执行；
    @Async
    @Scheduled(fixedRate = 3600 * 1000)
    public void startScheduledTask() {
        ThingPageReqDTO pageReqDTO = new ThingPageReqDTO();
        pageReqDTO.setPageNo(1);
        pageReqDTO.setPageSize(200);
        pageReqDTO.setEnabled(true);

        PageResult<Thing> thingPageResult = thingService.listThingPage(pageReqDTO);

        while (CollUtil.isNotEmpty(thingPageResult.getList())) {
            // 遍历设备列表，进行实时监控
            batchProcessThing(thingPageResult.getList());

            pageReqDTO.setPageNo(thingPageResult.getPageNo() + 1);
            thingPageResult = thingService.listThingPage(pageReqDTO);
        }
    }

    private void batchProcessThing(List<Thing> things) {
        for (Thing thing : things) {
            taskExecutor.execute(() -> {
                try {
                    setThingToOffline(thing);
                } catch (Exception e) {
                    log.info("设备状态更新失败[{}]", thing.getId());
                }
            });
        }
    }

    public void setThingToOffline(Thing thing) {
        if (ObjUtil.isNull(thing.getConnectedAt())) {
            return;
        }

        // 超过1天未上报数据的设备，设置为离线
        if (DateUtil.betweenDay(DateUtil.date(), DateUtil.date(thing.getConnectedAt()), true) >= 2) {
            // 已离线设备，不重复设置
            if (thing.getDisconnectedAt() != null) {
                return;
            }

            thingService.updateThingOnlineStatus(thing.getId(), false);
            log.info("设备离线[{}]", thing.getId());
        }
    }


}
