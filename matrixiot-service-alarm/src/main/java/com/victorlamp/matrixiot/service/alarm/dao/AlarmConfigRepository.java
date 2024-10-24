package com.victorlamp.matrixiot.service.alarm.dao;

import cn.hutool.core.date.DateUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.alarm.dto.alarmconfig.AlarmConfigPageReqDTO;
import com.victorlamp.matrixiot.service.alarm.entity.AlarmConfig;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AlarmConfigRepository extends MongoRepository<AlarmConfig, String> {

    String ALARM_CONFIG_QUERY = """
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
                          { '$regexMatch': { input: '$productName', regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: '$describe', regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: { $reduce: { input: '$contacts.person', initialValue: '', in: { $concat: ['$$value', ' ', '$$this'] } } }, regex: :#{#keywords}, options:'i' } }
                          { '$regexMatch': { input: { $reduce: { input: '$contacts.phone', initialValue: '', in: { $concat: ['$$value', ' ', '$$this'] } } }, regex: :#{#keywords}, options:'i' } }
                        ],
                      }
                    ]
                  }
                },
                { $expr: { $cond: [ { $in: [:#{#level}, [null, '']] }, {}, { $eq: ['$level', :#{#level}] } ] } }
              ],
              'deletedAt': null
            }
            """;

    default PageResult<AlarmConfig> findPage(AlarmConfigPageReqDTO reqDTO) {
        PageRequest pageRequest = PageRequest.of(reqDTO.getPageNo() - 1, reqDTO.getPageSize());
        Page<AlarmConfig> newAlarmPage = findPage(reqDTO.getKeywords(), reqDTO.getLevel(), pageRequest);
        long total = count(reqDTO);
        return new PageResult<>(newAlarmPage.toList(), total, newAlarmPage.getNumber() + 1, newAlarmPage.getSize());
    }

    @Query(ALARM_CONFIG_QUERY)
    Page<AlarmConfig> findPage(String keywords, String level, Pageable pageable);

    default long count(AlarmConfigPageReqDTO reqDTO) {
        return count(reqDTO.getKeywords(), reqDTO.getLevel());
    }

    @CountQuery(ALARM_CONFIG_QUERY)
    long count(String keywords, String level);

    @Override
    default void deleteById(@NonNull String id) {
        AlarmConfig alarmConfig = findById(id).orElse(null);
        if (alarmConfig == null) {
            return;
        }
        alarmConfig.setDeletedAt(DateUtil.current());
        save(alarmConfig);
    }

    @Query("{ 'productId': :#{#productId}, 'deletedAt': null }")
    AlarmConfig findByProductId(@NonNull String productId);

}
