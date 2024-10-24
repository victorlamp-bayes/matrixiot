package com.victorlamp.matrixiot.service.agent.utils.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

@Slf4j
public class MqttClientConnect {

    private final String key;

    private final MqttClient mqttClient;

    /**
     * 客户端connect连接mqtt服务器
     **/
    public MqttClientConnect(String key, String url, String clientId, MqttConnectOptions options, MqttCallback callback) throws MqttException {
        this.key = key;
        // 连接设置
        mqttClient = new MqttClient(url, clientId, new MemoryPersistence());
        mqttClient.setCallback(callback);
        mqttClient.connect(options);
    }

    /**
     * 订阅一个个主题 ，此方法默认的的Qos等级为：1
     *
     * @param topic 主题
     */
    public void sub(String topic) throws MqttException {
        mqttClient.subscribe(topic, 0);
    }

    /**
     * 订阅某一个主题，可携带Qos
     *
     * @param topic 所要订阅的主题
     * @param qos   消息质量：0、1、2
     */
    public void sub(String topic, int qos) throws MqttException {
        mqttClient.subscribe(topic, qos);
    }

    /**
     * 关闭MQTT连接
     */
    public void close() throws MqttException {
        mqttClient.close();
        mqttClient.disconnect();
    }

    public String getKey() {
        return this.key;
    }


//    TODO 暂时屏蔽发布消息通道
//    /**
//     * 向某个主题发布消息 默认qos：1
//     *
//     * @param topic:发布的主题
//     * @param msg：发布的消息
//     */
//    public void pub(String topic, String msg) throws MqttException {
//        MqttMessage mqttMessage = new MqttMessage();
//        mqttMessage.setPayload(msg.getBytes(StandardCharsets.UTF_8));
//        MqttTopic mqttTopic = mqttClient.getTopic(topic);
//        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
//        token.waitForCompletion();
//    }
//
//    /**
//     * 向某个主题发布消息
//     *
//     * @param topic: 发布的主题
//     * @param msg:   发布的消息
//     * @param qos:   消息质量    Qos：0、1、2
//     */
//    public void pub(String topic, String msg, int qos) throws MqttException {
//        MqttMessage mqttMessage = new MqttMessage();
//        mqttMessage.setQos(qos);
//        mqttMessage.setPayload(msg.getBytes());
//        MqttTopic mqttTopic = mqttClient.getTopic(topic);
//        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
//        token.waitForCompletion();
//    }
//
//    /**
//     * 订阅多个主题 ，此方法默认的的Qos等级为：1
//     *
//     * @param topic 主题
//     */
//    public void sub(String[] topic) throws MqttException {
//        mqttClient.subscribe(topic);
//    }


}
