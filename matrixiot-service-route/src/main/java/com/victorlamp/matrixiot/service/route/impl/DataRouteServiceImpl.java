package com.victorlamp.matrixiot.service.route.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.script.ScriptUtil;
import com.alibaba.fastjson2.JSON;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.route.DataRouteService;
import com.victorlamp.matrixiot.service.route.convert.DataRouteCovert;
import com.victorlamp.matrixiot.service.route.dao.DataRouteRepository;
import com.victorlamp.matrixiot.service.route.dto.*;
import com.victorlamp.matrixiot.service.route.entity.*;
import com.victorlamp.matrixiot.service.route.enums.DataRouteStatusEnum;
import com.victorlamp.matrixiot.service.route.enums.DataRouteTransformerTypeEnum;
import com.victorlamp.matrixiot.service.route.enums.DataSourceTopicEnum;
import com.victorlamp.matrixiot.service.route.message.MQConsumerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.route.constant.ErrorCodeConstants.*;
import static com.victorlamp.matrixiot.service.route.enums.DataRouteStatusEnum.RUNNING;
import static com.victorlamp.matrixiot.service.route.enums.DataRouteTransformerTypeEnum.JS;

@DubboService(validation = "true")
@Slf4j
public class DataRouteServiceImpl implements DataRouteService {
    @Resource
    private DataRouteRepository dataRouteRepository;
    @Resource
    private MQConsumerManager mqConsumerManager;
    @DubboReference
    private ProductService productService;
    @DubboReference
    private ThingService thingService;

    @PostConstruct
    public void startAllDataRoutes() {
        try {
            List<DataRoute> dataRoutes = listDataRoute(new DataRouteListReqDTO(RUNNING));
            mqConsumerManager.batchRegisterMQConsumer(dataRoutes);
        } catch (Exception e) {
            log.error("初始化启动数据路由失败。{}", e.getMessage());
        }
    }

    @Override
    public DataRoute createDataRoute(DataRouteCreateReqDTO reqDTO) {
        log.info("创建数据路由: {}", JSON.toJSONString(reqDTO));

        // 校验数据路由名称唯一
        validateDataRouteNameUnique(null, reqDTO.getName());
        // 校验转换脚本
        validateTransformer(reqDTO.getTransformer());
        // 校验数据源
        DataSourceCreateDTO source = validDataSourceByCreate(reqDTO.getSource());
        reqDTO.setSource(source);
        // 校验数据目的
        DataDestinationCreateDTO destination = validDataDestinationByCreate(reqDTO.getDestination());
        reqDTO.setDestination(destination);

        DataRoute dataRoute = DataRouteCovert.INSTANCE.toEntity(reqDTO);

        DataRoute newDataRoute = dataRouteRepository.insert(dataRoute);

        // 启动数据路由
        if (StrUtil.equalsIgnoreCase(newDataRoute.getStatus(), RUNNING.name())) {
            mqConsumerManager.registerMQConsumer(newDataRoute);
        }

        return newDataRoute;
    }

    @Override
    public DataRoute getDataRoute(String id) {
        return dataRouteRepository.findById(id).orElse(null);
    }

    @Override
    public List<DataRoute> listDataRoute(DataRouteListReqDTO reqDTO) {
        if (reqDTO == null || reqDTO.getStatus() == null) {
            return dataRouteRepository.findAll();
        }

        return dataRouteRepository.findAllByStatus(reqDTO.getStatus().name());
    }

    @Override
    public PageResult<DataRoute> listDataRoutePage(DataRoutePageReqDTO reqDTO) {
        return dataRouteRepository.findPage(reqDTO);
    }

    @Override
    public void updateDataRoute(String id, DataRouteUpdateReqDTO reqDTO) {
        DataRoute dataRoute = validateDataRouteExists(id);
        // 校验数据路由是否可修改。运行中的数据路由不可修改
        validateDataRouteModifiable(dataRoute);

        if (!StrUtil.isNullOrUndefined(reqDTO.getName())) {
            validateDataRouteNameUnique(reqDTO.getId(), reqDTO.getName());
            dataRoute.setName(reqDTO.getName());
        }

        if (!StrUtil.isNullOrUndefined(reqDTO.getDescription())) {
            dataRoute.setDescription(reqDTO.getDescription());
        }

        if (ObjUtil.isNotNull(reqDTO.getSource())) {
            DataSource source = validDataSourceByUpdate(dataRoute.getSource(), reqDTO.getSource());
            dataRoute.setSource(source);
        }

        if (ObjUtil.isNotNull(reqDTO.getTransformer())) {
            validateTransformer(reqDTO.getTransformer());
            dataRoute.setTransformer(reqDTO.getTransformer());
        }

        if (ObjUtil.isNotNull(reqDTO.getDestination())) {
            DataDestination destination = validDataDestinationByUpdate(dataRoute.getDestination(), reqDTO.getDestination());
            dataRoute.setDestination(destination);
        }

        dataRouteRepository.save(dataRoute);
    }

