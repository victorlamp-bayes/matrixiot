package com.victorlamp.matrixiot.service.alarm.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@RefreshScope
@ConfigurationProperties("alarm.email")
public class AlarmEmailProperty {

    /**
     * 邮件服务器的SMTP地址
     */
    private String host;

    /**
     * 邮件服务器的SMTP端口，可选，默认 465
     */
    private int port = 465;

    /**
     * 发件人邮箱
     */
    private String from;

    /**
     * 用户名，不填则默认为邮箱前缀
     */
    private String user;

    /**
     * 邮箱密码（注意，某些邮箱需要为SMTP服务单独设置授权码，详情查看相关帮助）
     */
    private String pass;

    /**
     * 控制 {@link com.victorlamp.matrixiot.service.alarm.controller.AlarmController#email()) 是否可以测试
     */
    private Boolean testApiEnabled;
}
