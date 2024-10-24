package com.victorlamp.matrixiot.service.route.utils.route;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import com.victorlamp.matrixiot.service.route.constant.DestinationConfigType;
import com.victorlamp.matrixiot.service.route.entity.DataDestination;
import com.victorlamp.matrixiot.service.route.entity.DataDestinationConfig;
import com.victorlamp.matrixiot.service.route.entity.DataRoute;
import com.victorlamp.matrixiot.service.route.utils.script.DataRouteScriptExecutor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.route.constant.ErrorCodeConstants.DATA_FLOW_TRANSFORMATION_FAILED;

@Slf4j
public class DataSender {
    public static void sendData(DataRoute dataRoute, ThingData thingData) {
        DataDestination destination = dataRoute.getDestination();
        DataDestinationConfig config = dataRoute.getDestination().getConfig();

        // 如果没有转换脚本 直接流转原始报文
        String flowingData;
        if (ObjUtil.isNull(dataRoute.getTransformer()) || StrUtil.isBlank(dataRoute.getTransformer().getContent())) {
            flowingData = thingData.getOriginData();
        } else {
            JSONObject jsonObject = new JSONObject();
            Long timestamp = null;
            if (CollUtil.size(thingData.getProperties()) > 0) {
                for (ThingPropertyData property : thingData.getProperties()) {
                    if (timestamp == null) {
                        timestamp = property.getTimestamp();
                    }
                    jsonObject.put(property.getIdentifier(), property.getValue());
                }
            }
            if (!jsonObject.containsKey("timestamp") && timestamp != null) {
                jsonObject.put("timestamp", DateUtil.date(timestamp).toString());
            }

            log.debug("数据json[{}]", jsonObject.toJSONString());
            flowingData = transform(dataRoute.getId(), jsonObject.toString());
        }

        if (StrUtil.equals(destination.getType().name(), DestinationConfigType.REST_API) && config instanceof DataDestinationConfig.RestApi api) {
            sendToRestApi(api, flowingData);
        } else if (StrUtil.equals(destination.getType().name(), DestinationConfigType.RABBITMQ) && config instanceof DataDestinationConfig.RabbitMQ rabbitMQ) {
            sendToRabbitMQ(rabbitMQ, flowingData);
        }
    }

    private static synchronized String transform(String id, String data) {
        return DataRouteScriptExecutor.transform(id, data);
    }

    private static void sendToRestApi(DataDestinationConfig.RestApi config, String data) {
        String apiUrl = config.getUrl();
        String method = config.getMethod();

        log.info("数据路由开始");
        log.debug("{}", data);

        HttpRequest request = HttpUtil.createRequest(EnumUtil.fromStringQuietly(Method.class, method.toUpperCase()), apiUrl)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header("openApiSign", config.getVerifyCode())
                .body(data);

        HttpResponse response = request.execute();
        String respBody = response.body();

        if (!JSON.isValid(respBody)) {
            throw exception(DATA_FLOW_TRANSFORMATION_FAILED);
        }

        JSONObject respJson = JSON.parseObject(respBody);
        if (StrUtil.equalsIgnoreCase(respJson.getString("status"), "true") && StrUtil.equalsIgnoreCase(respJson.getString("errorCode"), "0")) {
            log.info("数据路由成功");
            log.debug("{}", response);
        } else {
            log.error("数据路由失败[{}]", response);
            throw exception(DATA_FLOW_TRANSFORMATION_FAILED);
        }
    }

    private static void sendToRabbitMQ(DataDestinationConfig.RabbitMQ config, String data) {
        ConnectionFactory factory = createConnectionFactory(config);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            log.info("开始发送数据[{}]", data);

            // 消息确认机制
            channel.confirmSelect();
            channel.queueDeclare(config.getTopic(), true, false, false, null);
            channel.basicPublish("", config.getTopic(), MessageProperties.PERSISTENT_TEXT_PLAIN, data.getBytes());

            if (!channel.waitForConfirms()) {
                log.info("RabbitMQ信息发送失败");
                throw exception(DATA_FLOW_TRANSFORMATION_FAILED);
            }

        } catch (IOException | TimeoutException | InterruptedException e) {
            log.info("RabbitMQ连接异常，errmsg = {}", e.getMessage());
            throw exception(DATA_FLOW_TRANSFORMATION_FAILED);
        }
    }

    private static ConnectionFactory createConnectionFactory(DataDestinationConfig.RabbitMQ config) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setPort(Integer.parseInt(config.getPort()));
        factory.setUsername(config.getUsername());
        factory.setPassword(config.getPassword());
        return factory;
    }
}
