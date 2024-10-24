package com.victorlamp.matrixiot.service.alarm.utils;

import cn.hutool.extra.mail.MailAccount;
import com.victorlamp.matrixiot.service.alarm.config.AlarmEmailProperty;
import com.victorlamp.matrixiot.service.alarm.entity.Alarm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class MsgUtil {
    @Autowired
    private AlarmEmailProperty alarmEmailProperty;

    private MailAccount mailAccount() {

        MailAccount mailAccount = new MailAccount();

        mailAccount.setHost(alarmEmailProperty.getHost());
        mailAccount.setPort(25);
        mailAccount.setAuth(true);
        mailAccount.setFrom(alarmEmailProperty.getFrom());
        mailAccount.setPass(alarmEmailProperty.getPass());

        if (StringUtils.hasText(alarmEmailProperty.getUser())) {

            mailAccount.setUser(alarmEmailProperty.getUser());
        }

        return mailAccount;
    }

    /**
     * 发送告警信息
     */
//    public boolean sendMessage(Alarm alarm) throws Exception {
//        //告警信息联系方式
//        List<ContactInfo> contacts = alarm.getContacts();
//
//        // 遍历联系方式列表
//        for (ContactInfo contact : contacts) {
//            if (contact.getPhone() != null) {
//                log.info("告警Id=[{}]，发送渠道：{}，消息发送目的地：{}；", alarm.getId(), "电话", contact.getPhone());
//                sendSMS(alarm);
//            }
//            if (contact.getEmail() != null) {
//                log.info("告警Id=[{}]，发送渠道：{}，消息发送目的地：{}；", alarm.getId(), "邮箱", contact.getEmail());
//                sendEmail(contact.getEmail(), alarm);
//            } else {
//                log.info("告警发送渠道为 NULL，无需发送消息；");
//            }
//        }
//        return true;
//    }

    /**
     * 发送短信
     */
    private void sendSMS(Alarm alarm) {
        // TODO 真实发送短信，并删除下方模拟信息
        log.info("[模拟] 已发送邮件");
    }

    /**
     * 发送邮件
     *
     * @param address:邮件地址
     * @param alarm：告警信息
     */
    private void sendEmail(String address, Alarm alarm) throws Exception {
        //QQ邮箱测试
        String userName = "362802705@qq.com"; // 发件人邮箱
        String password = "emzvhaettbwubihf"; // 发件人密码，其实不一定是邮箱的登录密码，对于QQ邮箱来说是SMTP服务的授权文本
        String smtpHost = "smtp.qq.com"; // 邮件服务器

        //163邮箱测试
        // String userName = "wes@163.com"; // 发件人邮箱
        // String password = "123456789"; // 发件人密码，其实不一定是邮箱的登录密码，对于163邮箱来说也可能是SMTP服务的授权文本
        // String smtpHost = "smtp.163.com"; // 邮件服务器

        String to = String.join(",", address); // 收件人，多个收件人以半角逗号分隔
        String cc = "webliheng@gmail.com"; // 抄送人，多个抄送以半角逗号分隔
        String subject = "[物联网]告警通知，级别：" + alarm.getLevel().getLabel(); // 主题
        String body = "<html><body>" +
                "<h3>" + subject + "</h3>" +
                "<p><strong>异常设备:</strong> " + alarm.getThingId() + "</p>" +
                "<p><strong>告警级别:</strong> " + alarm.getLevel().getLabel() + "</p>" +
                "<p><strong>告警信息:</strong> " + alarm.getMessage() + "</p>" +
                "请您及时处理！" + "</p>" +
                "</body></html>"; // 告警信息-正文，使用HTML格式
        //List<String> attachments = Arrays.asList("C:\\Users\\87482\\Documents\\Excel\\AEP测试设备信息.xlsx"); // 附件的路径，支持多个附件
        EmailUtils emailUtils = EmailUtils.entity(smtpHost, userName, password, to, cc, subject, body, null);
        emailUtils.send(); // 发送！
    }

    /**
     * 调用回调请求
     */
    private void sendWebhook(Alarm alarm) {
        // TODO 调用回调请求
    }
}
