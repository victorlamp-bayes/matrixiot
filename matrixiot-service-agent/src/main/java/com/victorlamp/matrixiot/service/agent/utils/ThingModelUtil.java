package com.victorlamp.matrixiot.service.agent.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingEventData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelEvent;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.AGENT_ILLEGAL_PROTOCOL_DATA;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.AGENT_ILLEGAL_TIME_FORMAT;

@Slf4j
public class ThingModelUtil {

    public static void validateThingData(ThingModel thingModel, String protocolData) {
        // 检查是否有效的JSON格式
        if (!JSON.isValid(protocolData)) {
            log.error("无效的协议数据:{}", protocolData);
            throw exception(AGENT_ILLEGAL_PROTOCOL_DATA);
        }

        // TODO 严格校验所有字段
//        // 检查是否与物模型匹配
//        JSONObject protocolDataJson = JSON.parseObject(protocolData);
//        JSONObject propertiesJson = null;
//        JSONObject eventsJson = null;
//        if (protocolDataJson.containsKey("properties") && protocolDataJson.containsKey("events")) {
//            propertiesJson = protocolDataJson.getJSONObject("properties");
//            eventsJson = protocolDataJson.getJSONObject("events");
//        } else {
//            propertiesJson = protocolDataJson;
//        }
//
//        // 校验属性是否与物模型匹配
//        Set<String> propertyIdentifierSet = thingModel.getProperties().stream().map(ThingModelProperty::getIdentifier).collect(Collectors.toSet());
//        for (String identifier : propertiesJson.keySet()) {
//            if (!propertyIdentifierSet.contains(identifier)) {
//                throw exception(AGENT_UNDEFINED_THING_MODEL_PROPERTY);
//            }
//        }
//
//        // 校验事件是否与物模型匹配
//        Set<String> eventIdentifierSet = thingModel.getEvents().stream().map(ThingModelEvent::getIdentifier).collect(Collectors.toSet());
//        for (String identifier : eventsJson.keySet()) {
//            if (!eventIdentifierSet.contains(identifier)) {
//                throw exception(AGENT_UNDEFINED_THING_MODEL_EVENT);
//            }
//        }
    }

    public static ThingData buildThingData(ThingModel thingModel, Thing thing, String protocolData, String originData) {
        JSONObject protocolDataJson = JSON.parseObject(protocolData);
        long timestamp = getTimestamp(protocolDataJson);

        ThingData thingData = ThingData.builder().productId(thingModel.getId()).thingId(thing.getId()).originData(originData).build();

        JSONObject propertiesJson = null;
        JSONObject eventsJson = null;
        if (protocolDataJson.containsKey("properties") && protocolDataJson.containsKey("events")) {
            propertiesJson = protocolDataJson.getJSONObject("properties");
            eventsJson = protocolDataJson.getJSONObject("events");
        } else {
            propertiesJson = protocolDataJson;
        }

        // 构造属性数据
        if (ObjUtil.isNotNull(propertiesJson)) {
            List<ThingPropertyData> propertiesData = buildPropertiesData(thingModel.getProperties(), propertiesJson, timestamp);
            thingData.setProperties(propertiesData);
        }
        // 构造事件数据
        if (ObjUtil.isNotNull(eventsJson)) {
            List<ThingEventData> eventsData = buildEventsData(thingModel.getEvents(), eventsJson, timestamp);
            thingData.setEvents(eventsData);
        }
        // TODO 构造服务数据

        log.debug("设备数据:[{}]", thingData);
        return thingData;
    }


    private static List<ThingPropertyData> buildPropertiesData(List<ThingModelProperty> propertiesSpec, JSONObject dataJson, long timestamp) {
        List<ThingPropertyData> propertiesData = new ArrayList<>();
        propertiesSpec.forEach(spec -> {
            String specIdentifier = spec.getIdentifier();
            if (dataJson.containsKey(specIdentifier)) {
                propertiesData.add(new ThingPropertyData(specIdentifier, dataJson.getString(specIdentifier), timestamp));
            }
        });
        return propertiesData;
    }

    private static List<ThingEventData> buildEventsData(List<ThingModelEvent> eventsSpec, JSONObject dataJson, long timestamp) {
        List<ThingEventData> eventsData = new ArrayList<>();
        eventsSpec.forEach(spec -> {
            String specIdentifier = spec.getIdentifier();
            if (dataJson.containsKey(specIdentifier)) {
                eventsData.add(new ThingEventData(specIdentifier, spec.getType().name(), dataJson.getString(specIdentifier), timestamp));
            }
        });
        return eventsData;
    }

    private static long getTimestamp(JSONObject jsonObject) {
        if (jsonObject.containsKey("collectTime")) {
            String dateStr = jsonObject.getString("collectTime");
            try {
                return DateUtil.parse(dateStr).getTime();
            } catch (Exception e) {
                log.error(AGENT_ILLEGAL_TIME_FORMAT.getMsg());
                throw exception(AGENT_ILLEGAL_TIME_FORMAT);
            }
        }

        if (jsonObject.containsKey("timestamp")) {
            return jsonObject.getLong("timestamp");
        }

        return DateUtil.date().getTime();
    }
}
