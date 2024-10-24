package com.victorlamp.matrixiot.service.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import com.alibaba.fastjson2.JSONObject;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.agent.service.Agent;
import com.victorlamp.matrixiot.service.agent.utils.ExternalDbUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.*;
import static com.victorlamp.matrixiot.service.management.enums.ExternalTypeEnum.SANCHUAN_DB;

@Service
@Slf4j
public class SanchuanDbAgent extends Agent implements SchedulingConfigurer {

    private static final String EXTERNAL_TYPE = SANCHUAN_DB.name();

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

        Connection conn = getConnection(product);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, JSONObject> deviceMap = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM metertable");
            rs = stmt.executeQuery();

            // 获取外部设备列表
            deviceMap = MapUtil.newHashMap();
            while (rs.next()) {
                String deviceId = rs.getString("MAddr");
                JSONObject thingExternalConfig = new JSONObject();
                thingExternalConfig.put("meterAddress", rs.getString("MAddr"));
                thingExternalConfig.put("meterType", rs.getString("MMeterType"));
                thingExternalConfig.put("regionId", rs.getString("Region_Id"));
                thingExternalConfig.put("description", rs.getString("MDesc"));

                deviceMap.put(deviceId, thingExternalConfig);
            }

            DbUtil.close(rs, stmt, conn); // 静默关闭。必须按照ResultSet, Statement, Connection的顺序关闭
        } catch (SQLException e) {
            log.error(THING_AGENT_PULL_THING_FROM_EXTERNAL_SERVER_FAILED.getMsg());
        } finally {
            DbUtil.close(rs, stmt, conn); // 静默关闭。必须按照ResultSet, Statement, Connection的顺序关闭
        }

        if (ObjUtil.isNull(deviceMap)) {
            throw exception(THING_AGENT_PULL_THING_FROM_EXTERNAL_SERVER_FAILED);
        }

        // 创建设备
        for (String deviceId : deviceMap.keySet()) {
            createThing(product, deviceMap.get(deviceId));
        }
    }

    private void createThing(Product product, JSONObject thingExternalConfigJson) {
        // 校验设备是否已存在，已存在则忽略
        String deviceId = thingExternalConfigJson.getString("meterAddress");
        boolean thingExists = thingService.existsByExternalConfigItem("meterAddress", deviceId);
        if (thingExists) {
            log.info("设备已存在, 外部Id:[{}]", deviceId);
            return;
        }

        // 创建新设备
        ThingCreateReqDTO createReqDTO = ThingCreateReqDTO.builder()
                .productId(product.getId())
                .name(deviceId)
                .description(thingExternalConfigJson.getString("description"))
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

        Connection conn = getConnection(product);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ThingData thingData = null;
        try {
            stmt = conn.prepareStatement("SELECT TOP 1 * FROM meterdata WHERE meter_adress = ? ORDER BY read_time DESC");

            JSONObject thingExternalConfig = thing.getExternalConfig().getConfig();
            stmt.setString(1, thingExternalConfig.getString("meterAddress"));
            rs = stmt.executeQuery();

            // 每次只读取一条
            if (rs.next()) {
                Date date = rs.getDate("read_time");
                Float flow = rs.getFloat("flow");

                // 构造ThingData
                thingData = ThingData.builder().productId(product.getId()).thingId(thing.getId()).timestamp(date.getTime()).build();
                List<ThingPropertyData> propertyDataList = CollUtil.newArrayList();
                propertyDataList.add(new ThingPropertyData("flow", StrUtil.toString(flow), date.getTime()));
                // 附加电子表号
                propertyDataList.add(new ThingPropertyData("electronicNo", thingExternalConfig.getString("meterAddress"), date.getTime()));
                thingData.setProperties(propertyDataList);

                DbUtil.close(rs, stmt, conn); // 静默关闭。必须按照ResultSet, Statement, Connection的顺序关闭
            }
        } catch (Exception e) {
            log.error(THING_AGENT_PULL_THING_DATA_FROM_EXTERNAL_SERVER_FAILED.getMsg());
            return;
        } finally {
            DbUtil.close(rs, stmt, conn); // 静默关闭。必须按照ResultSet, Statement, Connection的顺序关闭
        }

        if (thingData == null) {
            log.info("从外部服务器获取设备数据为空");
            return;
        }

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

    private Connection getConnection(Product product) {
        JSONObject productExternalConfig = product.getExternalConfig().getConfig();
        String jdbcUrl = productExternalConfig.getString("jdbcUrl");
        String username = productExternalConfig.getString("username");
        String password = productExternalConfig.getString("password");
        ExternalDbUtil.DataSourceConfig dbConfig = new ExternalDbUtil.DataSourceConfig(jdbcUrl, username, password);

        // 连接数据库
        Connection conn = null;
        try {
            conn = ExternalDbUtil.getConnection(EXTERNAL_TYPE, dbConfig);
            if (conn == null) {
                throw exception(THING_AGENT_CONNECT_TO_EXTERNAL_SERVER_FAILED);
            }
        } catch (Exception e) {
            log.error("{}:{}", THING_AGENT_CONNECT_TO_EXTERNAL_SERVER_FAILED.getMsg(), "三川");
            throw exception(THING_AGENT_CONNECT_TO_EXTERNAL_SERVER_FAILED);
        }

        return conn;
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
        CronTask cronTask = new CronTask(() -> pullThingData(product), "0 0 2 * * ?"); // 凌晨2点执行
        ScheduledFuture<?> future = Objects.requireNonNull(taskRegistrar.getScheduler()).schedule(cronTask.getRunnable(), cronTask.getTrigger());
        assert future != null;
        log.info("[{}] - 定时任务设置成功:拉取产品[{}]的设备数据", EXTERNAL_TYPE, product.getId());
    }
}
