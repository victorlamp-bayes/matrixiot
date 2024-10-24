package com.victorlamp.matrixiot.service.system.framework.sms;

import com.victorlamp.matrixiot.service.framework.sms.core.client.SmsClientFactory;
import com.victorlamp.matrixiot.service.system.service.sms.SmsClientFactoryImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SmsCodeProperties.class)
public class SmsCodeConfiguration {

    @Bean
    public SmsClientFactory smsClientFactory() {
        return new SmsClientFactoryImpl();
    }
}
