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

------

------

# 用户手册（测试）

## 目录

### 第1章  登录

#### 1.1 登录教程

用户进入登陆页面，输入系统默认的用户名和密码

Username： admin

Password： hviot123!@#

![1730945674821](.image/user-doc/登录1.png)

- 点击登录，进入安全验证。

![1730945759835](.image/user-doc/登录2.png)



- 当安全验证通过后，页面将跳转到物联网平台首页。

- 该页面可看到物联网平台的产品统计、设备统计、告警统计、设备数量统计可视图、设备状态统计可视图、以及地区设备数量分布的各项信息展示；

![1730945812340](.image/user-doc/登录3.png)

#### 1.2 个人中心

在物联网平台的首页的右上角可以看到用户头像，点击用户头像，则会显示个人中心，在个人中心用户可以修改个人的基本信息和修改密码。

![1730945862175](.image/user-doc/个人中心1.png)

#### **1.3自定义页面**

点击页面右侧的蓝色齿轮按钮，用户可以自己DIY物联网的UI布局、主题样式和界面显示。同时支持一键切换浅色/深色模式以及添加自定义水印。

![1730946599321](.image/user-doc/自定义页面1.png)

### 第2章 设备管理

#### 2.1 产品

打开左侧导航栏 --> 设备管理 --> 产品，即可进入产品管理页面。

![1730946792012](.image/user-doc/产品1.png)

##### **2.1.1 创建产品**

点击新增按钮，创建产品

![1730946897545](.image/user-doc/产品2.png)

**基本信息：**

- 产品名称：确保名称具有唯一性和描述性，便于用户识别和搜索。
- 节点类型：选择系统预置的节点类型：如：直连设备/网关设备/网关子设备。（直连设备和网关设备，需要选择连网协议/网关子设备需要选择接入网关协议）
- 数据格式：选择系统预置的数据格式，如：HVIOT标准格式/自定义。
- 产品品类：选择系统预置的产品品类：如：NBIoT水表/LoRaWAN水表....
- 厂商：填写产品的制造商或生产厂家的名称。
- 产品型号：提供产品的具体型号，以便用户区分不同版本或配置的产品。
- 产品描述：详细描述产品的功能、特点、应用场景等，帮助用户更好地理解产品。

> 网关产品所创建的设备可以挂载网关子产品所创建的设备，具有子设备管理模块，维持子设备的拓扑关系，和将拓扑关系同步到物联网平台。

**外部配置：**

如果该产品与第三方系统或服务集成，请在此处选择物联网系统预置支持的三方平台配置模板，并填写相关配置信息如API密钥、认证方式、接口地址等。

> 当产品创建成功后，需要点击发布才可以使用。

##### **2.1.2更新产品**

进入产品详情页面，点击编辑，即可更行产品信息。