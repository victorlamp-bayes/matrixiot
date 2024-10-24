package com.victorlamp.matrixiot.service.management.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.cache.CaffeineCacheUtils;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingDataService;
import com.victorlamp.matrixiot.service.management.api.ThingModelService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.convert.ThingConvert;
import com.victorlamp.matrixiot.service.management.dao.AreaRepository;
import com.victorlamp.matrixiot.service.management.dao.ExternalConfigTemplateRepository;
import com.victorlamp.matrixiot.service.management.dao.ThingRepository;
import com.victorlamp.matrixiot.service.management.dto.thing.*;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigItem;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.*;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import com.victorlamp.matrixiot.service.management.enums.NodeTypeEnum;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.management.service.area.Area;
import com.victorlamp.matrixiot.service.management.utils.ThingExternalConfigUtil;
import com.victorlamp.matrixiot.service.management.utils.ThingGeoUtils;
import com.victorlamp.matrixiot.service.management.utils.ThingServiceDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.management.constant.ErrorCodeConstants.*;

@DubboService(validation = "true")
@Service("thingService")
@Slf4j
public class ThingServiceImpl implements ThingService {
    @Resource
    private ThingRepository thingRepository;
    @Resource
    private ExternalConfigTemplateRepository externalConfigTemplateRepository;
    @Resource
    private ProductService productService;
    @Resource
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
    private ThingDataService thingDataService;
    @Resource
    private RocketMQTemplateProducer mqProducer;

    @Resource
    private AreaRepository areaRepository;

    @Override
    public Thing createThing(ThingCreateReqDTO reqDTO) {
        // 验证产品存在且已发布
        Product product = validateProductExistsAndPublished(reqDTO.getProductId());
        // 验证设备名称唯一
        validateThingNameUnique(null, reqDTO.getName());
        // TODO 子设备验证

        Thing thing = ThingConvert.INSTANCE.toEntity(reqDTO);

        // 创建时自动启用的设备，显式设置在线状态为离线
        if (thing.getEnabled() != null && thing.getEnabled()) {
            thing.setOnline(false);
        }
        thing.setProduct(product);
        return thingRepository.insert(thing);

        // TODO 向外部平台注册该设备
    }

    @Override
    public Thing getThing(String id) {
        return thingRepository.findById(id).orElse(null);
    }

    @Override
    public void updateThing(String id, ThingUpdateReqDTO reqDTO) {
        // 验证设备存在
        Thing thing = validateThingExists(id);

        // 修改设备名称
        if (!StrUtil.isNullOrUndefined(reqDTO.getName())) {
            validateThingNameUnique(reqDTO.getId(), reqDTO.getName());
            thing.setName(reqDTO.getName());
        }
        // 修改设备描述
        if (!StrUtil.isNullOrUndefined(reqDTO.getDescription())) {
            thing.setDescription(reqDTO.getDescription());
        }
        // 修改设备位置
        if (ObjUtil.isNotNull(reqDTO.getPosition())) {
            thing.setPosition(reqDTO.getPosition());
        }
        // 修改设备标签
        if (ObjUtil.isNotNull(reqDTO.getTags())) {
            thing.setTags(reqDTO.getTags());
        }
        // 修改设备启用状态
        if (ObjUtil.isNotNull(reqDTO.getEnabled()) && ObjUtil.notEqual(reqDTO.getEnabled(), thing.getEnabled())) {
            thing.setEnabled(reqDTO.getEnabled());
        }
        // 修改设备在线状态
        if (ObjUtil.isNotNull(reqDTO.getOnline()) && ObjUtil.notEqual(reqDTO.getOnline(), thing.getOnline())) {
            thing.setOnline(reqDTO.getOnline());
            if (reqDTO.getOnline()) {
                thing.setConnectedAt(DateUtil.current());
            } else {
                thing.setDisconnectedAt(DateUtil.current());
            }
        }
        // 修改设备额外配置
        if (ObjUtil.isNotNull(reqDTO.getExternalConfig())) {
            ThingExternalConfig externalConfigDTO = reqDTO.getExternalConfig();
            if (StrUtil.isBlank(externalConfigDTO.getType())) {
                throw exception(THING_INVALID_EXTERNAL_TYPE);
            }
            ThingExternalConfig externalConfig = new ThingExternalConfig(
                    externalConfigDTO.getType(),
                    externalConfigDTO.getConfig());
            thing.setExternalConfig(externalConfig);
        }

        thingRepository.save(thing);
    }