    @Override
    public void updateDataRouteStatus(DataRouteUpdateStatusReqDTO reqDTO) {
        DataRoute dataRoute = validateDataRouteExists(reqDTO.getId());

        // 校验状态字段是否正确
        if (StrUtil.isBlank(reqDTO.getStatus()) || EnumUtil.notContains(DataRouteStatusEnum.class, reqDTO.getStatus().toUpperCase())) {
            throw exception(DATA_ROUTE_STATUS_INVALID);
        }

        // 状态不变，直接返回
        if (StrUtil.equalsIgnoreCase(reqDTO.getStatus(), dataRoute.getStatus())) {
            return;
        }

        dataRoute.setStatus(reqDTO.getStatus());

        dataRoute = dataRouteRepository.save(dataRoute);

        // 启动数据路由
        if (StrUtil.equalsIgnoreCase(dataRoute.getStatus(), RUNNING.name())) {
            mqConsumerManager.registerMQConsumer(dataRoute);
        }
    }

    @Override
    public void deleteDataRoute(String id) {
        DataRoute dataRoute = validateDataRouteExists(id);

        if (StrUtil.equals(dataRoute.getStatus(), RUNNING.name())) {
            throw exception(DATA_ROUTE_DELETE_BY_STATUS);
        }
        dataRouteRepository.deleteById(id);

        mqConsumerManager.stopAndRemoveConsumer(id);
    }

    @Override
    public List<DataSourceTopic> listDataSourceTopic() {
        final List<DataSourceTopic> list = CollUtil.newArrayList();
        for (DataSourceTopicEnum topicEnum : DataSourceTopicEnum.values()) {
            list.add(new DataSourceTopic(topicEnum.getId(), topicEnum.getName(), topicEnum.getTopic()));
        }

        return CollUtil.emptyIfNull(list);
    }

    private void validateDataRouteModifiable(DataRoute dataRoute) {
        if (StrUtil.equalsIgnoreCase(dataRoute.getStatus(), RUNNING.name())) {
            throw exception(DATA_ROUTE_CANNOT_MODIFY_BY_STATUS);
        }
    }

    private void validateDataRouteNameUnique(String id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }

        DataRoute dataRoute = dataRouteRepository.findByName(name);
        if (dataRoute == null) {
            return;
        }

