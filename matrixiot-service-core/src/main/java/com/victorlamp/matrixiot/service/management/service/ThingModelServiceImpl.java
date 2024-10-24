package com.victorlamp.matrixiot.service.management.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.script.ScriptUtil;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.dao.ThingModelRepository;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.*;
import com.victorlamp.matrixiot.service.management.enums.ThingModelScriptTypeEnum;
import com.victorlamp.matrixiot.service.management.enums.ThingModelServiceParamDirectionEnum;
import com.victorlamp.matrixiot.service.management.utils.ThingModelScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.management.constant.ErrorCodeConstants.*;
import static com.victorlamp.matrixiot.service.management.enums.ThingModelScriptTypeEnum.JS;
import static com.victorlamp.matrixiot.service.management.enums.ThingModelServiceParamDirectionEnum.INPUT;
import static com.victorlamp.matrixiot.service.management.enums.ThingModelServiceParamDirectionEnum.OUTPUT;

@DubboService(validation = "true")
@Service("thingModelService")
@Slf4j
@Primary
public class ThingModelServiceImpl implements com.victorlamp.matrixiot.service.management.api.ThingModelService {
    @Resource
    private ThingModelRepository thingModelRepository;
    @Resource
    private ProductService productService;

    //*** 物模型 ***//

    /**
     * 仅在创建产品时，自动关联创建物模型
     */
    @Override
    public void createThingModel(String productId) {
        // 校验产品可修改
        validateProductModifiable(productId);
        // 校验物模型不存在
        ThingModel thingModel = thingModelRepository.findById(productId).orElse(null);
        if (ObjUtil.isNotNull(thingModel)) {
            throw exception(THING_MODEL_DUPLICATE);
        }

        thingModel = new ThingModel();
        thingModel.setId(productId);
        thingModelRepository.insert(thingModel);
    }

    @Override
    public ThingModel describeThingModel(String productId) {
        return validateThingModelExists(productId);
    }

    /**
     * 整体更新物模型
     */
    @Override
    public void updateThingModel(String productId, ThingModel thingModel) {
        // 校验产品可修改
        validateProductModifiable(productId);
        // 校验物模型存在
        ThingModel existsThingModel = validateThingModelExists(productId);

        // 校验请求参数：thingModel.id为空或与productId一致
        String thingModelId = thingModel.getId();
        if (StrUtil.isNotBlank(thingModelId) && !StrUtil.equalsIgnoreCase(productId, thingModelId)) {
            throw exception(THING_MODEL_MISMATCH_PRODUCT_ID_AND_THING_MODEL_ID);
        }

        // 设置Id
        if (StrUtil.isBlank(thingModelId)) {
            thingModel.setId(productId);
        }

        // TODO 校验物模型数据规范

        thingModelRepository.save(thingModel);
    }

    /**
     * 仅在删除产品时，自动关联删除物模型
     */
    @Override
    public void deleteThingModel(String productId) {
        // 校验物模型存在
        validateThingModelExists(productId);
        // 删除场景特殊处理：不能删除已有产品的物模型
        Product product = productService.getProduct(productId);
        if (ObjUtil.isNotNull(product)) {
            throw exception(THING_MODEL_CANNOT_DELETE_BY_EXISTS_PRODUCT);
        }
        thingModelRepository.deleteById(productId);
    }

    private void validateProductModifiable(String productId) {
        Product product = productService.getProduct(productId);
        if (product == null) {
            throw exception(THING_MODEL_PRODUCT_NOT_EXISTS);
        }
        if (product.getPublished()) {
            throw exception(THING_MODEL_CANNOT_MODIFY_PUBLISHED_PRODUCT);
        }
    }

    private ThingModel validateThingModelExists(String productId) {
        ThingModel thingModel = thingModelRepository.findById(productId).orElse(null);
        if (ObjUtil.isNull(thingModel)) {
            throw exception(THING_MODEL_NOT_EXISTS);
        }
        return thingModel;
    }