    @Override
    public void updateThingOnlineStatus(String id, boolean online) {
        Thing thing = validateThingExists(id);
        if (!thing.getEnabled()) {
            throw exception(THING_CANNOT_UPDATE_DISABLED_THING);
        }

        // 设备无论在线还是离线，均更新在线时间
        if (online) {
            thing.setOnline(true);
            thing.setConnectedAt(DateUtil.current());
            thing.setDisconnectedAt(null);
            thingRepository.save(thing);
            return;
        }

        // 设备 在线->离线，更新离线时间
        if (thing.getOnline()) {
            thing.setOnline(false);
            thing.setDisconnectedAt(DateUtil.current());
            thingRepository.save(thing);
            return;
        }

        // 离线 -> 离线，不处理
        return;
    }

    @Override
    public void deleteThing(String id) {
        // 验证设备存在
        Thing thing = validateThingExists(id);

        if (thing.getEnabled()) {
            throw exception(THING_CANNOT_DELETE_ENABLED);
        }

        // 验证未绑定子设备
        validateNotBindSubThing(id);

        thingRepository.deleteById(id);
    }

    @Override
    public List<Thing> listAll(String productId) {
        if (StrUtil.isNotBlank(productId)) {
            return thingRepository.findAllByProductId(productId);
        }

        return thingRepository.findAll();
    }


    @Override
    public PageResult<Thing> listThingPage(ThingPageReqDTO reqDTO) {
        return thingRepository.findPage(reqDTO);
    }

    @Override
    public void importThings(List<ThingImportReqDTO> importThings, String productId, boolean updateSupport) {
        for (ThingImportReqDTO importThing : importThings) {
            importThing.setProductId(productId);
            log.debug("导入设备:[{}]", importThing);
            String externalConfigDeviceId = ThingExternalConfigUtil.getExternalDeviceId(importThing.getExternalConfig());
            Thing existThing = thingRepository.findByExternalConfigId(externalConfigDeviceId);
            if (ObjUtil.isNull(existThing)) {
                ThingCreateReqDTO createReqDTO = ThingConvert.INSTANCE.importToCreateDTO(importThing);
                createThing(createReqDTO);
                continue;
            }
            if (!updateSupport) {
                continue;
            }
            ThingUpdateReqDTO updateReqDTO = ThingConvert.INSTANCE.importToUpdateDTO(importThing);
            updateThing(existThing.getId(), updateReqDTO);
        }
    }

    public void exportThing() {

    }

    @Override
    public void addSubThing(String gatewayId, String subThingId) {
        validateGatewayExists(gatewayId);
        Thing subThing = validateSubThingExists(subThingId);
        subThing.setGatewayId(gatewayId);
        thingRepository.save(subThing);
    }

    @Override
    public void addAllSubThing(String gatewayId, String productId) {
        validateGatewayExists(gatewayId);
        validateProductExistsAndPublished(productId);
        // TODO 子设备数量可能过多，使用批处理提供性能
        thingRepository.updateGatewayIdByProductId(gatewayId, productId);
    }

    @Override
    public void batchAddSubThing(String gatewayId, List<String> subThingId) {
        validateGatewayExists(gatewayId);
        List<Thing> subThingList = CollUtil.newArrayList();
        subThingId.forEach(sub -> {
            Thing subThing = validateThingExists(sub);
            subThing.setGatewayId(gatewayId);
            subThingList.add(subThing);
        });
        thingRepository.saveAll(subThingList);
    }

    @Override
    public void removeSubThing(String gatewayId, String subThingId) {
        Thing gateway = validateGatewayExists(gatewayId);
        Thing subThing = validateSubThingExists(subThingId);
        validateIsGatewayAndSubThing(gateway, subThing);
        subThing.setGatewayId(null);
        thingRepository.save(subThing);
    }

    @Override
    public void batchRemoveSubThing(String gatewayId, List<String> subThingId) {
        Thing gateway = validateGatewayExists(gatewayId);
        List<Thing> subThingList = CollUtil.newArrayList();
        subThingId.forEach(sub -> {
            Thing subThing = validateSubThingExists(sub);
            validateIsGatewayAndSubThing(gateway, subThing);
            subThing.setGatewayId(null);
            subThingList.add(subThing);
        });
        thingRepository.saveAll(subThingList);
    }

    @Override
    public void removeAllSubThing(String gatewayId) {
        List<Thing> subThingList = thingRepository.findByGatewayId(gatewayId);
        subThingList.forEach(subThing -> subThing.setGatewayId(null));
        thingRepository.saveAll(subThingList);
    }

    @Override
    public PageResult<Thing> listSubThingPage(String gatewayId, SubThingPageReqDTO reqDTO) {
        validateGatewayExists(gatewayId);
        return thingRepository.findPageByGatewayId(reqDTO);
    }