        // 如果 id 为空，说明不用比较是否为相同 id 的数据路由
        if (id == null) {
            throw exception(DATA_ROUTE_NAME_DUPLICATE);
        }
        if (!dataRoute.getId().equals(id)) {
            throw exception(DATA_ROUTE_NAME_DUPLICATE);
        }
    }

    private DataSourceCreateDTO validDataSourceByCreate(DataSourceCreateDTO source) {
        if (StrUtil.isBlank(source.getProductId())) {
            return null;
        }

        validateDataRouteProduct(source.getProductId());

        // 有设备id就查询，没有表示该产品下所有设备
        if (StrUtil.isNotBlank(source.getThingId())) {
            validateDataRouteThing(source.getThingId());
        }

        return source;
    }

    private DataSource validDataSourceByUpdate(DataSource dataSource, DataSourceUpdateDTO updateDTO) {
        // 更新操作时允许为空
        if (!StrUtil.isNullOrUndefined(updateDTO.getDescription())) {
            dataSource.setDescription(updateDTO.getDescription());
        }

        if (!StrUtil.isNullOrUndefined(updateDTO.getProductId())) {
            Product product = validateDataRouteProduct(updateDTO.getProductId());
            dataSource.setProductId(product.getId());
        }

        if (!StrUtil.isNullOrUndefined(updateDTO.getThingId())) {
            Thing thing = validateDataRouteThing(updateDTO.getThingId());
            dataSource.setThingId(thing.getId());
        }

        return dataSource;
    }

    private Product validateDataRouteProduct(String id) {
        Product product = productService.getProduct(id);
        if (product == null) {
            throw exception(DATA_ROUTE_PRODUCT_NOT_EXISTS);
        }
        return product;
    }

    private Thing validateDataRouteThing(String id) {
        Thing thing = thingService.getThing(id);
        if (thing == null) {
            throw exception(DATA_ROUTE_THING_NOT_EXISTS);
        }
        return thing;
    }

    private DataRoute validateDataRouteExists(String id) {
        DataRoute dataRoute = dataRouteRepository.findById(id).orElse(null);
        if (dataRoute == null) {
            throw exception(DATA_ROUTE_NOT_EXISTS);
        }
        return dataRoute;
    }

    private void validateTransformer(DataTransformer transformer) {
        // 允许为空，直接原报文转发
        if (ObjUtil.isNull(transformer)) {
            return;
        }

        // 允许为空，直接原报文转发
        if (StrUtil.isBlank(transformer.getContent())) {
            return;
        }

        DataRouteTransformerTypeEnum scriptType = transformer.getType();
        String scriptContent = transformer.getContent();

        if (scriptType != JS) {
            log.error("{}:[{}]", DATA_ROUTE_UNKNOWN_SCRIPT_TYPE.getMsg(), scriptType);
            throw exception(DATA_ROUTE_UNKNOWN_SCRIPT_TYPE);
        }

        ScriptEngine scriptEngine = ScriptUtil.getScript("graal.js");
        try {
            scriptEngine.eval(scriptContent);
        } catch (ScriptException e) {
            log.error(ILLEGAL_DATA_ROUTE_SCRIPT.getMsg());
            throw exception(ILLEGAL_DATA_ROUTE_SCRIPT);
        }
        Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        if (bindings.containsKey("transform")) {
            return;
        }
        log.error(DATA_ROUTE_TRANSFORM_NOT_EXISTS.getMsg());
        throw exception(DATA_ROUTE_TRANSFORM_NOT_EXISTS);
    }

    private DataDestinationCreateDTO validDataDestinationByCreate(DataDestinationCreateDTO destination) {
        if (destination.getConfig() instanceof DataDestinationConfig.RestApi create) {
            if (StrUtil.isNullOrUndefined(create.getUrl())) {
                throw exception(DATA_DESTINATION_URL_IS_NULL);
            }
            if (StrUtil.isNullOrUndefined(create.getMethod())) {
                throw exception(DATA_DESTINATION_METHOD_IS_NULL);
            }
            if (StrUtil.isNullOrUndefined(create.getVerifyCode())) {
                throw exception(DATA_DESTINATION_VERIFY_COD_IS_NULL);
            }
        } else if (destination.getConfig() instanceof DataDestinationConfig.RabbitMQ create) {
            if (StrUtil.isNullOrUndefined(create.getHost())) {
                throw exception(DATA_DESTINATION_RABBITMQ_HOST_IS_NULL);
            }
            if (StrUtil.isNullOrUndefined(create.getPort())) {
                throw exception(DATA_DESTINATION_RABBITMQ_PORT_IS_NULL);
            }
            if (StrUtil.isNullOrUndefined(create.getUsername())) {
                throw exception(DATA_DESTINATION_RABBITMQ_USERNAME_IS_NULL);
            }
            if (StrUtil.isNullOrUndefined(create.getPassword())) {
                throw exception(DATA_DESTINATION_RABBITMQ_PASSWORD_IS_NULL);
            }
            if (StrUtil.isNullOrUndefined(create.getTopic())) {
                throw exception(DATA_DESTINATION_RABBITMQ_TOPIC_IS_NULL);
            }
        }

        return destination;
    }

    private DataDestination validDataDestinationByUpdate(DataDestination destination, DataDestinationUpdateDTO updateDTO) {
        if (!StrUtil.isNullOrUndefined(updateDTO.getDescription())) {
            updateDTO.setDescription(updateDTO.getDescription());
        }

        // 对比数据目的类型和内容，如果类型更换内容直接替换，否则只修改对应字段
        if (!ObjUtil.isNull(updateDTO.getConfig())) {
            if (StrUtil.equals(destination.getType().name(), updateDTO.getType().name())) {
                DataDestinationConfig destinationConfig = updateConfigBasedOnType(destination.getConfig(), updateDTO.getConfig());
                destination.setConfig(destinationConfig);
            } else {
                destination.setType(updateDTO.getType());
                destination.setConfig(updateDTO.getConfig());
            }
        }

        return destination;
    }

    // 根据具体类型比较并更新字段
    private DataDestinationConfig updateConfigBasedOnType(DataDestinationConfig existConfig, DataDestinationConfig updateConfig) {
        if (existConfig instanceof DataDestinationConfig.RestApi exist && updateConfig instanceof DataDestinationConfig.RestApi update) {
            if (!StrUtil.isNullOrUndefined(update.getUrl())) {
                exist.setUrl(exist.getUrl());
            }
            if (!StrUtil.isNullOrUndefined(update.getMethod())) {
                exist.setMethod(update.getMethod());
            }
            if (!StrUtil.isNullOrUndefined(update.getVerifyCode())) {
                exist.setVerifyCode(update.getVerifyCode());
            }
        } else if (existConfig instanceof DataDestinationConfig.RabbitMQ exist && updateConfig instanceof DataDestinationConfig.RabbitMQ update) {
            if (!StrUtil.isNullOrUndefined(update.getHost())) {
                exist.setHost(update.getHost());
            }
            if (!StrUtil.isNullOrUndefined(update.getPort())) {
                exist.setPort(update.getPort());
            }
            if (!StrUtil.isNullOrUndefined(update.getUsername())) {
                exist.setUsername(update.getUsername());
            }
            if (!StrUtil.isNullOrUndefined(update.getPassword())) {
                exist.setPassword(update.getPassword());
            }
            if (!StrUtil.isNullOrUndefined(update.getTopic())) {
                exist.setTopic(update.getTopic());
            }
        }

        return existConfig;
    }
}
