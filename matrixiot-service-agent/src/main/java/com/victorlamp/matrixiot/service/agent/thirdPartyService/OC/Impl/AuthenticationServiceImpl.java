package com.victorlamp.matrixiot.service.agent.thirdPartyService.OC.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl {
//    private final HttpsUtil httpsUtil;
//
//    @Override
//    public String getAccessToken(String appId, String appSecret, String appAuthUrl) {
//        StreamClosedHttpResponse response;
//        try{
//            Map<String, String> param = new HashMap<>();
//            param.put("appId", appId);
//            param.put("secret", appSecret);
//            log.info("开始获取accessToken");
//            response = httpsUtil.doPostFormUrlEncodedGetStatusLine(appAuthUrl, param);
//
//        }catch(Exception e){
//            log.error("获取accessToken失败：" + e.getMessage());
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "获取accessToken失败"));
//        }
//
//        if (response != null) {
//            JSONObject data = JSON.parseObject(response.getContent());
//            log.info("响应结果，accessToken：{}", data.getString("accessToken"));
//            return data.getString("accessToken");
//        } else {
//            log.info("获取accessToken为空");
//            throw new ServiceException(
//                    ServiceException.ExceptionType.INTERNAL_FAILURE,
//                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "获取accessToken为空"));
//        }
//
//    }
//
//    @PostConstruct
//    public void afterPropertiesSet() throws Exception {
//        // TODO 测试环境跳过证书验证，生成环境配置后更改为证书验证
//        httpsUtil.init();
////        httpsUtil.initSSLConfigForTwoWay();
//    }
}
