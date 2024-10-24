package com.victorlamp.matrixiot.service.management.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingEventDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingPropertyDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingServiceDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ThingDataRepository extends MongoRepository<ThingData, String> {
    ThingData findFirstByThingIdOrderByTimestampDesc(String thingId);

    @Query("{ 'thingId': :#{#thingId}, 'service.commandId': :#{#commandId} }")
    ThingData findByThingIdAndCommandId(String thingId, String commandId);

    default ThingData findLatestThingData(String thingId) {
        return findFirstByThingIdOrderByTimestampDesc(thingId);
    }

    // 自定义接口
    default ThingPropertyData findLatestPropertyData(String thingId, String identifier) {
        ThingData thingData = findFirstByThingIdAndPropertiesIdentifierOrderByTimestampDesc(thingId, identifier);
        if (thingData == null) {
            return null;
        }
        return thingData.getProperties().get(0);
    }


    default PageResult<ThingData> findPage(ThingDataPageReqDTO reqDTO) {
        PageRequest pageRequest = PageRequest.of(reqDTO.getPageNo() - 1, reqDTO.getPageSize());
        Page<ThingData> thingDataPage = findAllByThingIdOrderByTimestampDesc(reqDTO.getThingId(), pageRequest);
        long total = count(reqDTO.getThingId());
        return new PageResult<>(thingDataPage.toList(), total, thingDataPage.getNumber() + 1, thingDataPage.getSize());
    }

    Page<ThingData> findAllByThingIdOrderByTimestampDesc(String thingId, Pageable pageable);

    @CountQuery("{ 'thingId': :#{#thingId} }")
    long count(String thingId);

    /**
     * 查询设备Property列表
     */
    default PageResult<ThingData> findPage(ThingPropertyDataPageReqDTO reqDTO) {
        List<ThingData> thingDataPage = findThingPropertyDataPage(reqDTO.getThingId(), (reqDTO.getPageNo() - 1) * reqDTO.getPageSize(), reqDTO.getPageSize());
        long total = countProperties(reqDTO.getThingId());
        return new PageResult<>(thingDataPage, total, reqDTO.getPageNo(), reqDTO.getPageSize());
    }

    @Aggregation({
            "{ $project: { 'events': 0, 'services': 0 } }",
            "{ $match: { 'thingId': :#{#thingId}, 'properties': { $nin: [null, []] } } }",
            "{ $sort: { 'timestamp': -1 } }",
            "{ $skip: :#{#skip} }",
            "{ $limit: :#{#limit} }"
    })
    List<ThingData> findThingPropertyDataPage(String thingId, int skip, int limit);

    @CountQuery("{ 'thingId': :#{#thingId}, 'properties': { $nin: [null, []] } }")
    long countProperties(String thingId);

    /**
     * 查询设备Event列表
     */
    default PageResult<ThingData> findPage(ThingEventDataPageReqDTO reqDTO) {
        List<ThingData> thingDataPage = findThingEventDataPage(reqDTO.getThingId(), (reqDTO.getPageNo() - 1) * reqDTO.getPageSize(), reqDTO.getPageSize());
        long total = countEvents(reqDTO.getThingId());
        return new PageResult<>(thingDataPage, total, reqDTO.getPageNo(), reqDTO.getPageSize());
    }

    @Aggregation({
            "{ $project: { 'properties': 0, 'services': 0 } }",
            "{ $match: { 'thingId': :#{#thingId}, 'events': { $nin: [null, []] } } }",
            "{ $sort: { 'timestamp': -1 } }",
            "{ $skip: :#{#skip} }",
            "{ $limit: :#{#limit} }"
    })
    List<ThingData> findThingEventDataPage(String thingId, int skip, int limit);

    @CountQuery("{ 'thingId': :#{#thingId}, 'events': { $nin: [null, []] } }")
    long countEvents(String thingId);

    default List<ThingPropertyData> findLatestPropertyData(String thingId, List<String> identifiers) {
        if (CollUtil.isEmpty(identifiers)) {
            return ListUtil.empty();
        }

        // 如果identifier数量为1，直接匹配查询
        if (CollUtil.size(identifiers) == 1) {
            return ListUtil.toList(this.findLatestPropertyData(thingId, identifiers.get(0)));
        }

        // 获取最近的一条属性数据集
        ThingData thingData = findFirstByThingIdAndPropertiesIsNotNullOrderByTimestampDesc(thingId);
        if (thingData == null || CollUtil.isEmpty(thingData.getProperties())) {
            return ListUtil.empty();
        }
        List<ThingPropertyData> firstPropertiesData = thingData.getProperties();

        // 属性数据与提供的identifier数量一致，直接返回结果
        if (firstPropertiesData.size() == identifiers.size()) {
            return thingData.getProperties();
        }

        List<ThingPropertyData> propertiesData = new ArrayList<>(firstPropertiesData);

        // 遍历未获取到数据的identifier
        List<String> usedIdentifiers = firstPropertiesData.stream().map(ThingPropertyData::getIdentifier).toList();
        List<String> unusedIdentifiers = identifiers.stream().filter(identifier -> !CollUtil.contains(usedIdentifiers, identifier)).toList();
        unusedIdentifiers.forEach(identifier -> propertiesData.add(this.findLatestPropertyData(thingId, identifier)));

        return propertiesData;
    }

    /**
     * 查询设备Service
     */
    default PageResult<ThingData> findPage(ThingServiceDataPageReqDTO reqDTO) {
        List<ThingData> thingDataPage = findThingServiceDataPage(reqDTO.getThingId(), (reqDTO.getPageNo() - 1) * reqDTO.getPageSize(), reqDTO.getPageSize());
        long total = countServices(reqDTO.getThingId());
        return new PageResult<>(thingDataPage, total, reqDTO.getPageNo(), reqDTO.getPageSize());
    }

    @Aggregation({
            "{ $project: { 'properties': 0, 'events': 0 } }",
            "{ $match: { 'thingId': :#{#thingId}, 'service': { $ne: null } } }",
            "{ $sort: { 'timestamp': -1 } }",
            "{ $skip: :#{#skip} }",
            "{ $limit: :#{#limit} }"
    })
    List<ThingData> findThingServiceDataPage(String thingId, int skip, int limit);

    @CountQuery("{ 'thingId': :#{#thingId}, 'service': { $ne: null } }")
    long countServices(String thingId);

    ThingData findFirstByThingIdAndPropertiesIsNotNullOrderByTimestampDesc(String thingId);

    ThingData findFirstByThingIdAndPropertiesIdentifierOrderByTimestampDesc(String thingId, String identifier);
}
