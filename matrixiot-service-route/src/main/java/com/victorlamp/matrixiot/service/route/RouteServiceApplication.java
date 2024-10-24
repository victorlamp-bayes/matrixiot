package com.victorlamp.matrixiot.service.route;

import com.victorlamp.matrixiot.configuration.config.MongoAuditingConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@OpenAPIDefinition(info = @Info(title = "DataRoute API"))
@EnableDubbo
@Import(MongoAuditingConfig.class)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan("com.victorlamp.matrixiot.service")
@EnableTransactionManagement
public class RouteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RouteServiceApplication.class, args);
    }

}
