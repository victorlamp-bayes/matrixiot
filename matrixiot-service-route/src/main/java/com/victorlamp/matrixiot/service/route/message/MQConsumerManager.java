package com.victorlamp.matrixiot.service.route.message;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.route.DataRouteService;
import com.victorlamp.matrixiot.service.route.dto.DataRouteUpdateStatusReqDTO;
import com.victorlamp.matrixiot.service.route.entity.DataRoute;
import com.victorlamp.matrixiot.service.route.enums.DataRouteStatusEnum;
import com.victorlamp.matrixiot.service.route.enums.DataSourceTopicEnum;
import com.victorlamp.matrixiot.service.route.utils.route.DataSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.route.constant.ErrorCodeConstants.*;

@Component
@Slf4j
public class MQConsumerManager {
    private final Cache<String, DefaultMQPushConsumer> consumers = Caffeine.newBuilder().build();

    @Resource
    private DataRouteService dataRouteService;

    @Value("${rocketmq.name-server}")
    private String nameServer;

    public void batchRegisterMQConsumer(List<DataRoute> dataRoutes) {
        for (DataRoute dataRoute : dataRoutes) {
            registerMQConsumer(dataRoute);
        }
    }

    public void registerMQConsumer(DataRoute dataRoute) {
        String topic = EnumUtil.getFieldBy(DataSourceTopicEnum::getTopic, DataSourceTopicEnum::getId, dataRoute.getSource().getTopicId());
        log.info("开始注册数据路由[{}]消费者", dataRoute.getId());

        String msgTag = StrUtil.format("pid='{}'", dataRoute.getSource().getProductId());
        if (!StrUtil.isNullOrUndefined(dataRoute.getSource().getThingId())) {
            msgTag += StrUtil.format(" AND tid='{}'", dataRoute.getSource().getThingId());
        }

        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
            consumer.setNamesrvAddr(nameServer);
            consumer.setConsumerGroup(topic + "_" + dataRoute.getId());
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            consumer.subscribe(topic, MessageSelector.bySql(msgTag));
            // 消费超时时间
            consumer.setConsumeTimeout(5);
            // 拉取消息数量
            consumer.setPullBatchSize(10);
            // 线程配置
            consumer.setConsumeThreadMin(10);
            consumer.setConsumeThreadMax(10);
            // 关闭时无须等待消息消费
            consumer.setAwaitTerminationMillisWhenShutdown(0);

            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> sendData(msgs, dataRoute.getId()));

            // 将消费者注册到集合
            consumers.put(dataRoute.getId(), consumer);
            consumer.start();
        } catch (MQClientException e) {
            log.info("数据路由[{}]监听器注册失败，errmsg = {}", dataRoute.getId(), e.getMessage());
            throw exception(DATA_ROUTE_CREATE_FAILED);
        }
    }

    private ConsumeConcurrentlyStatus sendData(List<MessageExt> msgs, String routeId) {
        DataRoute dataRoute = dataRouteService.getDataRoute(routeId);

        if (dataRoute == null) {
            stopAndRemoveConsumer(routeId);
            log.info("数据路由[{}]: {}", routeId, DATA_ROUTE_NOT_EXISTS.getMsg());
            throw exception(DATA_ROUTE_NOT_EXISTS);
        }

        // 异常或者停用的数据路由停止流转
        if (StrUtil.equals(dataRoute.getStatus(), DataRouteStatusEnum.STOP.name()) ||
                StrUtil.equals(dataRoute.getStatus(), DataRouteStatusEnum.ABNORMAL.name())) {
            stopAndRemoveConsumer(routeId);
            log.info("数据路由[{}]: {}", routeId, DATA_ROUTE_STATUS_IS_NOT_RUNNING.getMsg());
            throw exception(DATA_ROUTE_STATUS_IS_NOT_RUNNING);
        }


        for (MessageExt msg : msgs) {
            ThingData thingData = JSONObject.parseObject(new String(msg.getBody(), StandardCharsets.UTF_8), ThingData.class);
            log.info("数据路由接收到数据");
            log.debug("{}", thingData);

            try {
                DataSender.sendData(dataRoute, thingData);
            } catch (Exception e) {
                log.error("数据路由异常，data={}, msg={}", thingData, e.getMessage());
//            setAbnormalStatus(routeId);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

    }

    public void stopAndRemoveConsumer(String routeId) {
        DefaultMQPushConsumer consumer = consumers.getIfPresent(routeId);
        if (consumer != null) {
            consumer.shutdown();
            consumers.invalidate(routeId);
        }
    }

    public void setAbnormalStatus(String id) {
        dataRouteService.updateDataRouteStatus(new DataRouteUpdateStatusReqDTO(id, DataRouteStatusEnum.ABNORMAL.name()));
        stopAndRemoveConsumer(id);
    }
}
