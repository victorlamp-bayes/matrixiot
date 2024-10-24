package com.victorlamp.matrixiot.service.management.service;

import cn.hutool.core.util.ObjUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.common.constant.ThingTopic;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingDataService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.dao.ThingDataRepository;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingEventDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingPropertyDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingServiceDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import com.victorlamp.matrixiot.configuration.message.RocketMQTemplateProducer;
import com.victorlamp.matrixiot.service.route.enums.DataSourceTopicEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.management.constant.ErrorCodeConstants.*;

@DubboService(validation = "true")
@Service("thingDataService")
@Slf4j
public class ThingDataServiceImpl implements ThingDataService {

    @Resource
    private ThingService thingService;
    @Resource
    private ProductService productService;
    @Resource
    private ThingDataRepository thingDataRepository;
    @Resource
    private RocketMQTemplateProducer mqProducer;

    @Override
    public ThingData createThingData(ThingData thingData) {
        // 校验设备存在
        Thing thing = validateThingExists(thingData.getThingId());
        // 校验设备已启用
        if (!thing.getEnabled()) {
            throw exception(THING_DATA_THING_DISABLED);
        }
        // 校验产品存在且已发布
        validateProductExists(thingData.getProductId());

//        if (isThingDataDuplicated(thingData)) {
//            log.info("上报设备数据重复");
//            return null;
//        }

        // 写入数据库
        ThingData newThingData = thingDataRepository.insert(thingData);

        // 发送到消息队列Topic：THING_POST_WARNING
        if (newThingData.getEvents() != null && !newThingData.getEvents().isEmpty()) {
            mqProducer.asyncSendMessage(ThingTopic.THING_POST_WARNING, newThingData);
        }

        // 发送到消息队列Topic：THING_DATA_CHANGED。使用产品id和设备id进行过滤
        String pid = newThingData.getProductId();
        String tid = newThingData.getThingId();
        mqProducer.asyncSendMessage(DataSourceTopicEnum.THING_DATA_CHANGED.getTopic(), newThingData, pid, tid);
        return newThingData;
    }

    @Override
    public List<ThingPropertyData> getLatestThingPropertyData(String thingId) {
        // 校验设备存在
        Thing thing = validateThingExists(thingId);

        // TODO 当前系统直接取最新数据就包含了所有物模型数据，简化处理。后续根据需求修改实现
        ThingData thingData = thingDataRepository.findLatestThingData(thingId);

        if (ObjUtil.isEmpty(thingData)) {
            return Collections.emptyList();
        }

        return thingData.getProperties();
    }

    @Override
    public PageResult<ThingData> listThingDataPage(ThingDataPageReqDTO reqDTO) {
        // 校验设备存在
        validateThingExists(reqDTO.getThingId());

        return thingDataRepository.findPage(reqDTO);
    }

    @Override
    public PageResult<ThingData> listThingPropertyDataPage(ThingPropertyDataPageReqDTO reqDTO) {
        // 校验设备存在
        validateThingExists(reqDTO.getThingId());

        return thingDataRepository.findPage(reqDTO);
    }

    @Override
    public PageResult<ThingData> listThingEventDataPage(ThingEventDataPageReqDTO reqDTO) {
        // 校验设备存在
        validateThingExists(reqDTO.getThingId());

        return thingDataRepository.findPage(reqDTO);
    }

    @Override
    public PageResult<ThingData> listThingServiceDataPage(ThingServiceDataPageReqDTO reqDTO) {
        // 校验设备存在
        validateThingExists(reqDTO.getThingId());

        return thingDataRepository.findPage(reqDTO);
    }

    @Override
    public Boolean isThingDataDuplicated(ThingData thingData) {
        // 只比较最新的一条数据
        ThingData latestThingData = thingDataRepository.findLatestThingData(thingData.getThingId());
        if (ObjUtil.isEmpty(latestThingData)) {
            return false;
        }

        // 属性是否重复
        List<ThingPropertyData> latestThingPropertyData = latestThingData.getProperties();
        for (ThingPropertyData propertyData : thingData.getProperties()) {
            if (!latestThingPropertyData.contains(propertyData)) {
                return false;
            }
        }

        return true;

        // TODO 事件是否重复

        // TODO 服务是否重复
    }

    @Override
    public ThingData createServiceData(ThingData thingData) {
        ThingData newThingData = thingDataRepository.insert(thingData);

        if (!ObjUtil.isNotNull(newThingData)) {
            log.info("服务调用数据未写入");
            return null;
        }

        return newThingData;
    }

