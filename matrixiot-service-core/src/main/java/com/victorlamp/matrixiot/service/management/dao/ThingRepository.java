package com.victorlamp.matrixiot.service.management.dao;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.dto.thing.SubThingPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thing.ThingPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThingRepository extends MongoRepository<Thing, String> {

    String THING_QUERY = """
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
                          { '$regexMatch': { input: '$description', regex: :#{#keywords}, options:'i' } }
                        ],
                      }
                    ]
                  }
                },
                { $expr: { $cond: [ { $in: [:#{#productId}, [null, '']] }, {}, { $eq: [{$toString: '$product.$id'}, :#{#productId}] } ] } },
                { $expr: { $cond: [ { $eq: [:#{#online}, null] }, {}, { $eq: ['$online', :#{#online}] } ] } },
                { $expr: { $cond: [ { $eq: [:#{#enabled}, null] }, {}, { $eq: ['$enabled', :#{#enabled}] } ] } }
              ],
              'deletedAt': null
            }
            """;

    String SUB_THING_PAGE_QUERY = """
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
                          { '$regexMatch': { input: '$description', regex: :#{#keywords}, options:'i' } }
                        ],
                      }
                    ]
                  }
                },
                { $expr: { $cond: [ { $in: [:#{#gatewayId}, [null, '']] }, {}, { $eq: ['$gatewayId', :#{#gatewayId}] } ] } },
                { $expr: { $cond: [ { $eq: [:#{#online}, null] }, {}, { $eq: ['$online', :#{#online}] } ] } },
                { $expr: { $cond: [ { $eq: [:#{#enabled}, null] }, {}, { $eq: ['$enabled', :#{#enabled}] } ] } }
              ],
              'deletedAt': null
            }
            """;

    @Override
    @NonNull
    @Query("{'id': :#{#id},'deletedAt': null}")
    Optional<Thing> findById(@NonNull String id);

    @Override
    default void deleteById(@NonNull String id) {
        Thing thing = findById(id).orElse(null);
        if (thing == null) {
            return;
        }
        thing.setDeletedAt(DateUtil.current());
        save(thing);
    }

    @Query("{'name' : :#{#name},'deletedAt': null}")
    @Aggregation
    Thing findByName(String name);

    @Query("{'gatewayId': :#{#gatewayId}, 'deletedAt': null}")
    List<Thing> findByGatewayId(String gatewayId);

    @Override
    @NonNull
    @Query("{'deletedAt': null}")
    List<Thing> findAll();

    @Query("{ 'product.id': :#{#productId}, 'deletedAt': null }")
    List<Thing> findAllByProductId(String productId);

    default PageResult<Thing> findPage(ThingPageReqDTO reqDTO) {
        PageRequest pageRequest = PageRequest.of(reqDTO.getPageNo() - 1, reqDTO.getPageSize());
        Page<Thing> thingPage = findPage(reqDTO.getKeywords(), reqDTO.getProductId(), reqDTO.getOnline(), reqDTO.getEnabled(), pageRequest);
        long total = countThing(reqDTO);
        return new PageResult<>(ListUtil.toList(thingPage.toList()), total, thingPage.getNumber() + 1, thingPage.getSize());
    }

    @Query(THING_QUERY)
    Page<Thing> findPage(String keywords, String productId, Boolean online, Boolean enabled, Pageable pageable);

    default long countThing(ThingPageReqDTO reqDTO) {
        return countThing(reqDTO.getKeywords(), reqDTO.getProductId(), reqDTO.getOnline(), reqDTO.getEnabled());
    }

    @CountQuery(THING_QUERY)
    long countThing(String keywords, String productId, Boolean online, Boolean enabled);

    default PageResult<Thing> findPageByGatewayId(SubThingPageReqDTO reqDTO) {
        PageRequest pageRequest = PageRequest.of(reqDTO.getPageNo() - 1, reqDTO.getPageSize());
        Page<Thing> subThingPage = findPageByGatewayId(reqDTO.getGatewayId(), reqDTO.getKeywords(), reqDTO.getOnline(), pageRequest);
        long total = countSubThing(reqDTO);
        return new PageResult<>(subThingPage.toList(), total, subThingPage.getNumber() + 1, subThingPage.getSize());
    }

    default long countSubThing(SubThingPageReqDTO reqDTO) {
        return countSubThing(reqDTO.getGatewayId(), reqDTO.getKeywords(), reqDTO.getOnline());
    }

    @Query(SUB_THING_PAGE_QUERY)
    Page<Thing> findPageByGatewayId(String gatewayId, String keywords, Boolean online, Pageable pageable);

    @CountQuery(SUB_THING_PAGE_QUERY)
    long countSubThing(String gatewayId, String keywords, Boolean online);

    @Query("{ 'product.id': :#{#productId}, 'deletedAt': null }")
    @Update("{ $set: {'gatewayId': :#{#gatewayId} } }")
    void updateGatewayIdByProductId(String gatewayId, String productId);

    @ExistsQuery("{ 'product.id': :#{#productId}, 'deletedAt': null }")
    boolean existsByProductId(String productId);

    @ExistsQuery("{ 'gatewayId': :#{#gatewayId}, 'id': { $ne: :#{#gatewayId} } , 'deletedAt': null }")
    boolean existsByGatewayId(String gatewayId);

    @Query("{'externalConfig.config.id': {'$regex': :#{#deviceId}, $options:'i' }, 'deletedAt': null}")
    Thing findByExternalConfigId(String deviceId);

    @Query("{'externalConfig.config.deviceNo': :#{#deviceNo}, 'deletedAt': null}")
    Thing findByExternalConfigDeviceNo(String deviceNo);

    @ExistsQuery("{'externalConfig.config.:#{#name}': {'$regex': :#{#value}, $options:'i' }, 'deletedAt': null}")
    boolean existsByExternalConfigItem(String name, Object value);

    @Query("{'position': {$exists: true}, $and: [{'position': {$geoWithin: { $polygon : ?0 }}}, {'deletedAt': null}]}")
    List<Thing> findByProvinceBoundary(double[][] provinceBoundary);

    @Query("{'position': {$exists: false}, 'deletedAt': null}")
    List<Thing> findByTenantId(); //后续加上租户Id拓展
}
