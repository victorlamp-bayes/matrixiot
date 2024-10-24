package com.victorlamp.matrixiot.configuration.message;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RocketMQTemplateProducer {

    private final RocketMQTemplate template;

    /**
     * 发送普通消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public <T> void sendMessage(String topic, T message) {
        Message<T> msg = MessageBuilder.withPayload(message).build();
        this.template.convertAndSend(topic, message);
        log.info("普通消息发送完成");
        log.debug("消息内容:\n{}", message);
    }

    /**
     * 发送同步消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void syncSendMessage(String topic, Object message) {
        SendResult sendResult = this.template.syncSend(topic, message);
        log.info("同步发送消息完成：sendResult = {}", sendResult);
        log.debug("消息内容:\n{}", message);
    }

    /**
     * 发送异步消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public <T> void asyncSendMessage(String topic, T message) {
        Message<T> msg = MessageBuilder.withPayload(message).build();
        this.template.asyncSend(topic, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功. [{}] [{}]", topic, sendResult.getSendStatus());
                log.debug("消息内容:\n{}", msg);
            }

            @Override
            public void onException(Throwable e) {
                log.info("异步消息发送异常，exception = {}", e.getMessage());
            }
        });
    }

    public <T> void asyncSendMessage(String topic, T message, String pid, String tid) {
        Message<T> msg = MessageBuilder.withPayload(message).setHeader("pid", pid).setHeader("tid", tid).build();

        this.template.asyncSend(topic, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功. [{}] [{}]", topic, sendResult.getSendStatus());
                log.debug("消息内容:\n{}", msg);
            }

            @Override
            public void onException(Throwable e) {
                log.info("异步消息发送异常，exception = {}", e.getMessage());
            }
        });
    }

    /**
     * 发送单向消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void sendOneWayMessage(String topic, Object message) {
        this.template.sendOneWay(topic, message);
        log.info("单向发送消息完成");
        log.debug("消息内容:\n{}", message);
    }

    /**
     * 同步发送批量消息
     *
     * @param topic       topic
     * @param messageList 消息集合
     * @param timeout     超时时间（毫秒）
     */
    public void syncSendMessages(String topic, List<Object> messageList, long timeout) {
        this.template.syncSend(topic, messageList, timeout);
        log.info("同步发送批量消息完成");
        log.debug("消息内容:\n{}", JSON.toJSONString(messageList));
    }

    /**
     * 发送事务消息
     *
     * @param topic   topic
     * @param message 消息对象
     */
    public void sendMessageInTransaction(String topic, Object message) {
        String transactionId = UUID.randomUUID().toString();
        TransactionSendResult result = this.template.sendMessageInTransaction(topic, MessageBuilder.withPayload(message)
                .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                .build(), message);
        log.info("发送事务消息（半消息）完成：result = {}", result);

    }

    /**
     * 发送携带 tag 的消息（过滤消息）
     *
     * @param topic   topic，RocketMQTemplate将 topic 和 tag 合二为一了，底层会进行
     *                拆分再组装。只要在指定 topic 时跟上 {:tags} 就可以指定tag
     *                例如 test-topic:tagA
     * @param message 消息体
     */
    public void syncSendMessageWithTag(String topic, Object message) {
        this.template.syncSend(topic, message);
        log.info("发送带 tag 的消息完成");
        log.debug("消息内容:\n{}", message);
    }

    /**
     * 同步发送延时消息
     *
     * @param topic      topic
     * @param message    消息体
     * @param timeout    超时
     * @param delayLevel 延时等级：现在RocketMq并不支持任意时间的延时，需要设置几个固定的延时等级，
     *                   从1s到2h分别对应着等级 1 到 18，消息消费失败会进入延时消息队列
     *                   "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
     */
    public void syncSendDelay(String topic, Object message, long timeout, int delayLevel) {
        this.template.syncSend(topic, MessageBuilder.withPayload(message).build(), timeout, delayLevel);
        log.info("已同步发送延时消息");
        log.debug("消息内容:\n{}", message);
    }

    /**
     * 异步发送延时消息
     *
     * @param topic      topic
     * @param message    消息对象
     * @param timeout    超时时间
     * @param delayLevel 延时等级
     */
    public void asyncSendDelay(String topic, Object message, long timeout, int delayLevel) {
        this.template.asyncSend(topic, MessageBuilder.withPayload(message).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送延时消息成功，message = {}", message);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("异步发送延时消息发生异常，exception = {}", throwable.getMessage());
            }
        }, timeout, delayLevel);
        log.info("已异步发送延时消息");
        log.debug("消息内容:\n{}", message);
    }

    /**
     * 发送单向顺序消息
     *
     * @param topic topic
     */
    public void sendOneWayOrderly(String topic) {
        for (int i = 0; i < 30; i++) {
            this.template.sendOneWayOrderly(topic, MessageBuilder.withPayload("message - " + i).build(), "topic");
            log.info("单向顺序发送消息完成：message = {}", "message - " + i);
        }
    }

    /**
     * 同步发送顺序消息
     *
     * @param topic topic
     */
    public void syncSendOrderly(String topic) {
        for (int i = 0; i < 30; i++) {
            SendResult sendResult = this.template.syncSendOrderly(topic, MessageBuilder.withPayload("message - " + i).build(), "syncOrderlyKey");
            log.info("同步顺序发送消息完成：message = {}, sendResult = {}", "message - " + i, sendResult);
        }
    }
}