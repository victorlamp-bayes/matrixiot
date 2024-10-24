package com.victorlamp.matrixiot.service.management.message;

import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.common.constant.ThingTopicConsumerGroup;
import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import com.victorlamp.matrixiot.service.management.dao.ThingDataRepository;
import com.victorlamp.matrixiot.service.management.dao.ThingRepository;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
@RocketMQMessageListener(topic = ThingTopic.THING_PROPERTY_SET_REPLY, consumerGroup = ThingTopicConsumerGroup.DataConsumer + "_" + ThingTopic.THING_PROPERTY_SET_REPLY)
public class ThingPropertySetReplyConsumer implements RocketMQListener<ThingData> {

    private final ThingDataRepository thingDataRepository;
    private final ThingRepository thingRepository;

    @Override
    public void onMessage(ThingData message) {
        // 存储设备响应的消息
        if (message == null) {
            log.error("设置设备属性MQ回复消息内容为空");
            throw new ServiceException(
                    ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "设置设备属性MQ回复消息内容为空"));
        }

        try {
            thingDataRepository.save(message);
        } catch (Exception e) {
            log.error("设置设备属性写入数据库失败：" + e.getMessage());
            throw new ServiceException(
                    ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_DATABASE, "设置设备属性写入"));
        }

        try {
            Thing thing = thingRepository.findById(message.getThingId()).orElse(null);
            assert thing != null;
            thing.setConnectedAt(message.getTimestamp());
            thing.setDisconnectedAt(null);
            thing.setOnline(true);
            thingRepository.save(thing);
        } catch (Exception e) {
            log.error("设置设备属性写入thingData数据库成功，更新设备状态写入thing数据库失败：" + e.getMessage());
            throw new ServiceException(
                    ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_DATABASE, "设置设备属性写入成功，更新设备状态写入数据库"));
        }
    }
}