    private ThingModel validateThingModelModifiable(String productId) {
        validateProductModifiable(productId);
        return validateThingModelExists(productId);
    }

    private boolean isEmpty(ThingModel thingModel) {
        if (thingModel == null || thingModel.getId() == null) {
            return true;
        }

        return CollUtil.isEmpty(thingModel.getProperties())
                && CollUtil.isEmpty(thingModel.getEvents())
                && CollUtil.isEmpty(thingModel.getServices())
                && (ObjUtil.isEmpty(thingModel.getScript()) || (StrUtil.isBlank(thingModel.getScript().getContent())));
    }

    //*** 物模型 - 脚本 ***//

    @Override
    public List<PresetScript> listPresetScript() {
        return ThingModelScriptUtils.readGroovyDirectory();
    }

    @Override
    public ThingModelScript describeThingModelScript(String productId) {
        ThingModel thingModel = validateThingModelExists(productId);
        return thingModel.getScript();
    }

    @Override
    public void updateThingModelScript(String productId, ThingModelScript script) {
        // TODO 临时放通可修改已发布产品的物模型
//        ThingModel thingModel = validateThingModelModifiable(productId);

        ThingModel thingModel = validateThingModelExists(productId);
        // 校验脚本 TODO 暂时跳过了Groovy校验，后续补充
        if (script.getType() == JS) {
            validateScript(script);
        }

        thingModel.setScript(script);
        thingModelRepository.save(thingModel);
    }

    private void validateScript(ThingModelScript script) {
        ThingModelScriptTypeEnum scriptType = script.getType();
        String scriptContent = script.getContent();

        switch (scriptType) {
            case PRESET -> {
                // TODO 预置脚本测试通过后上线，不允许修改，暂时不做校验
                return;
            }
            case JS -> {
                ScriptEngine scriptEngine = ScriptUtil.getScript("graal.js");
                try {
                    scriptEngine.eval(scriptContent);
                } catch (ScriptException e) {
                    log.error(THING_MODEL_ILLEGAL_SCRIPT_CONTENT.getMsg());
                    throw exception(THING_MODEL_ILLEGAL_SCRIPT_CONTENT);
                }
                Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
                if (bindings.containsKey("rawDataToProtocol") && bindings.containsKey("protocolToRawData")) {
                    return;
                }
                log.error(THING_MODEL_SCRIPT_NOT_CONTAINS_REQUIRED_FUNC.getMsg());
                throw exception(THING_MODEL_SCRIPT_NOT_CONTAINS_REQUIRED_FUNC);
            }

            default -> {
                log.error("{}:[{}]", THING_MODEL_UNKNOWN_SCRIPT_TYPE.getMsg(), scriptType);
                throw exception(THING_MODEL_UNKNOWN_SCRIPT_TYPE);
            }
        }
    }

    //*** 物模型 - 属性 ***//

    @Override
    public void createThingModelProperty(String productId, ThingModelProperty property) {
        // TODO 临时放通可修改已发布产品的物模型
//        ThingModel thingModel = validateThingModelModifiable(productId);

        ThingModel thingModel = validateThingModelExists(productId);

        List<ThingModelProperty> propertyList = thingModel.getProperties();
        if (ObjUtil.isNull(propertyList)) {
            propertyList = CollUtil.newArrayList();
        }

        // 校验标识符唯一
        validateIdentifierUnique(thingModel, null, property);
        // 验证名称唯一
        validateNameUnique(thingModel, null, property);

        propertyList.add(property);
        thingModel.setProperties(propertyList);
        thingModelRepository.save(thingModel);
    }

    @Override
    public ThingModelProperty describeThingModelProperty(String productId, String identifier) {
        // 校验物模型存在
        ThingModel thingModel = validateThingModelExists(productId);
        // 校验属性存在
        ThingModelProperty property = validatePropertyExists(thingModel, identifier);

        return property;
    }

    @Override
    public List<ThingModelProperty> listThingModelProperty(String productId) {
        ThingModel thingModel = validateThingModelExists(productId);
        return CollUtil.emptyIfNull(thingModel.getProperties());
    }

