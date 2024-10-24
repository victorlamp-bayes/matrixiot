package com.victorlamp.matrixiot.service.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.agent.service.Agent;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingCreateReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingExternalConfig;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingServiceData;
import com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.THING_AGENT_CREATE_THING_FOR_EXTERNAL_DEVICE_FAILED;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.THING_AGENT_PULL_THING_FROM_EXTERNAL_SERVER_FAILED;

@Service
@Slf4j
public class DaiaoSubmeterAgent extends Agent implements SchedulingConfigurer {

    private static final String EXTERNAL_TYPE = ExternalTypeEnum.DAIAO_SUBMETER.name();

    @DubboReference
    private ProductService productService;
    @DubboReference
    private ThingService thingService;
    @Resource
    private RocketMQTemplateProducer mqProducer;
    private ScheduledTaskRegistrar taskRegistrar;

    @Override
    public void pullThing(Product product) {
        // 校验产品外部配置类型
        if (!validateProductExternalType(product, EXTERNAL_TYPE)) {
            return;
        }

        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String listThingUrl = productExternalConfig.getString("listThingUrl");
        Map<String, Object> paramMap = MapUtil.newHashMap(false);
        paramMap.put("account", productExternalConfig.getString("account"));
        paramMap.put("password", productExternalConfig.getString("password"));
        paramMap.put("meterUse", productExternalConfig.getString("meterUse")); // 仪表类型  water:水表
        paramMap.put("usageType", productExternalConfig.getString("usageType")); // 仪表用途 bigmeter:考核表, submeter:结算表

        // 请求外部数据
        String respBody = HttpUtil.get(listThingUrl, paramMap);

        // 响应：error
        if (JSON.isValidObject(respBody)) {
            JSONObject errorResp = JSON.parseObject(respBody);
            log.error("{}. 产品[{}],错误消息:{}", THING_AGENT_PULL_THING_FROM_EXTERNAL_SERVER_FAILED.getMsg(), product.getId(), errorResp.getString("error"));
            throw exception(THING_AGENT_PULL_THING_FROM_EXTERNAL_SERVER_FAILED);
        }

        JSONArray deviceList = JSON.parseArray(respBody);
        for (JSONObject thingExternalConfig : deviceList.toList(JSONObject.class)) {
            createThing(product, thingExternalConfig);
        }
    }

    private void createThing(Product product, JSONObject thingExternalConfigJson) {
        // 校验设备是否已存在，已存在则忽略
        String deviceId = thingExternalConfigJson.getString("meterNumber");
        boolean thingExists = thingService.existsByExternalConfigItem("meterNumber", deviceId);
        if (thingExists) {
            log.info("设备已存在, 外部Id:[{}]", deviceId);
            return;
        }

        // 创建新设备
        ThingCreateReqDTO createReqDTO = ThingCreateReqDTO.builder()
                .productId(product.getId())
                .name(thingExternalConfigJson.getString("cuName"))
                .description(thingExternalConfigJson.getString("cuAddress"))
                .externalConfig(new ThingExternalConfig(EXTERNAL_TYPE, thingExternalConfigJson))
                .enabled(true)
                .build();
        log.debug("创建设备: {}", createReqDTO);
        Thing thing = thingService.createThing(createReqDTO);
        if (thing == null) {
            log.error("创建设备失败，外部Id:[{}]", deviceId);
            throw exception(THING_AGENT_CREATE_THING_FOR_EXTERNAL_DEVICE_FAILED);
        }

        log.info("成功创建设备[{}], 外部Id:[{}]", thing.getId(), deviceId);
    }

    @Override
    public void pullThingData(Product product) {
        // 校验产品外部配置类型
        if (!validateProductExternalType(product, EXTERNAL_TYPE)) {
            return;
        }

        // 获取设备列表
        ThingPageReqDTO pageReqDTO = ThingPageReqDTO.builder().productId(product.getId()).enabled(true).build();
        PageResult<Thing> pageResult = thingService.listThingPage(pageReqDTO);

        // 拉取设备数据，每次一条，顺序执行，降低系统并发负载
        while (pageResult.getList().size() > 0) {
            for (Thing thing : pageResult.getList()) {
                pullThingData(thing);
            }

            // 下一批
            pageReqDTO.setPageNo(pageResult.getPageNo() + 1);
            pageResult = thingService.listThingPage(pageReqDTO);
        }
    }

