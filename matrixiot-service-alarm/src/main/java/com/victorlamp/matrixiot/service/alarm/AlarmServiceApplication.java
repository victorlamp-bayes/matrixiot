package com.victorlamp.matrixiot.service.alarm;

import com.victorlamp.matrixiot.configuration.config.MongoAuditingConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@OpenAPIDefinition(
        info = @Info(title = "告警服务",
                version = "1.0.0",
                description = "记录场景联动中动作执行的告警信息，暂不支持新增接口，提供查询和删除接口",
                contact = @Contact(name = "孙林", email = "sunlin@hxiswater.com")))
@EnableDubbo
@Import(MongoAuditingConfig.class)
@ComponentScan("com.victorlamp.matrixiot.service")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties
@EnableTransactionManagement
public class AlarmServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlarmServiceApplication.class, args);
    }
}