    @Override
    public Boolean existsByProductId(String productId) {
        return thingRepository.existsByProductId(productId);
    }

    @Override
    public Thing getThingByExternalConfigId(String id) {
        return thingRepository.findByExternalConfigId(id);
    }

    @Override
    public Thing getThingByExternalConfigDeviceNo(String deviceNo) {
        return thingRepository.findByExternalConfigDeviceNo(deviceNo);
    }

    @Override
    public Boolean existsByExternalConfigItem(String name, Object value) {
        return thingRepository.existsByExternalConfigItem(name, value);
    }

    @Override
    public List<ExternalConfigItem> listExternalConfigItem(String type) {
        return externalConfigTemplateRepository.findByType(type).getThing();
    }

    @Override
    public ThingStatistics getStatistics(String productId) {
        // TODO 校验productId

        // 未传递productId时，统计所有产品数据
        ThingPageReqDTO reqDTO = new ThingPageReqDTO();

        if (StrUtil.isNotBlank(productId)) {
            reqDTO.setProductId(productId);
        }
        int total = (int) thingRepository.countThing(reqDTO);

        reqDTO.setEnabled(true);
        int enabled = (int) thingRepository.countThing(reqDTO);

        reqDTO.setOnline(true);
        int online = (int) thingRepository.countThing(reqDTO);

        return new ThingStatistics(total, enabled, online);
    }

    @Override
    public ThingGeo getAggregatedThingGeo(ThingGeoReqDTO reqDTO) {
        Area area = areaRepository.getAreaByCode(520322);//桐梓县

        //1.行政编码区域的中心点，查询没有位置的设备：
        JSONObject properties = area.getBound().getJSONArray("features").getJSONObject(0).getJSONObject("properties");
        JSONArray centerPosition = properties.getJSONArray("center"); //中心点位置
        double centerLon = centerPosition.getDouble(0);
        double centerLat = centerPosition.getDouble(1);

        GeoPoint centerPoint = new GeoPoint(centerLon, centerLat);
        List<Thing> noGeoThingList = thingRepository.findByTenantId();
        GridCellThing noGeoThings = new GridCellThing(centerPoint, noGeoThingList.size());

        //2.获取行政编码区域经纬度范围
        JSONObject geometry = area.getBound().getJSONArray("features").getJSONObject(0).getJSONObject("geometry");
        JSONArray coordinatesArray = geometry.getJSONArray("coordinates").getJSONArray(0).getJSONArray(0);

        int size = coordinatesArray.size();
        double[][] coordinates = new double[size][2];

        for (int i = 0; i < size; i++) {
            JSONArray coordinate = coordinatesArray.getJSONArray(i);
            coordinates[i][0] = coordinate.getDouble(0);
            coordinates[i][1] = coordinate.getDouble(1);
        }

        List<Thing> things = thingRepository.findByProvinceBoundary(coordinates);

        //实现经纬度范围参数查询所有带有位置信息的设备
        List<GridCellThing> gridCellThings = ThingGeoUtils.getGridCellThings(reqDTO, things);
        ThingGeo thingGeo = new ThingGeo();
        thingGeo.setGridCellList(gridCellThings);
        thingGeo.setNoGeoThings(noGeoThings);
        return thingGeo;
    }

    @Override
    public void invokeThingService(ThingData thingData) {
        if (!ObjUtil.isNotEmpty(thingData.getService())) {
            throw exception(THING_INVOKE_SERVICE_EMPTY_DATA);
        }

        // 校验设备
        Thing thing = validateThingExists(thingData.getThingId());

        // 校验设备已启用
        if (!thing.getEnabled()) {
            throw exception(THING_DATA_THING_DISABLED);
        }

        // 校验产品存在且已发布
        validateProductExistsAndPublished(thing.getProduct().getId());

        // 获取物模型
        ThingModel thingModel = thingModelCache.get(thingData.getProductId());
        // 检查数据是否合法
        ThingServiceDataUtils.validateServiceData(thingModel, thingData.getService(), null);

        // 创建服务
        ThingData newThingData = thingDataService.createServiceData(thingData);
        if (ObjUtil.isNull(newThingData)) {
            log.info("服务调用创建失败，数据[{}]", newThingData);
            throw exception(THING_DATA_CREATE_SERVICE_DATA_FAILED);
        }
        // MQ到agent执行下发
        mqProducer.asyncSendMessage(ThingTopic.THING_SERVICE_INVOKE, newThingData);
    }

