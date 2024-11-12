package com.victorlamp.matrixiot.server;

import com.victorlamp.matrixiot.configuration.config.MongoAuditingConfig;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableDubbo
@EnableTransactionManagement
@EnableConfigurationProperties
@Import({MongoAuditingConfig.class})
@ComponentScan(basePackages = {"com.victorlamp.matrixiot.service", "com.victorlamp.matrixiot.configuration"})
public class MatrixIoTServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MatrixIoTServerApplication.class, args);
    }
}
