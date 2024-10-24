package com.victorlamp.matrixiot.service.management.message;

import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.common.constant.ThingTopicConsumerGroup;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingCreateReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
@RocketMQMessageListener(topic = ThingTopic.THIRD_DEVICE_REGISTER_REPLY, consumerGroup = ThingTopicConsumerGroup.ManagementConsumer + "_" + ThingTopic.THIRD_DEVICE_REGISTER_REPLY)
public class ThirdDeviceRegisterReplyConsumer implements RocketMQListener<ThingCreateReqDTO> {
    private final ThingService thingService;

    @Override
    public void onMessage(ThingCreateReqDTO message) {
        thingService.createThing(message);
    }
}
