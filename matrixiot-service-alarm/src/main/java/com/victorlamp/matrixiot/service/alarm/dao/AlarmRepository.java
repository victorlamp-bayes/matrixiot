package com.victorlamp.matrixiot.service.alarm.dao;

import cn.hutool.core.date.DateUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.alarm.dto.alarm.AlarmPageReqDTO;
import com.victorlamp.matrixiot.service.alarm.entity.Alarm;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends MongoRepository<Alarm, String> {

    String ALARM_QUERY = """
            {
              $and: [
                { 'productId': { $regex: ?#{#productId != null ? #productId : ''} } },
                { 'thingId': { $regex: ?#{#thingId != null ? #thingId : ''} } },
                { $expr: { $cond: [ { $in: [:#{#status}, [null, '']] }, {}, { $eq: ['$status', :#{#status}] } ] } },
                { $expr: { $cond: [ { $in: [:#{#level}, [null, '']] }, {}, { $eq: ['$level', :#{#level}] } ] } },
                { $expr: { $cond: [ { $in: [:#{#scene}, [null, '']] }, {}, { $eq: ['$scene', :#{#scene}] } ] } },
                { $expr: { $cond: [ { $in: [:#{#sendStatus}, [null, '']] }, {}, { $eq: ['$sendStatus', :#{#sendStatus}] } ] } },
                { $expr: { $cond: [ { $in: [:#{#timeRange}, [null, '']] }, {}, { $and: [ {$gte: ['$timestamp', :#{#timeRange?.get(0)}]}, {$lte: ['$timestamp', :#{#timeRange?.get(1)}]} ] } ] } },
                { 'deletedAt': null }
              ]
            }
            """;

    default PageResult<Alarm> findPage(AlarmPageReqDTO reqDTO) {
        PageRequest pageRequest = PageRequest.of(reqDTO.getPageNo() - 1, reqDTO.getPageSize());
        Page<Alarm> alarmPage = findPage(reqDTO.getProductId(), reqDTO.getThingId(), reqDTO.getStatus(), reqDTO.getLevel(), reqDTO.getScene(), reqDTO.getSendStatus(), reqDTO.getTimeRange(), pageRequest);
        long total = count(reqDTO);
        return new PageResult<>(alarmPage.toList(), total, alarmPage.getNumber() + 1, alarmPage.getSize());
    }

    @Query(ALARM_QUERY)
    Page<Alarm> findPage(String productId, String thingId, Boolean status, String level, String scene, String sendStatus, List<Long> timeRange, Pageable pageable);

    default long count(AlarmPageReqDTO reqDTO) {
        return count(reqDTO.getProductId(), reqDTO.getThingId(), reqDTO.getStatus(), reqDTO.getLevel(), reqDTO.getScene(), reqDTO.getSendStatus(), reqDTO.getTimeRange());
    }

    @CountQuery(ALARM_QUERY)
    long count(String productId, String thingId, Boolean status, String level, String scene, String sendStatus, List<Long> timeRange);

    @Override
    @NonNull
    @Query("{'id': :#{#id},'deletedAt': null}")
    Optional<Alarm> findById(@NonNull String id);

    @Override
    default void deleteById(@NonNull String id) {
        Alarm alarm = findById(id).orElse(null);
        if (alarm == null) {
            return;
        }
        alarm.setDeletedAt(DateUtil.current());
        save(alarm);
    }

}
