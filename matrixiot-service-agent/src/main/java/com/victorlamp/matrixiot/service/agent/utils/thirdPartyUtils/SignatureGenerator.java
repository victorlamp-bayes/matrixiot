package com.victorlamp.matrixiot.service.agent.utils.thirdPartyUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class SignatureGenerator {
    public enum SignatureMethod {
        SHA1, MD5, SHA256
    }

    public String assembleToken(String res, String accessKey) {
        StringBuilder sb = new StringBuilder();

        String version = "2020-05-29";
        String et = System.currentTimeMillis() / 1000 + 3600 + "";
        String method = SignatureMethod.SHA256.name().toLowerCase();

        try {
            String sig = URLEncoder.encode(generatorSignature(version, res, et, accessKey, method), StandardCharsets.UTF_8);
            sb.append("version=")
                    .append(version)
                    .append("&res=")
                    .append(URLEncoder.encode(res, StandardCharsets.UTF_8))
                    .append("&et=")
                    .append(et)
                    .append("&method=")
                    .append(method)
                    .append("&sign=")
                    .append(sig);
            return sb.toString();
        }catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }

    private String generatorSignature(String version, String resourceName, String expirationTime, String accessKey, String signatureMethod) {
        String encryptText = expirationTime + "\n" + signatureMethod + "\n" + resourceName + "\n" + version;
        String signature;
        byte[] bytes = HmacEncrypt(encryptText, accessKey, signatureMethod);
        signature = Base64.getEncoder().encodeToString(bytes);
        return signature;
    }

    private byte[] HmacEncrypt(String data, String key, String signatureMethod) {
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKeySpec signKey = new SecretKeySpec(Base64.getDecoder().decode(key),
                "Hmac" + signatureMethod.toUpperCase());

        try {
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("Hmac" + signatureMethod.toUpperCase());

            //用给定密钥初始化 Mac 对象
            mac.init(signKey);

            //完成 Mac 操作
            return mac.doFinal(data.getBytes());
        }catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }
}
