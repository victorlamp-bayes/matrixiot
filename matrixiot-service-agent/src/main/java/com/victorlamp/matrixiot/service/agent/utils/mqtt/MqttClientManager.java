package com.victorlamp.matrixiot.service.agent.utils.mqtt;

import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttClientManager {
    public final Cache<String, MqttClientConnect> mqttClients = Caffeine.newBuilder().build();

    /**
     * 设置连接，同步方法，避免重复设置
     */
    public synchronized void setClientConnect(Product product, MqttCallback callback) throws MqttException {
        if (product == null) {
            return;
        }

        if (mqttClients.getIfPresent(product.getId()) != null) {
            return;
        }

        // 获取Mqtt配置参数 TODO 校验参数
        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String host = productExternalConfig.getString("host");
        String port = productExternalConfig.getString("port");
        String url = "tcp://" + host + ":" + port;
        String username = productExternalConfig.getString("username");
        String password = productExternalConfig.getString("password");

        // Mqtt连接配置
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{url});
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setConnectionTimeout(30); // 设置超时时间，单位为秒
        options.setKeepAliveInterval(30); // 设置心跳时间 单位为秒，表示服务器每 30 秒向客户端发送心跳判断客户端是否在线
        options.setAutomaticReconnect(true); // 断线自动重连
        options.setCleanSession(false);

        String clientId = productExternalConfig.getString("clientId");
        String topic = productExternalConfig.getString("topic");

        MqttClientConnect connect = new MqttClientConnect(product.getId(), url, clientId, options, callback);
        log.info("连接MQTT服务器成功[{}]", url);
        connect.sub(topic);
        log.info("订阅主题成功[{}]", topic);

        mqttClients.put(product.getId(), connect);
    }
}