    @Override
    public void updateThingModelProperty(String productId, String identifier, ThingModelProperty property) {
        // TODO 临时放通可修改已发布产品的物模型
        // 校验物模型可修改
//        ThingModel thingModel = validateThingModelModifiable(productId);

        ThingModel existThingModel = validateThingModelExists(productId);
        // 校验属性存在
        ThingModelProperty existProperty = validatePropertyExists(existThingModel, identifier);
        // 校验标识符唯一
        validateIdentifierUnique(existThingModel, identifier, property);
        // 校验名称唯一
        validateNameUnique(existThingModel, identifier, property);

        // 填充数据
        BeanUtil.copyProperties(property, existProperty);

        thingModelRepository.save(existThingModel);
    }

    @Override
    public void deleteThingModelProperty(String productId, String identifier) {
        // TODO 临时放通可修改已发布产品的物模型
        // 校验物模型可修改
//        ThingModel existsThingModel = validateThingModelModifiable(productId);

        ThingModel existThingModel = validateThingModelExists(productId);
        // 校验属性存在
        ThingModelProperty existProperty = validatePropertyExists(existThingModel, identifier);

        existThingModel.getProperties().remove(existProperty);
        thingModelRepository.save(existThingModel);
    }

    private void validateNameUnique(ThingModel thingModel, String identifier, ThingModelProperty property) {
        if (StrUtil.isBlank(property.getName()) || CollUtil.isEmpty(thingModel.getProperties())) {
            return;
        }

        ThingModelProperty existsProperty = getEntityByName(thingModel.getProperties(), property.getName());
        if (existsProperty == null) {
            return;
        }

        // 创建场景：identifier为空，存在重复数据，抛出异常
        if (identifier == null) {
            throw exception(THING_MODEL_PROPERTY_NAME_DUPLICATE);
        }

        // 更新场景：identifier不为空，重复数据不是自身，抛出异常
        if (!StrUtil.equalsIgnoreCase(identifier, existsProperty.getIdentifier())) {
            throw exception(THING_MODEL_PROPERTY_NAME_DUPLICATE);
        }
    }

    private void validateIdentifierUnique(ThingModel thingModel, String identifier, ThingModelProperty property) {
        if (StrUtil.isBlank(property.getIdentifier()) || CollUtil.isEmpty(thingModel.getProperties())) {
            return;
        }

        ThingModelProperty existsProperty = getEntityByIdentifier(thingModel.getProperties(), property.getIdentifier());
        if (existsProperty == null) {
            return;
        }

        // 创建场景：identifier为空，存在重复数据，抛出异常
        if (identifier == null) {
            throw exception(THING_MODEL_PROPERTY_IDENTIFIER_DUPLICATE);
        }

        // 更新场景：identifier不为空，重复数据不是自身，抛出异常
        if (!StrUtil.equalsIgnoreCase(identifier, existsProperty.getIdentifier())) {
            throw exception(THING_MODEL_PROPERTY_IDENTIFIER_DUPLICATE);
        }
    }

    private ThingModelProperty validatePropertyExists(ThingModel thingModel, String identifier) {
        List<ThingModelProperty> propertyList = thingModel.getProperties();
        if (CollUtil.isEmpty(propertyList)) {
            throw exception(THING_MODEL_PROPERTY_NOT_EXISTS);
        }

        ThingModelProperty property = getEntityByIdentifier(propertyList, identifier);
        if (ObjUtil.isNull(property)) {
            throw exception(THING_MODEL_PROPERTY_NOT_EXISTS);
        }

        return property;
    }

    //*** 物模型 - 事件 ***//

