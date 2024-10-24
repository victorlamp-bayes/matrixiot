package com.victorlamp.matrixiot.service.agent.message;

import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.common.constant.ThingTopicConsumerGroup;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = ThingTopic.THING_PROPERTY_EVENT_POST_REPLY, consumerGroup = ThingTopicConsumerGroup.AgentConsumer + "_" + ThingTopic.THING_PROPERTY_EVENT_POST_REPLY)
public class ThingPropertyEventPostReplyConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        // TODO 根据具体情况判断，是否将云端响应消息发送给设备
    }
}
