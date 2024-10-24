package com.victorlamp.matrixiot.service.agent.thirdPartyService.LoRaWAN.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLoRaDeviceDataDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8263039985343411913L;

    /**
     * 设备唯一编号
     */
    private String mac;

    /**
     * 应用服务电子识别号
     */
    private String appeui;

    /**
     * 数据上传时间  格式:yyyyMMddHHmmss
     */
    private String lastUpdateTime;

    /**
     * 设备传输数据  16进制字符串
     */
    private String data;

    /**
     * 解析结果信息
     * code为0表示解析成功，code为1表示没有配置解析规则，code为2表示解析失败，code为3表示解析超时
     */
    private String reserver;

    /**
     * 数据类型
     * 223表示LoRa节点自发的心跳包，其他表示数据
     */
    private Integer data_type;

    /**
     * 0到多条网关信息
     */
    private String gateways;
}
