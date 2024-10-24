package com.victorlamp.matrixiot.service.agent;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@OpenAPIDefinition(info = @Info(title = "ThingAgent API"))
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableDubbo
@EnableScheduling
@ComponentScan("com.victorlamp.matrixiot.service")
@EnableTransactionManagement
@Import(cn.hutool.extra.spring.SpringUtil.class)
public class ThingAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThingAgentApplication.class, args);
    }
}
