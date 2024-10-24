package com.victorlamp.matrixiot.service.management.message;

import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.common.constant.ThingTopicConsumerGroup;
import com.victorlamp.matrixiot.service.management.api.ThingDataService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RocketMQMessageListener(topic = ThingTopic.THING_PROPERTY_EVENT_POST, consumerGroup = ThingTopicConsumerGroup.DataConsumer + "_" + ThingTopic.THING_PROPERTY_EVENT_POST)
public class ThingPropertyEventPostConsumer implements RocketMQListener<ThingData> {

    @Resource
    private ThingDataService thingDataService;
    @Resource
    private ThingService thingService;

    @Override
    public void onMessage(ThingData message) {
        if (message == null) {
            log.warn("上报属性/事件消息内容为空");
            return;
        }

        log.info("消费上报属性/事件消息[{}]", message.getThingId());
        ThingData thingData = thingDataService.createThingData(message);

        if (thingData == null) {
            log.info("上报设备数据重复,未写入");
            return;
        }

        log.info("更新设备在线状态[{}]", thingData.getThingId());
        thingService.updateThingOnlineStatus(thingData.getThingId(), true);
    }
}
