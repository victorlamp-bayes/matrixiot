package com.victorlamp.matrixiot.service.framework.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "matrixiot.web")
@Validated
@Data
public class WebProperties {
}
