package com.victorlamp.matrixiot.service.agent.message;

import cn.hutool.core.util.ObjUtil;
import com.victorlamp.matrixiot.service.agent.service.ThingAgentService;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.common.constant.ThingTopicConsumerGroup;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RequiredArgsConstructor
@RocketMQMessageListener(topic = ThingTopic.THING_SERVICE_INVOKE, consumerGroup = ThingTopicConsumerGroup.AgentConsumer + "_" + ThingTopic.THING_SERVICE_INVOKE)
public class ThingServiceInvokeConsumer implements RocketMQListener<ThingData> {
    @Resource
    private ThingAgentService thingAgentService;

    @Override
    public void onMessage(ThingData thingData) {
        if (thingData == null || !ObjUtil.isNotNull(thingData.getService())) {
            log.warn("服务调用消息内容空");
            return;
        }

        log.info("消费服务调用消息[{}]", thingData.getThingId());

        // TODO 实现发送请求
        thingAgentService.sendCommand(thingData);
    }
}