    @Override
    public void createThingModelEvent(String productId, ThingModelEvent event) {
        // TODO 临时放通可修改已发布产品的物模型
        // 校验物模型可修改
//        ThingModel thingModel = validateThingModelModifiable(productId);

        ThingModel thingModel = validateThingModelExists(productId);

        List<ThingModelEvent> eventList = thingModel.getEvents();
        if (ObjUtil.isNull(eventList)) {
            eventList = CollUtil.newArrayList();
        }

        // 校验标识符唯一
        validateIdentifierUnique(thingModel, null, event);
        // 验证名称唯一
        validateNameUnique(thingModel, null, event);

        eventList.add(event);
        thingModel.setEvents(eventList);
        thingModelRepository.save(thingModel);
    }

    @Override
    public ThingModelEvent describeThingModelEvent(String productId, String identifier) {
        // 校验物模型存在
        ThingModel thingModel = validateThingModelExists(productId);
        // 校验事件存在
        ThingModelEvent event = validateEventExists(thingModel, identifier);

        return event;
    }

    @Override
    public List<ThingModelEvent> listThingModelEvent(String productId) {
        ThingModel thingModel = validateThingModelExists(productId);
        return CollUtil.emptyIfNull(thingModel.getEvents());
    }

    @Override
    public void updateThingModelEvent(String productId, String identifier, ThingModelEvent event) {
        // TODO 临时放通可修改已发布产品的物模型
        // 校验物模型可修改
//        ThingModel thingModel = validateThingModelModifiable(productId);

        ThingModel existThingModel = validateThingModelExists(productId);
        // 校验事件存在
        ThingModelEvent existsEvent = validateEventExists(existThingModel, identifier);

        // 校验标识符唯一
        validateIdentifierUnique(existThingModel, identifier, event);
        // 校验名称唯一
        validateNameUnique(existThingModel, identifier, event);

        // 填充数据
        BeanUtil.copyProperties(event, existsEvent);

        thingModelRepository.save(existThingModel);
    }

    @Override
    public void deleteThingModelEvent(String productId, String identifier) {
        // TODO 临时放通可修改已发布产品的物模型
        // 校验物模型可修改
//        ThingModel thingModel = validateThingModelModifiable(productId);

        ThingModel existThingModel = validateThingModelExists(productId);

        // 校验事件存在
        ThingModelEvent existsEvent = validateEventExists(existThingModel, identifier);

        existThingModel.getEvents().remove(existsEvent);
        thingModelRepository.save(existThingModel);
    }

    private void validateNameUnique(ThingModel thingModel, String identifier, ThingModelEvent event) {
        if (StrUtil.isBlank(event.getName()) || CollUtil.isEmpty(thingModel.getEvents())) {
            return;
        }

        ThingModelEvent existsEvent = getEntityByName(thingModel.getEvents(), event.getName());
        if (existsEvent == null) {
            return;
        }

        // 创建场景：identifier为空，存在重复数据，抛出异常
        if (identifier == null) {
            throw exception(THING_MODEL_EVENT_NAME_DUPLICATE);
        }

        // 更新场景：identifier不为空，重复数据不是自身，抛出异常
        if (!StrUtil.equalsIgnoreCase(identifier, existsEvent.getIdentifier())) {
            throw exception(THING_MODEL_EVENT_NAME_DUPLICATE);
        }
    }

    private void validateIdentifierUnique(ThingModel thingModel, String identifier, ThingModelEvent event) {
        if (StrUtil.isBlank(event.getIdentifier()) || CollUtil.isEmpty(thingModel.getEvents())) {
            return;
        }

        ThingModelEvent existsEvent = getEntityByIdentifier(thingModel.getEvents(), event.getIdentifier());
        if (existsEvent == null) {
            return;
        }

        // 创建场景：identifier为空，存在重复数据，抛出异常
        if (identifier == null) {
            throw exception(THING_MODEL_EVENT_IDENTIFIER_DUPLICATE);
        }

        // 更新场景：identifier不为空，重复数据不是自身，抛出异常
        if (!StrUtil.equalsIgnoreCase(identifier, existsEvent.getIdentifier())) {
            throw exception(THING_MODEL_EVENT_IDENTIFIER_DUPLICATE);
        }
    }