    @Override
    public void thirdInvokeThingService(InvokeServiceReqDTO reqDTO) {
        log.debug("设备调用服务数据:{}", reqDTO);

        if (ObjUtil.isNull(reqDTO)) {
            throw exception(THING_INVOKE_SERVICE_EMPTY_DATA);
        }

        if (!CollUtil.isNotEmpty(reqDTO.getInputParams())) {
            throw exception(THING_INVOKE_SERVICE_EMPTY_INPUT_PARAM);
        }

        // 校验设备
        Thing thing = thingRepository.findByExternalConfigDeviceNo(reqDTO.getDeviceNo());
        if (ObjUtil.isNull(thing)) {
            log.info("设备不存在[{}]", reqDTO.getDeviceNo());
            throw exception(THING_NOT_EXISTS);
        }

        // 校验设备已启用
        if (!thing.getEnabled()) {
            throw exception(THING_DATA_THING_DISABLED);
        }

        // 校验产品存在且已发布
        validateProductExistsAndPublished(thing.getProduct().getId());

        // 获取物模型
        ThingModel thingModel = thingModelCache.get(thing.getProduct().getId());
        // 检查数据是否合法
        ThingServiceDataUtils.validateServiceData(thingModel, null, reqDTO);

        // 构造ThingData
        ThingData thingData = ThingServiceDataUtils.buildThingData(thing, reqDTO);

        // 创建服务
        ThingData newThingData = thingDataService.createServiceData(thingData);
        if (ObjUtil.isNull(newThingData)) {
            log.info("服务调用失败，数据[{}]", newThingData);
            throw exception(THING_DATA_CREATE_SERVICE_DATA_FAILED);
        }
        // MQ到agent执行下发
        mqProducer.asyncSendMessage(ThingTopic.THING_SERVICE_INVOKE, newThingData);
    }

    private Product validateProductExistsAndPublished(String productId) {
        Product product = productService.getProduct(productId);
        if (product == null) {
            throw exception(THING_PRODUCT_NOT_EXISTS);
        }
        if (!product.getPublished()) {
            throw exception(THING_PRODUCT_NOT_PUBLISHED);
        }

        return product;
    }

    private void validateThingNameUnique(String id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }

        Thing thing = thingRepository.findByName(name);
        if (thing == null) {
            return;
        }

        // 如果 id 为空，说明不用比较是否为相同 id 的产品
        if (id == null) {
            throw exception(THING_NAME_DUPLICATE);
        }
        if (!thing.getId().equals(id)) {
            throw exception(THING_NAME_DUPLICATE);
        }
    }

    private Thing validateThingExists(String id) {
        Thing thing = thingRepository.findById(id).orElse(null);
        if (ObjUtil.isNull(thing)) {
            throw exception(THING_NOT_EXISTS);
        }
        return thing;
    }

    private void validateNotBindSubThing(String id) {
        if (thingRepository.existsByGatewayId(id)) {
            throw exception(THING_CANNOT_DELETE_BIND_SUB_THING);
        }
    }

    private Thing validateGatewayExists(String id) {
        Thing thing = thingRepository.findById(id).orElse(null);
        if (ObjUtil.isNull(thing)) {
            throw exception(THING_NOT_EXISTS);
        }
        if (thing.getProduct().getNodeType() != NodeTypeEnum.GATEWAY) {
            throw exception(THING_CANNOT_ADD_SUB_THING_TO_NON_GATEWAY);
        }
        return thing;
    }

    private Thing validateSubThingExists(String id) {
        Thing thing = thingRepository.findById(id).orElse(null);
        if (ObjUtil.isNull(thing)) {
            throw exception(THING_NOT_EXISTS);
        }
        if (thing.getProduct().getNodeType() != NodeTypeEnum.SUBDEVICE) {
            throw exception(THING_CANNOT_ADD_NON_SUB_THING_TO_GATEWAY);
        }
        return thing;
    }

    private void validateIsGatewayAndSubThing(Thing gateway, Thing subThing) {
        if (ObjUtil.isNull(gateway)) {
            throw exception(THING_CANNOT_REMOVE_FROM_NULL_GATEWAY);
        }
        if (ObjUtil.isNull(subThing)) {
            throw exception(THING_CANNOT_REMOVE_NULL_SUB_THING);
        }
        if (!StrUtil.equalsIgnoreCase(gateway.getId(), subThing.getGatewayId())) {
            throw exception(THING_NOT_GATEWAY_AND_SUB_THING);
        }
    }

    ////////////////////////////////////

