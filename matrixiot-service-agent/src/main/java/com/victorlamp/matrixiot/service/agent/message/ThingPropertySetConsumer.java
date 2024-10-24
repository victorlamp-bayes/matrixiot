package com.victorlamp.matrixiot.service.agent.message;

import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.common.constant.ThingTopicConsumerGroup;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@RocketMQMessageListener(topic = ThingTopic.THING_PROPERTY_SET, consumerGroup = ThingTopicConsumerGroup.AgentConsumer + "_" + ThingTopic.THING_PROPERTY_SET)
public class ThingPropertySetConsumer implements RocketMQListener<ThingData> {
    @Override
    public void onMessage(ThingData thingData) {
//        if (thingData == null || CollUtil.size(thingData.getServices()) < 0) {
//            log.warn("属性设置消息为空");
//            return;
//        }
//
//        log.info("消费属性设置消息[{}]", thingData.getThingId());

        // TODO 实现发送请求
//        thirdPartyService.sendCommand(thingData);
    }
}