    private ThingModelEvent validateEventExists(ThingModel thingModel, String identifier) {
        List<ThingModelEvent> eventList = thingModel.getEvents();
        if (CollUtil.isEmpty(eventList)) {
            throw exception(THING_MODEL_EVENT_NOT_EXISTS);
        }

        ThingModelEvent existsEvent = getEntityByIdentifier(eventList, identifier);
        if (ObjUtil.isNull(existsEvent)) {
            throw exception(THING_MODEL_EVENT_NOT_EXISTS);
        }

        return existsEvent;
    }

    //*** 物模型 - 服务 ***//

    @Override
    public void createThingModelService(String productId, ThingModelService service) {
        // 校验物模型存在
        ThingModel thingModel = validateThingModelModifiable(productId);

        List<ThingModelService> serviceList = thingModel.getServices();
        if (ObjUtil.isNull(serviceList)) {
            serviceList = CollUtil.newArrayList();
        }

        // 校验标识符唯一
        validateIdentifierUnique(thingModel, null, service);
        // 验证名称唯一
        validateNameUnique(thingModel, null, service);

        serviceList.add(service);
        thingModel.setServices(serviceList);
        thingModelRepository.save(thingModel);
    }

    @Override
    public ThingModelService describeThingModelService(String productId, String identifier) {
        // 校验物模型存在
        ThingModel thingModel = validateThingModelExists(productId);
        // 校验服务存在
        return validateServiceExists(thingModel, identifier);
    }

    @Override
    public List<ThingModelService> listThingModelService(String productId) {
        ThingModel thingModel = validateThingModelExists(productId);

        log.debug(thingModel.toString());

        return thingModel.getServices();
    }

    @Override
    public void updateThingModelService(String productId, String identifier, ThingModelService service) {
        // 校验物模型可修改
        ThingModel thingModel = validateThingModelModifiable(productId);
        // 校验服务存在
        ThingModelService existsService = validateServiceExists(thingModel, identifier);

        // 校验标识符唯一
        validateIdentifierUnique(thingModel, identifier, service);
        // 校验名称唯一
        validateNameUnique(thingModel, identifier, service);

        // 填充数据
        BeanUtil.copyProperties(service, existsService);

        thingModelRepository.save(thingModel);
    }

    @Override
    public void deleteThingModelService(String productId, String identifier) {
        // 校验物模型可修改
        ThingModel thingModel = validateThingModelModifiable(productId);
        // 校验服务存在
        ThingModelService existsService = validateServiceExists(thingModel, identifier);

        thingModel.getServices().remove(existsService);

        thingModelRepository.save(thingModel);
    }

    private void validateNameUnique(ThingModel thingModel, String identifier, ThingModelService service) {
        if (StrUtil.isBlank(service.getName()) || CollUtil.isEmpty(thingModel.getServices())) {
            return;
        }

        ThingModelService existsService = getEntityByName(thingModel.getServices(), service.getName());
        if (existsService == null) {
            return;
        }

        // 创建场景：identifier为空，存在重复数据，抛出异常
        if (identifier == null) {
            throw exception(THING_MODEL_SERVICE_NAME_DUPLICATE);
        }

        // 更新场景：identifier不为空，重复数据不是自身，抛出异常
        if (!StrUtil.equalsIgnoreCase(identifier, existsService.getIdentifier())) {
            throw exception(THING_MODEL_SERVICE_NAME_DUPLICATE);
        }
    }

    private void validateIdentifierUnique(ThingModel thingModel, String identifier, ThingModelService service) {
        if (StrUtil.isBlank(service.getIdentifier()) || CollUtil.isEmpty(thingModel.getServices())) {
            return;
        }

        ThingModelService existsService = getEntityByIdentifier(thingModel.getServices(), service.getIdentifier());
        if (existsService == null) {
            return;
        }

        // 创建场景：identifier为空，存在重复数据，抛出异常
        if (identifier == null) {
            throw exception(THING_MODEL_SERVICE_IDENTIFIER_DUPLICATE);
        }

        // 更新场景：identifier不为空，重复数据不是自身，抛出异常
        if (!StrUtil.equalsIgnoreCase(identifier, existsService.getIdentifier())) {
            throw exception(THING_MODEL_SERVICE_IDENTIFIER_DUPLICATE);
        }
    }

