package com.victorlamp.matrixiot.service.agent.utils.thirdPartyUtils;

import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpSendCenter {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String JSON = "application/json";
    private static final String Authorization = "Authorization";
    private static CloseableHttpClient httpClient;

    public void init() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .build();

        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public String post(String url, String jsonObject, String authorization) {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.setHeader(CONTENT_TYPE, JSON);
        httpPost.setHeader(Authorization, authorization);

        // 设置请求体
        HttpEntity requestEntity = new StringEntity(jsonObject, ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        try {
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        }catch (Exception e) {
            log.error("http request error::{}", e.getMessage());
            throw new ServiceException(
                    ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "请求发送失败"));
        }
    }
}
