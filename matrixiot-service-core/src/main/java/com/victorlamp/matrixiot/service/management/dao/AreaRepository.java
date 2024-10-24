package com.victorlamp.matrixiot.service.management.dao;


import com.victorlamp.matrixiot.service.management.service.area.Area;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends MongoRepository<Area, String> {
    Area getAreaByCode(int code);
}