    private ThingModelService validateServiceExists(ThingModel thingModel, String identifier) {
        List<ThingModelService> serviceList = thingModel.getServices();
        if (CollUtil.isEmpty(serviceList)) {
            throw exception(THING_MODEL_SERVICE_NOT_EXISTS);
        }
        ThingModelService service = getEntityByIdentifier(serviceList, identifier);
        if (ObjUtil.isNull(service)) {
            throw exception(THING_MODEL_SERVICE_NOT_EXISTS);
        }

        return service;
    }

    //*** 物模型 - 服务参数 ***//

    @Override
    public void createThingModelServiceParam(String productId, String serviceIdentifier, ThingModelServiceParam serviceParam, ThingModelServiceParamDirectionEnum paramDirection) {
        // 校验物模型可修改
        ThingModel thingModel = validateThingModelModifiable(productId);
        // 校验服务存在
        ThingModelService service = validateServiceExists(thingModel, serviceIdentifier);

        List<ThingModelServiceParam> existsParamList = getServiceParamListByDirection(service, paramDirection);
        if (ObjUtil.isNull(existsParamList)) {
            existsParamList = CollUtil.newArrayList();
        }

        // 校验标识符唯一
        validateIdentifierUnique(service, null, serviceParam, paramDirection);
        // 验证名称唯一
        validateNameUnique(service, null, serviceParam, paramDirection);

        existsParamList.add(serviceParam);
        thingModelRepository.save(thingModel);
    }

    @Override
    public ThingModelServiceParam describeThingModelServiceParam(String productId, String serviceIdentifier, String paramIdentifier, ThingModelServiceParamDirectionEnum paramDirection) {
        // 校验物模型存在
        ThingModel thingModel = validateThingModelExists(productId);
        // 校验服务存在
        ThingModelService service = validateServiceExists(thingModel, serviceIdentifier);
        // 校验服务参数存在
        ThingModelServiceParam param = validateServiceParamExists(service, paramIdentifier, paramDirection);

        return param;
    }

    @Override
    public List<ThingModelServiceParam> listThingModelServiceParam(String productId, String serviceIdentifier, ThingModelServiceParamDirectionEnum paramDirection) {
        // 校验物模型存在
        ThingModel thingModel = validateThingModelExists(productId);
        // 校验服务存在
        ThingModelService service = validateServiceExists(thingModel, serviceIdentifier);

        return getServiceParamListByDirection(service, paramDirection);
    }

    @Override
    public void updateThingModelServiceParam(String productId, String serviceIdentifier, String paramIdentifier, ThingModelServiceParam serviceParam, ThingModelServiceParamDirectionEnum paramDirection) {
        // 校验物模型可修改
        ThingModel thingModel = validateThingModelModifiable(productId);
        // 校验服务存在
        ThingModelService service = validateServiceExists(thingModel, serviceIdentifier);
        // 校验服务参数存在
        ThingModelServiceParam existsParam = validateServiceParamExists(service, paramIdentifier, paramDirection);

        // 校验标识符唯一
        validateIdentifierUnique(service, existsParam.getIdentifier(), serviceParam, paramDirection);
        // 校验名称唯一
        validateNameUnique(service, existsParam.getIdentifier(), serviceParam, paramDirection);

        // 填充数据
        BeanUtil.copyProperties(serviceParam, existsParam);

        thingModelRepository.save(thingModel);
    }

