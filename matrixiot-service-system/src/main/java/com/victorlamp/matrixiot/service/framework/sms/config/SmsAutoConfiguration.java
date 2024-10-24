package com.victorlamp.matrixiot.service.framework.sms.config;

import com.victorlamp.matrixiot.service.framework.sms.core.client.SmsClientFactory;
import com.victorlamp.matrixiot.service.system.service.sms.SmsClientFactoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 短信配置类
 *
 * @author 芋道源码
 */
@AutoConfiguration
public class SmsAutoConfiguration {

    @Bean
    public SmsClientFactory smsClientFactory() {
        return new SmsClientFactoryImpl();
    }

}
