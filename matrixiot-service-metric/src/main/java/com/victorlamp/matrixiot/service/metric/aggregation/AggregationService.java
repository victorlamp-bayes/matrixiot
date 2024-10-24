package com.victorlamp.matrixiot.service.metric.aggregation;

import com.alibaba.nacos.shaded.com.google.common.base.Strings;
import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.metric.entity.Metric;
import com.victorlamp.matrixiot.service.metric.entity.MetricData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.victorlamp.matrixiot.service.metric.impl.MetricServiceImpl.METRIC_DATA;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationService {
    private static final String LATEST = "LATEST";
    private static final String COUNT = "COUNT";
    private static final String SUM = "SUM";
    private static final String MIN = "MIN";
    private static final String MAX = "MAX";
    private static final String AVG = "AVG";
    private static final String INC = "INC";
    private final MongoTemplate mongoTemplate;

    /**
     * 查询并进行聚合计算，计算结果写入独立的表
     *
     * @param metric 监控指标数据
     */
    public void aggregateData(Metric metric) {
        String productId = metric.getProductId();
        String identifier = metric.getPropertyIdentifier();
        String thingId = metric.getThingId();
        Integer aggregationFreq = metric.getAggregationFreq();

        long now = (new Date()).getTime();
        long TimeRange = now - aggregationFreq * 60 * 1000;

        Criteria criteria = Criteria.where("productId").is(productId)
                .and("property.identifier").is(identifier)
                .and("property.timestamp").gte(TimeRange).lte(now);

        if (!Strings.isNullOrEmpty(thingId)) {
            criteria = criteria.and("thingId").is(thingId);
        }

        try {
            List<ThingData> thingData = mongoTemplate.find(Query.query(criteria), ThingData.class);
            // 该时间段没有符合的监控指标的数据，停止本次聚合计算
            if (thingData.isEmpty()) {
                return;
            }
        } catch (Exception e) {
            log.error("查询该时间段监控指标的数据，原因：" + e.getMessage());
            throw new ServiceException(ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_DATABASE, "监控指标数据查询"));
        }

        Aggregation aggregation = buildAggregation(metric, criteria);

        AggregationResults<MetricData> results;
        try {
            // 执行聚合查询
            results = mongoTemplate.aggregate(aggregation, "thingData", MetricData.class);
        } catch (Exception e) {
            log.error("执行聚合计算失败，原因：" + e.getMessage());
            throw new ServiceException(ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "监控指标执行聚合计算失败"));
        }

        String metricId = metric.getId();
        String newCollectionName = METRIC_DATA + metricId;
        if (!mongoTemplate.collectionExists(newCollectionName)) {
            mongoTemplate.createCollection(newCollectionName);
        }

        try {
            long timestamp = (new Date()).getTime();
            results.getMappedResults().forEach(result -> {
                result.setCreatedAt(timestamp);
                mongoTemplate.insert(results.getMappedResults(), newCollectionName);
            });
        } catch (Exception e) {
            log.error("聚合计算结果写入数据库失败，原因：" + e.getMessage());
            throw new ServiceException(ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_DATABASE, "监控指标聚合计算结果写入数据库"));
        }
    }

    // 添加动态字段并赋值
    private AddFieldsOperation createAddFieldsOperation(String aggregationType, Integer aggregationFreq) {
        return Aggregation.addFields()
                .addField("aggregationType").withValue(aggregationType)
                .addField("aggregationFreq").withValue(aggregationFreq)
                .build();
    }

    // 分组
    private GroupOperation createGroupOperation() {
        return Aggregation.group()
                .first("productId").as("productId")
                .first("thingId").as("thingId")
                .first("property.identifier").as("identifier")
                .first("aggregationType").as("aggregationType")
                .first("aggregationFreq").as("aggregationFreq");
    }

    private ProjectionOperation createProjectionOperation() {
        return Aggregation.project()
                .andInclude("productId")
                .andInclude("thingId")
                .andInclude("identifier")
                .andInclude("aggregationType")
                .andInclude("aggregationFreq");
    }

    private Aggregation buildAggregation(Metric metric, Criteria criteria) {

        AddFieldsOperation addField = createAddFieldsOperation(metric.getAggregationType(), metric.getAggregationFreq());
        GroupOperation group = createGroupOperation();
        ProjectionOperation project = createProjectionOperation();

        return switch (metric.getAggregationType()) {
            case LATEST -> buildLatestAggregation(criteria, addField, group);
            case COUNT -> buildCountAggregation(criteria, addField, group);
            case SUM -> buildSumAggregation(criteria, addField, group);
            case MIN -> buildMinAggregation(criteria, addField, group);
            case MAX -> buildMaxAggregation(criteria, addField, group);
            case AVG -> buildAvgAggregation(criteria, addField, group);
            case INC -> buildIncAggregation(criteria, addField, group, project);
            default -> {
                log.error("聚合方式无效， aggregation={}", metric.getAggregationType());
                throw new ServiceException(ServiceException.ExceptionType.INVALID_REQUEST,
                        ExceptionInfoBuilder.build(ExceptionTemplate.INVALID_REQUEST_INVALID_FORMAT, "监控指标 aggregation"));
            }
        };
    }

    private Aggregation buildLatestAggregation(Criteria criteria, AddFieldsOperation addField, GroupOperation group) {
        return Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "property.timestamp"),
                addField,
                group.first("property.value").as("value")
        );
    }

    private Aggregation buildCountAggregation(Criteria criteria, AddFieldsOperation addField, GroupOperation group) {
        return Aggregation.newAggregation(
                Aggregation.match(criteria),
                addField,
                group.count().as("value")
        );
    }

    private Aggregation buildSumAggregation(Criteria criteria, AddFieldsOperation addField, GroupOperation group) {
        return Aggregation.newAggregation(
                Aggregation.match(criteria),
                addField,
                group.sum(ConvertOperators.valueOf("property.value").convertToDouble()).as("value")
        );
    }

    private Aggregation buildMinAggregation(Criteria criteria, AddFieldsOperation addField, GroupOperation group) {
        return Aggregation.newAggregation(
                Aggregation.match(criteria),
                addField,
                group.min("property.value").as("value")
        );
    }

    private Aggregation buildMaxAggregation(Criteria criteria, AddFieldsOperation addField, GroupOperation group) {
        return Aggregation.newAggregation(
                Aggregation.match(criteria),
                addField,
                group.max("property.value").as("value")
        );
    }

    private Aggregation buildAvgAggregation(Criteria criteria, AddFieldsOperation addField, GroupOperation group) {
        return Aggregation.newAggregation(
                Aggregation.match(criteria),
                addField,
                group.avg(ConvertOperators.valueOf("property.value").convertToDouble()).as("value")
        );
    }

    private Aggregation buildIncAggregation(Criteria criteria, AddFieldsOperation addField, GroupOperation group, ProjectionOperation project) {
        return Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "property.timestamp"),
                addField,
                group.first(ConvertOperators.valueOf("property.value").convertToDouble()).as("firstValue")
                        .last(ConvertOperators.valueOf("property.value").convertToDouble()).as("lastValue"),
                project.andExpression("firstValue - lastValue").as("value")
        );
    }
}
