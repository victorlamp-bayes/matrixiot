package com.victorlamp.matrixiot.service.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.cache.CaffeineCacheUtils;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.agent.service.Agent;
import com.victorlamp.matrixiot.service.agent.thirdPartyService.GD.netty.NettyClient;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingModelService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.constant.ProductExternalConfigItems;
import com.victorlamp.matrixiot.service.management.constant.ThingExternalConfigItems;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingServiceData;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum;
import com.victorlamp.matrixiot.service.agent.utils.ThingModelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class HuaxuGdAgent extends Agent implements SchedulingConfigurer {

    private static final String EXTERNAL_TYPE = ExternalTypeEnum.HUAXU_GD.name();

    @DubboReference
    private ProductService productService;
    @DubboReference
    private ThingService thingService;
    @DubboReference
    private ThingModelService thingModelService;

    private final LoadingCache<String, ThingModel> thingModelCache = CaffeineCacheUtils.buildLoadingCache(
            Duration.ofMinutes(1L),
            new CacheLoader<>() {
                @Override
                public @Nullable ThingModel load(String key) {
                    return thingModelService.describeThingModel(key);
                }
            }
    );

    @Resource
    private RocketMQTemplateProducer mqProducer;
    private ScheduledTaskRegistrar taskRegistrar;


    @Override
    public void pullThing(Product product) {
        log.info("[{}]暂不支持拉取设备列表", EXTERNAL_TYPE);
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

        // 获取连接外部服务器配置
        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String ip = productExternalConfig.getString(ProductExternalConfigItems.HUB_IP);
        Integer port = productExternalConfig.getInteger(ProductExternalConfigItems.HUB_PORT);

        JSONObject thingExternalConfig = thing.getExternalConfig().getConfig();
        String hubCode = thingExternalConfig.getString(ThingExternalConfigItems.HUB_SUB_HUB_CODE);
        String code = hubCode.substring(4);
        String region = hubCode.substring(0, 4);
        String channelPort = thingExternalConfig.getString(ThingExternalConfigItems.HUB_SUB_CHANNEL_PORT);
        String chPort = StrUtil.wrap(channelPort, "\"");

        log.info("开始发送指令读取冻结数据");
        NettyClient nettyClient = new NettyClient(ip, port);
        String respBody = nettyClient.getFreezeReadingInfo(code, region, chPort);

        log.info("解析冻结数据");
        String data = parseData(respBody);
        if (StrUtil.isBlank(data)) {
            log.error("冻结数据为空");
            return;
        }

        log.debug("响应Body: {}", data);

        // 获取物模型
        ThingModel thingModel = thingModelCache.get(thing.getProduct().getId());
        // 检查数据是否合法
        ThingModelUtil.validateThingData(thingModel, data);
        // 构造ThingData
        ThingData thingData = ThingModelUtil.buildThingData(thingModel, thing, data, respBody);
        // 附加电子表号
        // TODO 处理异常情况。正常情况下至少应该有一个属性
        assert CollUtil.size(thingData.getProperties()) > 0;
        ThingPropertyData property = ThingPropertyData.builder()
                .identifier("electronicNo")
                .value(thing.getExternalConfig().getConfig().getString("electronicNo"))
                .timestamp(thingData.getProperties().get(0).getTimestamp()).build();
        thingData.getProperties().add(property);

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

    private String parseData(String respBody) {
        JSONObject respJson = JSON.parseObject(respBody);

        // 服务器响应状态：1 - 成功，其他 - 失败
        Integer status = respJson.getInteger("status");
        if (status != 1) {
            log.error("外部服务器返回失败消息: {}", respJson);
            return StrUtil.EMPTY;
        }

        // 读取冻结数据：水量
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> results = respJson.getJSONArray("results").toList(JSONObject.class);
        for (JSONObject result : results) {
            if (result.containsKey("quantity")) {
                jsonObject.put("currentQuantity", result.get("quantity"));
                break;
            }
        }

        return jsonObject.toJSONString();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
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
        // 添加拉取设备数据任务
        CronTask cronTask = new CronTask(() -> pullThingData(product), "0 0 4 * * ?"); // 凌晨4点执行
        ScheduledFuture<?> future = Objects.requireNonNull(taskRegistrar.getScheduler()).schedule(cronTask.getRunnable(), cronTask.getTrigger());
        assert future != null;
        log.info("[{}] - 定时任务设置成功:拉取产品[{}]的设备数据", EXTERNAL_TYPE, product.getId());
    }
}