    @Override
    public void updateServiceData(String thingId, ThingData thingData) {
        log.debug("更新服务数据[{}]", thingData);

        // 校验设备存在
        Thing thing = validateThingExists(thingId);
        // 校验产品存在且已发布
        validateProductExists(thing.getProduct().getId());

        thingDataRepository.save(thingData);
    }

    @Override
    public ThingData getThingDataByThingIdAndCommandId(String thingId, String commandId) {
        log.debug("设备[{}]，指令[{}]", thingId, commandId);

        // 校验设备存在
        Thing thing = validateThingExists(thingId);
        // 校验产品存在且已发布
        validateProductExists(thing.getProduct().getId());

        ThingData existsThingData = thingDataRepository.findByThingIdAndCommandId(thingId, commandId);

        if (!ObjUtil.isNotNull(existsThingData)) {
            log.info(THING_DATA_SERVICE_DATA_NOT_EXISTS.getMsg());
            throw exception(THING_DATA_SERVICE_DATA_NOT_EXISTS);
        }

        return existsThingData;
    }

//    @Override
//    public ThingData.Command listThingCommandData(String thingId, String status, Long startTime, Long endTime, Integer pageSize, Integer pageNum) {
//        Criteria baseCriteria = Criteria.where("thingId").is(thingId);
//
//        Criteria statusCriteria = StringUtils.hasText(status) ? Criteria.where("command.status").is(status) : new Criteria();
//
//        Criteria timeRangeCriteria = new Criteria();
//        if (startTime != null && endTime != null) {
//            timeRangeCriteria.and("timestamp").gte(startTime).lte(endTime);
//        } else if (startTime != null) {
//            timeRangeCriteria.and("timestamp").gte(startTime);
//        } else if (endTime != null) {
//            timeRangeCriteria.and("timestamp").lte(endTime);
//        }
//
//        Criteria finalCriteria = new Criteria().andOperator(baseCriteria, statusCriteria, timeRangeCriteria);
//
//        Aggregation aggregation = Aggregation.newAggregation(
//                Aggregation.match(finalCriteria),
//                Aggregation.sort(Sort.by(Sort.Order.desc("timestamp"))),
//                Aggregation.skip((long) (pageNum - 1) * pageSize),
//                Aggregation.limit(pageSize),
//                Aggregation.project()
//                        .and("command._id").as("commandId")
//                        .and("command.status").as("status")
//                        .and("command.commandType").as("commandType")
//                        .and("command.commandContent").as("commandContent")
//                        .and("command.outputParams").as("outputParams")
//                        .and("timestamp").as("timestamp")
//        );
//
//        AggregationResults<ThingData.CommandDTO> aggregationResults =
//                mongoTemplate.aggregate(aggregation, "thingData", ThingData.CommandDTO.class);
//
//        List<ThingData.CommandDTO> commandDataDTOs = aggregationResults.getMappedResults();
//
//        return ResponseDTOBuilder.build(commandDataDTOs, commandDataDTOs.size(), pageSize, pageNum);
//    }
//
//    @Override
//    public PaginationResponseDTO<ThingData.EventData> listThingEventsData(String thingId, String eventType, String identifier, Long startTime, Long endTime, Integer pageSize, Integer pageNum) {
//        ThingModel thingModel = getThingModelByThingId(thingId);
//
//        List<ThingModelEvent> eventsSpec = thingModel.getEvents();
//        if (eventsSpec == null || eventsSpec.isEmpty()) {
//            throw ServiceException.RESOURCE_NOT_FOUND;
//        }
//
//        // 遍历设备物模型的所有事件，每个事件读取最新值
//        List<com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData.EventData> eventsData = new ArrayList<>();
//        for (ThingModelEvent eventSpec : eventsSpec) {
//            String eventIdentifier = eventSpec.getIdentifier();
//            // 第一步：构建基本查询条件，根据thingId和事件标识符查询
//            Criteria baseCriteria = Criteria.where("thingId").is(thingId)
//                    .and("event.identifier").is(eventIdentifier);
//
//            Aggregation aggregation = Aggregation.newAggregation(
//                    Aggregation.match(baseCriteria),
//                    Aggregation.sort(Sort.by(Sort.Order.desc("event.timestamp"))),
//                    Aggregation.group("event.identifier")
//                            .first("event.identifier").as("identifier")
//                            .first("event.eventName").as("eventName")
//                            .first("event.eventType").as("eventType")
//                            .first("event.eventData").as("eventData")
//                            .first("event.required").as("required")
//                            .first("event.timestamp").as("timestamp")
//            );
//            try {
//                AggregationResults<com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData.EventData> aggregationResults = mongoTemplate.aggregate(
//                        aggregation, "thingData", com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData.EventData.class);
//
//                com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData.EventData data = aggregationResults.getUniqueMappedResult();
//
//                if (data != null) {
//                    eventsData.add(new com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData.EventData(
//                            data.getIdentifier(),
//                            eventSpec.getName(),
//                            eventSpec.getEventType().name(),
//                            data.getEventData(),
//                            eventSpec.getRequired(),
//                            data.getTimestamp()
//                    ));
//                } else {
//                    eventsData.add(new com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData.EventData(
//                            eventSpec.getIdentifier(),
//                            eventSpec.getName(),
//                            eventSpec.getEventType().name(),
//                            null,
//                            eventSpec.getRequired(),
//                            null
//                    ));
//                }
//            } catch (Exception e) {
//                log.warn("[db]聚合查询失败，原因：" + e.getMessage());
//                throw new ServiceException(ServiceException.ExceptionType.INTERNAL_FAILURE,
//                        ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_DATABASE, "聚合查询"));
//            }
//
//        }
//
//        List<ThingData.EventData> eventDataDTOs = ThingDataMapper.INSTANCE.toEventDTO(eventsData);
//
//        // 如果有事件类型，添加事件类型匹配规则
//        if (eventType != null && !eventType.isEmpty()) {
//            eventDataDTOs = eventDataDTOs.stream()
//                    .filter(eventDataDTO -> eventType.equals(eventDataDTO.getEventType()))
//                    .collect(Collectors.toList());
//        }
//
//        // 如果有标识符，添加模糊匹配规则
//        if (identifier != null && !identifier.isEmpty()) {
//            String lowerCaseIdentifier = identifier.toLowerCase();  // 转换为小写以进行不区分大小写的比较
//            eventDataDTOs = eventDataDTOs.stream()
//                    .filter(eventDataDTO -> eventDataDTO.getIdentifier().toLowerCase().contains(lowerCaseIdentifier))
//                    .collect(Collectors.toList());
//        }
//
//        // 进一步根据时间范围进行筛选
//        if (startTime != null && endTime != null) {
//            eventDataDTOs = eventDataDTOs.stream()
//                    .filter(serviceDataDTO -> serviceDataDTO.getTimestamp() != null && serviceDataDTO.getTimestamp() >= startTime && serviceDataDTO.getTimestamp() <= endTime)
//                    .collect(Collectors.toList());
//        } else if (startTime != null) {
//            eventDataDTOs = eventDataDTOs.stream()
//                    .filter(serviceDataDTO -> serviceDataDTO.getTimestamp() != null && serviceDataDTO.getTimestamp() >= startTime)
//                    .collect(Collectors.toList());
//        } else if (endTime != null) {
//            eventDataDTOs = eventDataDTOs.stream()
//                    .filter(serviceDataDTO -> serviceDataDTO.getTimestamp() != null && serviceDataDTO.getTimestamp() <= endTime)
//                    .collect(Collectors.toList());
//        }
//
//        int startIndex = (pageNum - 1) * pageSize;
//        int endIndex = Math.min(startIndex + pageSize, eventDataDTOs.size());
//        List<ThingData.EventData> paginatedList = eventDataDTOs.stream()
//                .skip(startIndex)
//                .limit(endIndex - startIndex)
//                .collect(Collectors.toList());
//
//        return ResponseDTOBuilder.build(paginatedList, eventDataDTOs.size(), pageSize, pageNum);
//    }

//    @Override
//    public ThingData getThingDataByCommandId(String thingId, String commandId) {
//        try {
//            com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData thingData = thingDataRepository.findByThingIdAndCommand_CommandId(thingId, commandId);
//            if (thingData != null) {
//                return thingData;
//            } else {
//                throw new ServiceException(
//                        ServiceException.ExceptionType.RESOURCE_NOT_FOUND,
//                        ExceptionInfoBuilder.build(ExceptionTemplate.RESOURCE_NOT_FOUND_COMMON, "设备数据")
//                );
//            }
//        } catch (DataAccessException e) {
//            log.error("[db]数据库异常，通过commandId查询thingData失败，原因：", e);
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INVALID_REQUEST,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_DATABASE, "数据库异常，通过commandId查询thingData失败")
//            );
//        }
//    }

    private Thing validateThingExists(String thingId) {
        Thing thing = thingService.getThing(thingId);
        if (ObjUtil.isNull(thing)) {
            throw exception(THING_DATA_THING_NOT_EXISTS);
        }

        return thing;
    }

    private void validateProductExists(String productId) {
        Product product = productService.getProduct(productId);
        if (ObjUtil.isNull(product)) {
            throw exception(THING_DATA_PRODUCT_NOT_EXISTS);
        }
        if (!product.getPublished()) {
            throw exception(THING_DATA_PRODUCT_UNAVAILABLE);
        }
    }
}
