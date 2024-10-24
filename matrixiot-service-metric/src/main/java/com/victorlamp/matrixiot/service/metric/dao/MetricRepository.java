package com.victorlamp.matrixiot.service.metric.dao;

import cn.hutool.core.date.DateUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.metric.dto.MetricPageReqDTO;
import com.victorlamp.matrixiot.service.metric.entity.Metric;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetricRepository extends MongoRepository<Metric, String> {

    String METRIC_QUERY = """
            {
              $and: [
                {
                  $expr: {
                    $cond: [
                      { $in: [:#{#keywords}, [null, '']] },
                      {},
                      {
                        $or: [
                          { '$regexMatch': { input: {$toString: '$_id'}, regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: '$name', regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: '$description', regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: '$propertyIdentifier', regex: :#{#keywords}, options:'i' } }
                        ],
                      }
                    ]
                  }
                },
                { $expr: { $cond: [ { $in: [:#{#productId}, [null, '']] }, {}, { $eq: ['$productId', :#{#productId}] } ] } },
                { $expr: { $cond: [ { $in: [:#{#thingId}, [null, '']] }, {}, { $eq: ['$thingId', :#{#thingId}] } ] } },
                { $expr: { $cond: [ { $in: [:#{#status}, [null, '']] }, {}, { $eq: ['$status', :#{#status}] } ] } }
              ],
              'deletedAt': null
            }
            """;

    @Override
    @NonNull
    @Query("{'id': :#{#id}, 'deletedAt': null}")
    Optional<Metric> findById(@NonNull String id);

    @Query("{'name' : :#{#name},'deletedAt': null}")
    Metric findByName(@NonNull String name);

    @Query("{'identifier' : :#{#identifier},'deletedAt': null}")
    Metric findByIdentifier(@NonNull String identifier);

    default PageResult<Metric> findPage(MetricPageReqDTO reqDTO) {
        PageRequest pageRequest = PageRequest.of(reqDTO.getPageNo() - 1, reqDTO.getPageSize());
        Page<Metric> page = findPage(reqDTO.getKeywords(), reqDTO.getProductId(), reqDTO.getThingId(), reqDTO.getStatus(), pageRequest);
        long total = count(reqDTO);
        return new PageResult<>(page.toList(), total, page.getNumber() + 1, page.getSize());
    }

    @Query(METRIC_QUERY)
    Page<Metric> findPage(String keywords, String productId, String thingId, String status, Pageable pageable);

    default long count(MetricPageReqDTO reqDTO) {
        return count(reqDTO.getKeywords(), reqDTO.getProductId(), reqDTO.getThingId(), reqDTO.getStatus());
    }

    @CountQuery(METRIC_QUERY)
    long count(String keywords, String productId, String thingId, String status);

    @Override
    default void deleteById(@NonNull String id) {
        Metric metric = findById(id).orElse(null);
        if (metric == null) {
            return;
        }
        metric.setDeletedAt(DateUtil.current());
        save(metric);
    }
}