    @Override
    public void pullThingData(Thing thing) {
        Product product = thing.getProduct();

        // 校验产品和设备外部配置类型
        if (!validateProductExternalType(product, EXTERNAL_TYPE)) {
            return;
        }
        if (!validateThingExternalType(thing, EXTERNAL_TYPE)) {
            return;
        }

        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String getLatestThingDataUrl = productExternalConfig.getString("getLatestThingDataUrl");
        Map<String, Object> paramMap = MapUtil.newHashMap(false);
        paramMap.put("account", productExternalConfig.getString("account"));
        paramMap.put("password", productExternalConfig.getString("password"));
        paramMap.put("meterUse", productExternalConfig.getString("meterUse")); // 仪表类型  water:水表

        JSONObject thingExternalConfig = thing.getExternalConfig().getConfig();
        paramMap.put("meterNumber", thingExternalConfig.getString("meterNumber")); // 仪表编号

        String respBody = HttpUtil.get(getLatestThingDataUrl, paramMap);

        // 响应：error
        if (JSON.isValidObject(respBody)) {
            JSONObject errorResp = JSON.parseObject(respBody);
            log.error("拉取设备数据失败[{}]. 设备外部编号:{}, 错误消息:{}", thing.getId(), thingExternalConfig.getString("meterNumber"), errorResp.getString("error"));
            return;
        }

        JSONArray dataList = JSON.parseArray(respBody);
        if (ObjUtil.isEmpty(dataList) || dataList.isEmpty()) {
            log.error("拉取设备数据为空[{}]. 设备外部编号:{}", thing.getId(), thingExternalConfig.getString("meterNumber"));
            return;
        }

        // 读取一条数据
        JSONObject data = dataList.getJSONObject(0);

        // 暂时手工处理 TODO: 按物模型读取数据
        String volume = data.getString("volume").split(" ")[0];
        Float batteryLifetime = data.getFloat("batteryLifetime");
        Long timestamp = data.getLong("time");

        // 构造ThingData
        ThingData thingData = ThingData.builder()
                .productId(thing.getProduct().getId())
                .thingId(thing.getId())
                .timestamp(timestamp)
                .originData(data.toJSONString())
                .build();
        List<ThingPropertyData> propertyDataList = CollUtil.newArrayList();
        propertyDataList.add(new ThingPropertyData("volume", volume, timestamp));
        propertyDataList.add(new ThingPropertyData("batteryLifetime", batteryLifetime.toString(), timestamp));
        // 附加电子表号
        propertyDataList.add(new ThingPropertyData("electronicNo", thingExternalConfig.getString("meterNumber"), timestamp));
        thingData.setProperties(propertyDataList);

        // TODO 告警事件

        // 发送到消息队列
        mqProducer.asyncSendMessage(ThingTopic.THING_PROPERTY_EVENT_POST, thingData);
    }

    @Override
    public void postThingData(String originData) {

    }

    @Override
    public void registerDevice(Product product, Thing thing) {

    }

    @Override
    public ThingServiceData sendCommand(Product product, Thing thing, ThingServiceData serviceData, String rawData) {
        return null;
    }

    @Override
    public void replyCommand(String message) {

    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(2)); // 配置线程池
        this.taskRegistrar = taskRegistrar;
        this.addAllTasks();
    }

    private void addAllTasks() {
        log.info("[{}] - 开始设置定时任务", EXTERNAL_TYPE);

        List<Product> productList = productService.listProductByExternalType(EXTERNAL_TYPE);
        for (Product product : productList) {
            addTask(product);
        }
    }

    private void addTask(Product product) {
        // 添加拉取设备列表任务
        FixedDelayTask fixedDelayTask = new FixedDelayTask(() -> pullThing(product), 24 * 3600 * 1000, 5 * 1000);
        Objects.requireNonNull(taskRegistrar.getScheduler()).schedule(fixedDelayTask.getRunnable(), Instant.now());
        log.info("[{}] - 定时任务设置成功:拉取产品[{}]的设备列表", EXTERNAL_TYPE, product.getId());

        // 添加拉取设备数据任务
        CronTask cronTask = new CronTask(() -> pullThingData(product), "0 0 3 * * ?"); // 凌晨5点执行
        ScheduledFuture<?> future = Objects.requireNonNull(taskRegistrar.getScheduler()).schedule(cronTask.getRunnable(), cronTask.getTrigger());
        assert future != null;
        log.info("[{}] - 定时任务设置成功:拉取产品[{}]的设备数据", EXTERNAL_TYPE, product.getId());
    }
}
