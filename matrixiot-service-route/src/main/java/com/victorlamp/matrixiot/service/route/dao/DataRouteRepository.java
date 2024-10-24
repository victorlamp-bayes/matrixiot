package com.victorlamp.matrixiot.service.route.dao;

import cn.hutool.core.date.DateUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.route.dto.DataRoutePageReqDTO;
import com.victorlamp.matrixiot.service.route.entity.DataRoute;
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
public interface DataRouteRepository extends MongoRepository<DataRoute, String> {

    String DATA_ROUTE_QUERY = """
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
                      { '$regexMatch': { input: '$status', regex: :#{#keywords}, options:'i' } },
                    ],
                  }
                ]
              },
              'deletedAt': null
            }
            """;

    @Query("{ 'name': :#{#name}, 'deletedAt': null }")
    DataRoute findByName(String name);

    @Override
    @NonNull
    @Query("{ 'id': :#{#id}, 'deletedAt': null }")
    Optional<DataRoute> findById(@NonNull String id);

    @Override
    default void deleteById(@NonNull String id) {
        DataRoute dataRoute = findById(id).orElse(null);
        if (dataRoute == null) {
            return;
        }
        dataRoute.setDeletedAt(DateUtil.current());
        save(dataRoute);
    }

    @Query("{ 'status': :#{#status}, 'deletedAt': null }")
    List<DataRoute> findAllByStatus(String status);

    @Override
    @NonNull
    @Query("{ 'deletedAt': null }")
    List<DataRoute> findAll();

    default PageResult<DataRoute> findPage(DataRoutePageReqDTO reqDTO) {
        PageRequest pageRequest = PageRequest.of(reqDTO.getPageNo() - 1, reqDTO.getPageSize());
        Page<DataRoute> dataRoutePage = findPage(reqDTO.getKeywords(), pageRequest);
        long total = count(reqDTO);
        return new PageResult<>(dataRoutePage.toList(), total, dataRoutePage.getNumber() + 1, dataRoutePage.getSize());
    }

    @Query(DATA_ROUTE_QUERY)
    Page<DataRoute> findPage(String keywords, Pageable pageable);

    default long count(DataRoutePageReqDTO reqDTO) {
        return count(reqDTO.getKeywords());
    }

    @CountQuery(DATA_ROUTE_QUERY)
    long count(String keywords);
}