    @Override
    public void deleteThingModelServiceParam(String productId, String serviceIdentifier, String paramIdentifier, ThingModelServiceParamDirectionEnum paramDirection) {
        // 校验物模型可修改
        ThingModel thingModel = validateThingModelModifiable(productId);
        // 校验事件存在
        ThingModelService service = validateServiceExists(thingModel, serviceIdentifier);
        // 校验服务参数存在
        ThingModelServiceParam param = validateServiceParamExists(service, paramIdentifier, paramDirection);

        List<ThingModelServiceParam> paramList = getServiceParamListByDirection(service, paramDirection);
        paramList.remove(param);
        thingModelRepository.save(thingModel);
    }

    private void validateNameUnique(ThingModelService service, String paramIdentifier,
                                    ThingModelServiceParam serviceParam, ThingModelServiceParamDirectionEnum paramDirection) {
        assert service != null;

        List<ThingModelServiceParam> paramList = getServiceParamListByDirection(service, paramDirection);
        if (CollUtil.isEmpty(paramList)) {
            return;
        }

        ThingModelServiceParam param = getEntityByName(paramList, serviceParam.getName());
        if (ObjUtil.isNull(param)) {
            return;
        }

        // 创建场景：identifier为空，存在重复数据，抛出异常
        if (paramIdentifier == null) {
            throw exception(THING_MODEL_SERVICE_PARAM_NAME_DUPLICATE);
        }

        // 更新场景：identifier不为空，重复数据不是自身，抛出异常
        if (!StrUtil.equalsIgnoreCase(paramIdentifier, param.getIdentifier())) {
            throw exception(THING_MODEL_SERVICE_PARAM_NAME_DUPLICATE);
        }
    }

    private void validateIdentifierUnique(ThingModelService service, String paramIdentifier,
                                          ThingModelServiceParam serviceParam, ThingModelServiceParamDirectionEnum paramDirection) {
        assert service != null;

        List<ThingModelServiceParam> paramList = getServiceParamListByDirection(service, paramDirection);
        if (CollUtil.isEmpty(paramList)) {
            return;
        }

        ThingModelServiceParam param = getEntityByIdentifier(paramList, serviceParam.getIdentifier());
        if (ObjUtil.isNull(param)) {
            return;
        }

        // 创建场景：identifier为空，存在重复数据，抛出异常
        if (paramIdentifier == null) {
            throw exception(THING_MODEL_SERVICE_PARAM_IDENTIFIER_DUPLICATE);
        }

        // 更新场景：identifier不为空，重复数据不是自身，抛出异常
        if (!StrUtil.equalsIgnoreCase(paramIdentifier, param.getIdentifier())) {
            throw exception(THING_MODEL_SERVICE_PARAM_IDENTIFIER_DUPLICATE);
        }
    }

    private ThingModelServiceParam validateServiceParamExists(ThingModelService service, String paramIdentifier, ThingModelServiceParamDirectionEnum paramDirection) {
        List<ThingModelServiceParam> paramList = getServiceParamListByDirection(service, paramDirection);
        if (CollUtil.isEmpty(paramList)) {
            throw exception(THING_MODEL_SERVICE_PARAM_NOT_EXISTS);
        }

        ThingModelServiceParam param = getEntityByIdentifier(paramList, paramIdentifier);
        if (ObjUtil.isNull(param)) {
            throw exception(THING_MODEL_SERVICE_PARAM_NOT_EXISTS);
        }

        return param;
    }

    private List<ThingModelServiceParam> getServiceParamListByDirection(ThingModelService service, ThingModelServiceParamDirectionEnum direction) {
        assert service != null;

        if (direction == INPUT) {
            return service.getInputParams();
        }
        if (direction == OUTPUT) {
            return service.getOutputParams();
        }

        return ListUtil.empty();
    }

    //*** 公共辅助方法 ***//

    private <T extends ThingModelBase> T getEntityByName(List<T> list, String name) {
        return list.stream().filter(item -> StrUtil.equalsIgnoreCase(item.getName(), name)).findFirst().orElse(null);
    }

    private <T extends ThingModelBase> T getEntityByIdentifier(List<T> list, String identifier) {
        return list.stream().filter(item -> StrUtil.equalsIgnoreCase(item.getIdentifier(), identifier)).findFirst().orElse(null);
    }
}
