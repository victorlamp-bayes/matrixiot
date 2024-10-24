package com.victorlamp.matrixiot.service.management.dao;

import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalConfigTemplateRepository extends MongoRepository<ExternalConfigTemplate, String> {

    ExternalConfigTemplate findByType(String type);
}
