package com.victorlamp.matrixiot.service.framework.apilog.config;

import com.victorlamp.matrixiot.common.enums.WebFilterOrderEnum;
import com.victorlamp.matrixiot.service.framework.apilog.core.filter.ApiAccessLogFilter;
import com.victorlamp.matrixiot.service.framework.apilog.core.service.ApiAccessLogFrameworkService;
import com.victorlamp.matrixiot.service.framework.apilog.core.service.ApiAccessLogFrameworkServiceImpl;
import com.victorlamp.matrixiot.service.framework.apilog.core.service.ApiErrorLogFrameworkService;
import com.victorlamp.matrixiot.service.framework.apilog.core.service.ApiErrorLogFrameworkServiceImpl;
import com.victorlamp.matrixiot.service.framework.web.config.WebAutoConfiguration;
import com.victorlamp.matrixiot.service.infra.service.logger.ApiAccessLogService;
import com.victorlamp.matrixiot.service.infra.service.logger.ApiErrorLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

@AutoConfiguration(after = WebAutoConfiguration.class)
public class ApiLogAutoConfiguration {

    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

    @Bean
    public ApiAccessLogFrameworkService apiAccessLogFrameworkService(ApiAccessLogService apiAccessLogService) {
        return new ApiAccessLogFrameworkServiceImpl(apiAccessLogService);
    }

    @Bean
    public ApiErrorLogFrameworkService apiErrorLogFrameworkService(ApiErrorLogService apiErrorLogService) {
        return new ApiErrorLogFrameworkServiceImpl(apiErrorLogService);
    }

    /**
     * 创建 ApiAccessLogFilter Bean，记录 API 请求日志
     */
    @Bean
    @ConditionalOnProperty(prefix = "matrixiot.access-log", value = "enable", matchIfMissing = true)
    // 允许使用 matrixiot.access-log.enable=false 禁用访问日志
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter(
            @Value("${spring.application.name}") String applicationName,
            ApiAccessLogFrameworkService apiAccessLogFrameworkService) {
        ApiAccessLogFilter filter = new ApiAccessLogFilter(applicationName, apiAccessLogFrameworkService);
        return createFilterBean(filter, WebFilterOrderEnum.API_ACCESS_LOG_FILTER);
    }

}
