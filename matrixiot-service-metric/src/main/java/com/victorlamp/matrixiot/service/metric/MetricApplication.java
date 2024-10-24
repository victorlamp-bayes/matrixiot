package com.victorlamp.matrixiot.service.metric;

import com.victorlamp.matrixiot.configuration.config.MongoAuditingConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@OpenAPIDefinition(info = @Info(title = "Metric API"))
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubbo
@Import(MongoAuditingConfig.class)
@EnableScheduling
@ComponentScan("com.victorlamp.matrixiot.service")
@EnableTransactionManagement
public class MetricApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetricApplication.class, args);
    }
}