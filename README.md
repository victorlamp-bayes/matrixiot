<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2021-blue.svg" alt="Coverage Status">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.18-blue.svg" alt="Downloads">
  <img src="https://img.shields.io/github/license/victorlamp-bayes/matrixiot-server" alt="Downloads">
</p>

# 项目简介

**矩阵IoT（MatrixIoT）**，做中国人共享共用的IoT软件平台，服务千行百业！个人与企业可 100% 免费使用。
- 融合AI大模型与IoT物联网技术，通过行业标准的物联网协议实现万物互联。
- 原生Native的支持鸿蒙HarmonyOS设备和系统。  
- 广泛支持主流NB、MQTT、HTTP等三方平台接入。  
- 支持千万级设备水平扩展，百万级以上的并发连接。
- 支持单机部署，并提供升级至多机集群的能力。
- 支持私有云和本地部署。



[更多信息](https://victorlamp.com/products/iot)

# 参考文档

## 运行环境

### 基础服务
1. 安装 Docker Engine
2. 运行 startup.sh，启动基础服务。必选服务：MySQL、MongoDB、Redis、Nacos、RocketMQ，可选服务：Prometheus、Grafana。
3. 依次运行服务：matrixiot-gateway, matrixiot-service-system, matrix-service-core。

# 文档持续完善中...