//    @Override
//    public void setThingProperties(String thingId, ThingData.CommandDTO requestDTO) {
////        long timestamp = (new Date()).getTime();
////
////        Thing thing = thingUtils.getValidThing(thingId);
////        String productId = thing.getProduct().getId();
////        ThingModel thingModel = thingUtils.getValidThingModel(productId);
////
////        // 1.接收到的getCommand，是一个 Map<identifier:value>
////        Map<String, Object> params = requestDTO.getCommandContent();
////        if (params != null && !params.isEmpty()) {
////            for (Map.Entry<String, Object> entry : params.entrySet()) {
////                // 2.拿到接收到的 getPropertyCommand 中的每一组键值对，分别为 identifier: value;
////                String propertyIdentifier = entry.getKey();
////                Object propertyValue = entry.getValue();
////                boolean identifierMatched = false; // 标记是否找到匹配的属性标识符
////                for (ThingModel.Property property : thingModel.getProperties()) {
////                    // 3.如果该组的 identifier 与物模型中已有的 identifier 相同，则需要验证 value 的值是否合法
////                    if (property.getIdentifier().equals(propertyIdentifier)) {
////                        // 验证属性值是否符合规范
////                        ThingModelDTO.PropertyDTO propertyDTO = ThingModelMapper.INSTANCE.toPropertyDTO(property);
////                        thingUtils.validateParamValue(propertyValue, propertyDTO.getDataType(), propertyDTO.getDataSpec());
////                        identifierMatched = true;
////                    }
////                }
////                // 4.如果循环物模型结束，该接收到的 identifier 仍然匹配不到相同的物模型中的 identifier，则这个请求应该抛出错误，强行终止一下一步的操作，因为该请求无效;
////                if (!identifierMatched) {
////                    throw new ServiceException(
////                            ServiceException.ExceptionType.INVALID_REQUEST,
////                            ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST_COMMON, "设置设备的属性中的该{}在物模型中不存在", propertyIdentifier)
////                    );
////                }
////            }
////        } else {
////            throw new ServiceException(
////                    ServiceException.ExceptionType.INVALID_REQUEST,
////                    ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST_NULL_VALUE, "设置设备的属性中的PropertyCommand")
////            );
////        }
////
////        ThingDataDTO.CommandDTO commandDTO = new ThingDataDTO.CommandDTO();
////        commandDTO.setCommandType(CommandType.PROPERTY_SET.getName());
////        commandDTO.setCommandContent(params);
////
////        ThingData thingData = new ThingData();
////        thingData.setThingId(thingId);
////        thingData.setProductId(productId);
////        thingData.setTimestamp(timestamp);
////        thingData.setCommand(ThingDataMapper.INSTANCE.toCommand(commandDTO));
////        thingUtils.asyncSendMessage(ThingTopic.THING_PROPERTY_SET, thingData);
//    }

//    @Override
//    public void invokeThingService(String thingId, ThingData.CommandDTO.ServiceCommandDTO requestDTO) {
////        long timestamp = (new Date()).getTime();
////        //首先找到设备对应的产品和物模型，然后对比服务identifier看是否合法
////        Thing thing = thingUtils.getValidThing(thingId);
////
////        String productId = thing.getProduct().getId();
////        String serviceIdentifier = requestDTO.getIdentifier();
////        Map<String, Object> params = requestDTO.getParams();
////
////        if (params != null && !params.isEmpty()) {
////            for (Map.Entry<String, Object> entry : params.entrySet()) {
////                String paramIdentifier = entry.getKey();
////                Object paramValue = entry.getValue();
////
////                ThingModelDTO.ServiceDTO.ParamDTO paramDTO = thingModelService.describeThingModelServiceParameter(productId, serviceIdentifier, paramIdentifier).getData();
////                thingUtils.validateParamValue(paramValue, paramDTO.getDataType(), paramDTO.getDataSpec());
////            }
////        }
////
////        ThingDataDTO.CommandDTO commandDTO = new ThingDataDTO.CommandDTO();
////        commandDTO.setCommandType(CommandType.SERVICE_INVOKE.getName());
////        commandDTO.setServiceCommand(requestDTO);
////
////        //将serviceCommandDTO转换成Map<String, Object>，来统一存储为command格式;
////        String jsonStr = JSON.toJSONString(commandDTO.getServiceCommand());
////        Map<String, Object> serviceCommand = JSON.parseObject(jsonStr, new TypeReference<>() {
////        });
////        commandDTO.setCommandContent(serviceCommand);
////
////        ThingData thingData = new ThingData();
////        thingData.setThingId(thingId);
////        thingData.setProductId(productId);
////        thingData.setTimestamp(timestamp);
////        thingData.setCommand(ThingDataMapper.INSTANCE.toCommand(commandDTO));
////        thingUtils.asyncSendMessage(ThingTopic.THING_SERVICE_INVOKE, thingData);
//    }
}
