package com.victorlamp.matrixiot.service.metric.dao;

import com.victorlamp.matrixiot.service.metric.entity.SystemMetricData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemMetricDataRepository extends MongoRepository<SystemMetricData, String> {

    String QUERY = """
            {
              $and: [
                { $expr: { $gte: [ '$timestamp', :#{#startTime} ] } },
                { $expr: { $lte: [ '$timestamp', :#{#endTime} ] } },
                { $expr: { $eq: [ { $mod: [ { $minute: { $toDate: '$timestamp' } }, :#{#interval} ] }, { $mod: [ { $minute: { $toDate: :#{#endTime} } }, :#{#interval} ] } ] } }
              ],
              'deletedAt': null
            }
            """;

    @Query(QUERY)
    List<SystemMetricData> findByRange(long startTime, long endTime, int interval);
}
