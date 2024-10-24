package com.victorlamp.matrixiot.service.management.dao;

import cn.hutool.core.date.DateUtil;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModel;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThingModelRepository extends MongoRepository<ThingModel, String> {
    @Override
    @NonNull
    @Query("{'id': :#{#id}, 'deletedAt': null}")
    Optional<ThingModel> findById(@NonNull String id);

    @Override
    default void deleteById(@NonNull String id) {
        ThingModel entity = findById(id).orElse(null);
        if (entity == null) {
            return;
        }
        entity.setDeletedAt(DateUtil.current());
        save(entity);
    }

    @Query("{ '_id': :#{#id}, 'properties.identifier': :#{#identifier}, 'deletedAt': null }," +
            "{ '$elemMatch': { 'properties.identifier': :#{#identifier} } }")
    ThingModel findByIdAndPropertyIdentifier(String id, String identifier);

    @Query("{ 'id': :#{#id}, 'properties.name': :#{#name}, 'deletedAt': null }")
    ThingModel findByIdAndPropertyName(String id, String name);

//    @Aggregation({
//            "{ $unwind: '$properties' }",
//            "{ $match: { 'id': :#{#id}, 'properties.identifier': :#{#identifier}, 'deletedAt': null } }",
//            "{ $project: { 'properties': 1 } }"
//    })
//    ThingModelProperty findByIdAndPropertyIdentifier(String id, String identifier);
//
//    @Aggregation({
//            "{ $unwind: '$properties' }",
//            "{ $match: { 'id': :#{#id}, 'properties.name': :#{#name}, 'deletedAt': null } }",
//            "{ $project: { 'properties': 1 } }"
//    })
//    ThingModelProperty findByIdAndPropertyName(String id, String name);
//
//    @Aggregation({
//            "{ $unwind: '$events' }",
//            "{ $match: { 'id': :#{#id}, 'events.name': :#{#name}, 'deletedAt': null } }",
//            "{ $project: { 'events': 1 } }"
//    })
//    ThingModelEvent findByIdAndEventName(String id, String name);

}
