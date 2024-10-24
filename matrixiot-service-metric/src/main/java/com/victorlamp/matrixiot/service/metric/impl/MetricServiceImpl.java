package com.victorlamp.matrixiot.service.metric.impl;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingModelService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelProperty;
import com.victorlamp.matrixiot.service.metric.MetricService;
import com.victorlamp.matrixiot.service.metric.aggregation.AggregationSchedulerService;
import com.victorlamp.matrixiot.service.metric.convert.MetricConvert;
import com.victorlamp.matrixiot.service.metric.dao.MetricRepository;
import com.victorlamp.matrixiot.service.metric.dao.SystemMetricDataRepository;
import com.victorlamp.matrixiot.service.metric.dto.MetricCreateReqDTO;
import com.victorlamp.matrixiot.service.metric.dto.MetricPageReqDTO;
import com.victorlamp.matrixiot.service.metric.dto.SystemMetricDataReqDTO;
import com.victorlamp.matrixiot.service.metric.entity.Metric;
import com.victorlamp.matrixiot.service.metric.entity.SystemMetricData;
import com.victorlamp.matrixiot.service.metric.enums.AggregationFreqEnum;
import com.victorlamp.matrixiot.service.metric.enums.AggregationTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.quartz.SchedulerException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.metric.constant.ErrorCodeConstants.*;

@DubboService(validation = "true")
@Service("metricService")
@Slf4j
public class MetricServiceImpl implements MetricService {
    public static final String METRIC_DATA = "metricData_";

    @Resource
    private AggregationSchedulerService aggregationSchedulerService;
    @Resource
    private MetricRepository metricRepository;
    @Resource
    private SystemMetricDataRepository systemMetricDataRepository;
    @DubboReference
    private ProductService productService;
    @DubboReference
    private ThingModelService thingModelService;
    @DubboReference
    private ThingService thingService;

    @Override
    public PageResult<Metric> listMetricPage(MetricPageReqDTO reqDTO) {
        // TODO 校验参数
        return metricRepository.findPage(reqDTO);
    }

    @Override
    @Transactional(transactionManager = "mongoTransactionManager", rollbackFor = Exception.class)
    public Metric createMetric(MetricCreateReqDTO reqDTO) {
        // 校验名称是否唯一
        validateMetricNameUnique(null, reqDTO.getName());
        // 校验产品是否存在
        validateProductExists(reqDTO.getProductId());
        // 校验属性标识符是否存在
        validatePropertyIdentifierExists(reqDTO.getProductId(), reqDTO.getPropertyIdentifier());
        // 校验设备是否存在
        validateThingExists(reqDTO.getThingId());
        // 校验聚合类型
        validateAggregationType(reqDTO.getAggregationType());
        // 校验聚合频率
        validateAggregationFreq(reqDTO.getAggregationFreq());

        Metric metric = metricRepository.insert(MetricConvert.INSTANCE.toEntity(reqDTO));

        // 查询并进行聚合计算  创建数据后立即响应客户端，而不必等待聚合计算完成
        asyncAggregateData(metric);

        return metric;
    }

    @Override
    public void deleteMetric(String id) {
        validateMetricExists(id);
        metricRepository.deleteById(id);
    }

    //    @Override
//    public ResponseDTO<MetricDataDTO> getLatestMetricData(String metricId) {
//        String collectionName = METRIC_DATA + metricId;
//
//        Query query = new Query();
//        query.with(Sort.by(Sort.Order.desc("createdAt"))); // 排序，按照 createdAt 字段降序排列
//        query.limit(1); // 获取最新的一条数据
//
//        MetricData metricData;
//        try {
//            metricData = mongoTemplate.findOne(query, MetricData.class, collectionName);
//        } catch (Exception e) {
//            log.error("获取最新聚合值失败，原因：" + e.getMessage());
//            throw new ServiceException(ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_DATABASE, "获取最新聚合值"));
//        }
//
//        MetricDataDTO metricDataDTO = CustomMetricMapper.INSTANCE.toDTO(metricData);
//        return ResponseDTOBuilder.build(metricDataDTO);
//    }
//

    @Override
    public List<SystemMetricData> listSystemMetricData(SystemMetricDataReqDTO reqDTO) {
        Long startTime = reqDTO.getStartTime();
        Long endTime = reqDTO.getEndTime();

        int count = Math.toIntExact((endTime - startTime) / 100); // 时间区间分钟数
        int interval = 1; // 默认时间间隔1分钟
        while (count / interval > 120) {
            interval++;
        }

        // 获取采样值
        List<SystemMetricData> list = systemMetricDataRepository.findByRange(startTime * 1000, endTime * 1000, interval);

//        List<SystemMetricData> list = CollUtil.newArrayList();
//        Date start = DateUtil.offsetMinute(DateUtil.date(), -5 * 100);
//        for (int i = 0; i < 100; i++) {
//            SystemMetricData data = new SystemMetricData();
//            data.setTimestamp(DateUtil.offsetMinute(start, i * 5).getTime());
//            data.setValue(RandomUtil.randomInt(20000, 40000));
//            list.add(data);
//        }
        return list;
    }

    @Async
    protected void asyncAggregateData(Metric metric) {
        // 开启任务
        try {
            aggregationSchedulerService.startAggregationTask(metric);
        } catch (SchedulerException e) {
            log.error("监控指标定时任务开启失败，原因：" + e.getMessage());
        }
    }

    private void validateMetricExists(String id) {
        Metric metric = metricRepository.findById(id).orElse(null);
        if (metric == null) {
            throw exception(METRIC_NOT_EXISTS);
        }
    }

    private void validateMetricNameUnique(String id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }

        Metric metric = metricRepository.findByName(name);
        if (metric == null) {
            return;
        }

        // 如果 id 为空，说明不用比较是否为相同 id 的度量指标
        if (id == null) {
            throw exception(METRIC_NAME_DUPLICATE);
        }
        if (!metric.getId().equals(id)) {
            throw exception(METRIC_NAME_DUPLICATE);
        }
    }

    private void validateProductExists(String productId) {
        Product product = productService.getProduct(productId);
        if (ObjUtil.isEmpty(product)) {
            throw exception(METRIC_PRODUCT_NOT_EXISTS);
        }
    }

    private void validatePropertyIdentifierExists(String productId, String identifier) {
        if (StrUtil.isBlank(identifier)) {
            return;
        }

        ThingModelProperty property = thingModelService.describeThingModelProperty(productId, identifier);
        if (property == null) {
            throw exception(METRIC_PROPERTY_IDENTIFIER_DUPLICATE);
        }
    }

    private void validateThingExists(String thingId) {
        // 未指定设备id，表示全部设备
        if (StrUtil.isEmpty(thingId)) {
            return;
        }

        Thing thing = thingService.getThing(thingId);
        if (ObjUtil.isEmpty(thing)) {
            throw exception(METRIC_THING_NOT_EXISTS);
        }
    }

    private void validateAggregationType(String aggregationType) {
        if (EnumUtil.contains(AggregationTypeEnum.class, aggregationType)) {
            return;
        }

        throw exception(METRIC_UNKNOWN_AGGREGATION_TYPE);
    }

    private void validateAggregationFreq(Integer aggregationFreq) {
        AggregationFreqEnum freqEnum = EnumUtil.getBy(AggregationFreqEnum.class, e -> StrUtil.equals(e.getId(), aggregationFreq.toString()));
        if (ObjUtil.isNull(freqEnum)) {
            throw exception(METRIC_UNKNOWN_AGGREGATION_FREQ);
        }
    }
}