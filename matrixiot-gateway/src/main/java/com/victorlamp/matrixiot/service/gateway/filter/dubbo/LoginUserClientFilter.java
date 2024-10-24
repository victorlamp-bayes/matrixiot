package com.victorlamp.matrixiot.service.gateway.filter.dubbo;

import com.victorlamp.matrixiot.service.common.util.RpcFrameworkUtils;
import com.victorlamp.matrixiot.service.gateway.filter.context.ReactiveRequestContextHolder;
import com.victorlamp.matrixiot.service.gateway.util.SecurityFrameworkUtils;
import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * Dubbo消费者过滤器，将当前登录用户信息添加到ClientAttachment
 */
@Activate(group = CommonConstants.CONSUMER)
public class LoginUserClientFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        return ReactiveRequestContextHolder.getExchange().map(exchange -> {
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser(exchange);
            RpcFrameworkUtils.setLoginUser(loginUser);
            return invoker.invoke(invocation);
        }).block();
//        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser(exchange);
//        RpcFrameworkUtils.setLoginUser(loginUser);
//        return invoker.invoke(invocation);
    }
}
