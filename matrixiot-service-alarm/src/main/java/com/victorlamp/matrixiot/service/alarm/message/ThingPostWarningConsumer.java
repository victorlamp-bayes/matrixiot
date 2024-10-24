package com.victorlamp.matrixiot.service.alarm.message;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.victorlamp.matrixiot.service.alarm.AlarmService;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmCreateReqDTO;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmLevelEnum;
import com.victorlamp.matrixiot.service.alarm.enums.AlarmSendStatusEnum;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.common.constant.ThingTopicConsumerGroup;
import com.victorlamp.matrixiot.service.management.api.ThingModelService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingEventData;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelEvent;
import com.victorlamp.matrixiot.service.management.enums.ThingModelEventTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
@RocketMQMessageListener(topic = ThingTopic.THING_POST_WARNING, consumerGroup = ThingTopicConsumerGroup.AlarmConsumer + "_" + ThingTopic.THING_POST_WARNING)
public class ThingPostWarningConsumer implements RocketMQListener<ThingData> {

    private final AlarmService alarmService;

    @DubboReference
    private ThingService thingService;

    @DubboReference
    private ThingModelService thingModelService;

    @Override
    public void onMessage(ThingData message) {
        log.info("接收订阅消息:[{}]", ThingTopic.THING_POST_WARNING);
        log.debug("消息内容:{}", message);
        //获取到有事件的消息，将事件写入alarm数据库
        Thing thing = thingService.getThing(message.getThingId());

        List<ThingEventData> eventDataList = message.getEvents();

        for (ThingEventData eventData : eventDataList) {
            AlarmCreateReqDTO alarmCreateReqDTO = new AlarmCreateReqDTO();
            alarmCreateReqDTO.setThingId(message.getThingId());
            alarmCreateReqDTO.setProductId(message.getProductId());
            alarmCreateReqDTO.setStatus(false);

            // TODO 本地缓存ThingModel
            ThingModelEvent eventSpec = thingModelService.describeThingModelEvent(message.getProductId(), eventData.getIdentifier());
            alarmCreateReqDTO.setMessage(eventSpec.getName());
            alarmCreateReqDTO.setTimestamp(DateUtil.current());
            alarmCreateReqDTO.setSendStatus(AlarmSendStatusEnum.PENDING);
            AlarmLevelEnum level = AlarmLevelEnum.INFO;
            if (StrUtil.equalsIgnoreCase(eventData.getType(), ThingModelEventTypeEnum.WARNING.name())) {
                level = AlarmLevelEnum.WARNING;
            } else if (StrUtil.equalsIgnoreCase(eventData.getType(), ThingModelEventTypeEnum.ERROR.name())) {
                level = AlarmLevelEnum.MAJOR;
            }

            alarmCreateReqDTO.setLevel(level);
            alarmService.createAlarm(alarmCreateReqDTO);
        }
    }
}
