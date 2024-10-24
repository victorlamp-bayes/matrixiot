package com.victorlamp.matrixiot.configuration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * 其余模块 Import导入 使用同一个Mongo审计类
 * */
@Configuration
@EnableMongoAuditing
public class MongoAuditingConfig {
}
