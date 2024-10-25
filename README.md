<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2021-blue.svg" alt="Coverage Status">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.18-blue.svg" alt="Downloads">
  <img src="https://img.shields.io/github/license/victorlamp-bayes/matrixiot-server" alt="Downloads">
</p>

## 项目简介
**矩阵IoT（MatrixIoT）**，做中国人共享共用的IoT软件平台，服务千行百业！个人与企业可 100% 免费使用。
- 融合AI大模型与IoT物联网技术，通过行业标准的物联网协议实现万物互联。
- 原生Native的支持鸿蒙HarmonyOS设备和系统。  
- 广泛支持主流NB、MQTT、HTTP等三方平台接入。  
- 支持千万级设备水平扩展，百万级以上的并发连接。
- 支持单机部署，并提供升级至多机集群的能力。
- 支持私有云和本地部署。

![产品架构](.image/%E4%BA%A7%E5%93%81%E6%9E%B6%E6%9E%84.png)

* Java后端：JDK 17 + Spring 2.7
* 管理后台的电脑端（即将开放）：Vue3 + Element Plus
* 管理后台的移动端（即将开放）：采用 [uni-app](https://github.com/dcloudio/uni-app) 方案，一份代码多终端适配，同时支持 APP、小程序、H5！
* 后端采用 Spring Cloud Alibaba 微服务架构，注册中心 + 配置中心 Nacos，服务网关 Gateway
* 数据库默认使用 MySQL + MongoDB，即将支持 PostgreSQL、MariaDB、TiDB 等，基于 MyBatis Plus、Redis + Redisson 操作
* 消息队列默认使用 RocketMQ
* 权限认证使用 Spring Security & Token & Redis，支持多终端、多种用户的认证系统，支持 SSO 单点登录

## 开源协议

① 本项目采用最宽松的 [MIT License](https://gitee.com/victorlamp/matrixiot-server/blob/master/LICENSE) 开源协议，个人与企业可 100% 免费使用，不用保留类作者、Copyright 信息。

② 代码 100% 开源，企业和个人可完全免费使用。

[更多信息](https://victorlamp.com/products/iot)

# 参考文档

## 运行环境

### 基础服务
1. 安装 Docker Engine
2. 运行 startup.sh，启动基础服务。必选服务：MySQL、MongoDB、Redis、Nacos、RocketMQ，可选服务：Prometheus、Grafana。
3. 依次运行服务：matrixiot-gateway, matrixiot-service-system, matrix-service-core。

# 文档持续完善中...