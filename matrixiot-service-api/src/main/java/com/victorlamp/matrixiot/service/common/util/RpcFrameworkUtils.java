package com.victorlamp.matrixiot.service.common.util;

import com.victorlamp.matrixiot.service.system.authorization.LoginUser;
import org.apache.dubbo.rpc.RpcContext;

import static com.victorlamp.matrixiot.service.system.authorization.enums.LoginUserConstants.LOGIN_USER_ATTR;

public class RpcFrameworkUtils {

    public static LoginUser getLoginUser() {
        return (LoginUser) RpcContext.getServerAttachment().getObjectAttachment(LOGIN_USER_ATTR);
    }

    public static void setLoginUser(LoginUser loginUser) {
        RpcContext.getClientAttachment().setObjectAttachment(LOGIN_USER_ATTR, loginUser);
    }
}
