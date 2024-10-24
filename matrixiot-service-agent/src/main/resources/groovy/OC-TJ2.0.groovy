package groovy

import com.alibaba.fastjson2.JSON
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def protocolToRawData(String jsonString) {
    String StartChar = "";
    String EndChar = "16";

    String P_DataLength; //帧数据域长度

    String P_ControlField;//控制域

    String P_AddressField;//地址域

    String P_DataSymbol;//数据单元标识

    String P_SEQ;//SEQ真序列

    String P_Content = "";//帧数据单元域

    String P_Checksum = "";//校验码

    JsonSlurper jsonSlurper = new JsonSlurper();
    def jsonObj = jsonSlurper.parseText(jsonString)

    Map<String, Object> value = new HashMap<>();
    Map<String, Object> resultObj = new HashMap<>();
    try {
        if ("WriteValve".equals(jsonObj.getAt("identifier"))) {
            Object params = jsonObj.getAt("params");
            String meterEleno = params.getAt("electronicNo");//电子表号
            String valve = params.getAt("valveStatus")
            String subEleno = "";//转换后的电子表号
            //转换电子表号
            for (int j = 12; j >= 0;) {
                subEleno += meterEleno.substring(j, j + 2);
                j -= 2;
            }
            P_AddressField = subEleno;
            if (valve.equals("1"))
                P_Content = "55";//开阀
            else if (valve.equals("0") || valve.equals("9"))
                P_Content = "99";//关阀

            P_ControlField = "04";
            P_DataSymbol = "A017";
            P_SEQ = "0C";
            String sBody = P_DataSymbol + P_SEQ + P_Content;
            P_DataLength = (sBody.length() / 2);
            String sHead = "6810" + P_AddressField + P_ControlField + convertUtils.padLeft(P_DataLength, 2);

            P_Checksum = convertUtils.calculateChecksum(sHead + sBody)

            def data = sHead + sBody + P_Checksum + EndChar

            resultObj.put("serviceId", "BusinessService")
            resultObj.put("method", "SEND_DEVICE_MSG")
            value.put("value", data)
            resultObj.put("paras", value)

            String jsonResult = JsonOutput.toJson(resultObj)
            return jsonResult

        } else {
            resultObj.put("message", "identifier mismatching")
            String jsonResult = JsonOutput.toJson(resultObj)
            return jsonResult
        }
    } catch (Exception e) {
        e.fillInStackTrace()
        resultObj.put("message", e.getMessage())
        String jsonResult = JsonOutput.toJson(resultObj)
        return jsonResult
    }
}

def rawDataToProtocol(Object rawData) {
    String identifier = "";
    String msgType = "deviceReq";
    int hasMore = 0;
    int errcode = 0;
    String cmd = "";
    //上报基本数据
    String meterEleno = "";//水表电子号
    String IMEI = "";//通讯模块IMEI
    String MeterType = "";//仪表类型
    String Version = "";//协议版本号
    String FunctionCode = "";//功能码
    String RSPR = "";//信号强度
    String SNR = "";//信噪比
    String COVERLVER = "";//覆盖等级
    String CellID = "";//小区ID
    String UpFlag = "";//上行帧标识
    String DataLength = "";//数据域长度
    //上报数据域内容
    String TLVLength = "";//TLV数据域长度
    //tag 2 实时数据
    String currentQuantity = "";// 当前累计流量
    String dayPositiveQuantity = "";//日结累计正流量
    String dayRefluxQuantity = "";//日结累计逆流量
    String collectTime = "";//采集时间
    String dayMaxQuantity = "";//日最高流量
    String dayMaxQuantityTime = "";//日最高流量时间
    String batteryVoltage = "0";// 电池电压
    String successTimes = "";//累计发送成功次数
    String failTimes = "";//累计发送失败次数
    String valveState = "0";// 阀门状态（0开  01关  02 故障 ）
    //tag 4 普通告警参数
    String lowVoltageWarning = "0";//低电压告警（1告警 0不告警）
    String magneticInterWarning = "0";//磁干扰告警（1告警 0不告警）
    //tag 5  即时告警参数
    String eleModuleWarning = "0";//电子模块分离告警（1告警 0不告警）
    String overCurrentWarning = "0";//过流告警（1告警 0不告警）
    String overCurrentTime = "";//过流告警开始时间
    String overCurrentQuantity = "0";//过流流量
    String refluxWarning = "0";//反流告警（1告警 0不告警）
    String refluxWarningTime = "";//反流告警时间（1告警 0不告警）
    String refluxWarningQuantity = "";//反流告警流量（1告警 0不告警）
    String valveWaring = "0";//阀门异常告警（1异常 0 正常）
    //tag 6 周期数据
    String interMeasureStartTime = "";//间隔计量流量开始时间
    String interMeasureTime = "";//数据记录间隔
    Integer recordCount = 0;//记录个数
    ArrayList<String> recordValues = new ArrayList<String>();//记录值

    //tag 7 密集 周期数据
    String concentStartTime = "";//密集数据开始时间
    String concentTime = "";//数据记录间隔
    Integer concentRecordCount = 0;//记录个数
    ArrayList<String> concentRecordValues = new ArrayList<String>();//记录值

    Map<String, String> resultValues = new HashMap<String, String>();//记录值
    //tag 1 基础信息
    String imsi = "";//IMSI
    String meterCode = "";//水表表号
    String factoryNo = "";//厂商代码
    String meterTime = "";//终端时钟
    String workDay = "";//终端运行时间
    String meterVersion = "";//终端软件版本

    //tag 3 终端参数
    String overflowAlarmThreshold = "";//过流告警阀值
    String overflowTime = "";//持续过流时间
    String refluxAlarmThreshold = "";//反流告警阀值
    String refluxAlarmTime = "";//持续反流告警时间
    String instantWarningID = "";//即时告警ID
    String serverIP = "";//服务器IP及端口
    String APN = "";//运营商APN;
    String periodReportStartTime = "";//周期上报开始时间
    String periodReportEndTime = "";//周期上报结束时间
    String periodReportTime = "";//周期上报时长
    String periodReportFrence = "";//周期上报频率
    String reportTimes = "";//上报重发机制
    String vloAlarmThreshold = "";//电压告警阀值
    String intensiveReportStartTime = "";//密集上报开始时间
    String switchValve = "";//阀门开关指令
    String encrypEnable = "";//加密使能
    String retransmissionFrence = "";//重发周期
    String periodReportInterval = "";//周期上报数据间隔
    String conReportInterval = "";//密集上报数据间隔
    String SN = "";//加密密钥
    String Ble = "";//蓝牙使能

    //tag 8
    String dirOrder = "";//透传指令

    int mid = 0;
    String content = "";//数据内容
    String direction = "";//正反转
    String snr = "";//信噪比
    String batteryQuantity = "";//电池电量

    String totalHexString = "";//指令的全部内容

    Map<String, Object> jsonObj = new HashMap<>();
    Map<String, Object> root = new HashMap<>();

    try {
        if (JSON.isValid(rawData.toString())) {
            // 解析指令回调
            JsonSlurper jsonSlurper = new JsonSlurper();
            def raw = jsonSlurper.parseText(rawData.toString());
            if (raw.containsKey("commandId") && raw.containsKey("deviceId") && raw.containsKey("result")) {
                boolean status = true;
                String message = "";
                String commandId = raw.getAt("commandId").toString();
                def result = raw.getAt("result");
                String recordStatus = result.getAt("resultCode").toString();

                Map<String, Object> callbackRecord = new HashMap<>();
                callbackRecord.put("status", recordStatus);

                /*
                  PENDING 表示缓存未下发
                  EXPIRED 表示命令已经过期
                  CANCELED 表示命令已经被撤销执行
                  SENT 表示命令正在下发
                  SUCCESSFUL 表示命令已经成功执行
                  FAILED 表示命令执行失败
                  TIMEOUT 表示命令下发执行超时
                  DELIVERED 表示命令已送达设备
                */

                if (recordStatus.equals("PENDING")) {
                    status = true;
                    message = "缓存未下发"
                } else if (recordStatus.equals("SENT")) {
                    status = true;
                    message = "命令正在下发"
                } else if (recordStatus.equals("DELIVERED")) {
                    status = true;
                    message = "命令已送达设备"
                } else if (recordStatus.equals("SUCCESSFUL")) {
                    status = true;
                    message = "命令已经成功执行"
                } else if (recordStatus.equals("EXPIRED")) {
                    status = false;
                    message = "命令已经过期"
                } else if (recordStatus.equals("CANCELED")) {
                    status = false;
                    message = "命令已经被撤销执行"
                } else if (recordStatus.equals("TIMEOUT")) {
                    status = false;
                    message = "命令下发执行超时"
                } else if (recordStatus.equals("FAILED")) {
                    status = false;
                    message = "命令执行失败"
                }
                callbackRecord.put("message", message);

                jsonObj.put("status", status);
                jsonObj.put("callbackRecord", callbackRecord);
                jsonObj.put("commandId", commandId);

                String jsonResult = JsonOutput.toJson(jsonObj)
                return jsonResult;
            }
        }
    } catch (Exception e) {
        jsonObj.put("message", e.getMessage())
        String jsonResult = JsonOutput.toJson(jsonObj)
        return jsonResult
    }

    try {
        def hexString = rawData.toString().toUpperCase()
        def feIndex = hexString.indexOf("68");
        if (feIndex >= 0)
            hexString = hexString.substring(feIndex);//上报数据的全部内容if(feIndex>=0)
        totalHexString = hexString;
        if (hexString.substring(2, 3).equals("3") || hexString.substring(2, 3).equals("F"))//如果为F则为天津改造协议，否则为188协议
        {
            String conMar = hexString.substring(36, 38).toUpperCase();//功能码 （发起：01 02 03 04 05，应答 ：51 52 53 54 55）
            cmd = conMar;
            identifier = conMar;
            if (hexString.substring(2, 3).equals("F")) {
                IMEI = hexString.substring(3, 18);//IMEI
                meterEleno = convertUtils.hexTransfer(hexString.substring(18, 32));
            } else {
                meterEleno = convertUtils.convertHexToString(hexString.substring(2, 32));//IMEI ASCII
                IMEI = convertUtils.convertHexToString(hexString.substring(2, 32));//IMEI ASCII
            }
            mid = Integer.parseInt(hexString.substring(hexString.length() - 8, hexString.length() - 4), 16);//指令的mid标识
            //解析数据
            if (cmd.equals("02") || cmd.equals("04")) {//终端向平台发送实时数据	及即时告警数据
                msgType = "deviceReq";
                String sTempHex = "";
                //开始解析
                //解析帧固定参数
                MeterType = String.valueOf(Integer.parseInt(hexString.substring(32, 34), 16));//设备类型 0 小口径水表 1大口径水表
                Version = hexString.substring(34, 35) + "." + hexString.substring(35, 36);//协议版本号
                FunctionCode = hexString.substring(36, 38);//功能码
                RSPR = String.valueOf(Integer.valueOf(hexString.substring(38, 42), 16).shortValue());//信号强度
                SNR = String.valueOf(Integer.valueOf(hexString.substring(42, 46), 16).shortValue());//信噪比
                COVERLVER = hexString.substring(46, 48).equals("FF") ? "" : String.valueOf(Integer.valueOf(hexString.substring(46, 48), 16));//覆盖等级
                CellID = hexString.substring(48, 56).equals("FFFFFFFF") ? "" : String.valueOf(Integer.valueOf(hexString.substring(48, 56), 16));//小区ID
                UpFlag = hexString.substring(56, 58);//上行帧标识
                DataLength = String.valueOf(Integer.valueOf(hexString.substring(58, 62), 16));//数据域字节数
                sTempHex = hexString.substring(62, hexString.length() - 8);//数据域至MID之前加密部分

                if (sTempHex.length() == (Integer.parseInt(DataLength) * 2 - 4))//判断数据域长度是否与上传的参数一致（去除mid）
                {
                    if (UpFlag.equals("01"))//加密(加密数据首先进行解密)
                    {
                        sTempHex = convertUtils.AESECBPKCS7Padding2Decrypt(sTempHex);    //解密算法
                    }
                    //println("解析指令完成："+sTempHex);
                    TLVLength = String.valueOf(Integer.valueOf(sTempHex.substring(0, 4), 16));
                    //数据长度(数据长度指 TLV 数据字节数+结果码-4结果码)

                    sTempHex = sTempHex.substring(4);//只保留TLV数据（不包含数据域部分的数据长度）
                    String sTemp = "";//用于处理每一块TLV数据的临时变量

                    while (true) {
                        try {
                            if (sTempHex.length() == 0)
                                break;
                            //=========TAG第1部分===========
                            String tag = String.valueOf(Integer.valueOf(sTempHex.substring(0, 2), 16));//tag 标记
                            int length = Integer.valueOf(sTempHex.substring(2, 6), 16);//数据长度
                            //if(sTempHex.length()<(length*2+6))
                            //break;
                            sTemp = sTempHex.substring(6, length * 2 + 6);//TLV的Value部分
                            //解析操作
                            if (tag.equals("2"))//实时数据
                            {
                                String dataID = sTemp.substring(0, 2);//数据ID 1
                                currentQuantity = convertUtils.ConvertQuantity3(sTemp.substring(2, 10));// 当前累计流量 4
                                dataID = sTemp.substring(10, 12);//数据ID 1
                                dayPositiveQuantity = convertUtils.ConvertQuantity1(sTemp.substring(12, 20));//日结累计正流量 4
                                dataID = sTemp.substring(20, 22);//数据ID 1
                                dayRefluxQuantity = convertUtils.ConvertQuantity1(sTemp.substring(22, 30));//日结累计逆流量 4
                                dataID = sTemp.substring(30, 32);//数据ID 1
                                collectTime = "20" + sTemp.substring(32, 34) + "-" + sTemp.substring(34, 36) + "-" + sTemp.substring(36, 38) + " " + sTemp.substring(38, 40) + ":" + sTemp.substring(40, 42) + ":" + sTemp.substring(42, 44);
//采集时间 6
                                dataID = sTemp.substring(44, 46);//数据ID 1
                                dayMaxQuantity = convertUtils.ConvertQuantity1(sTemp.substring(46, 50));//日最高流量  2
                                dataID = sTemp.substring(50, 52);//数据ID 1
                                dayMaxQuantityTime = sTemp.substring(52, 54) + sTemp.substring(54, 56) + sTemp.substring(56, 58) + sTemp.substring(58, 60) + sTemp.substring(60, 62) + sTemp.substring(62, 64);
//日最高流量时间 6
                                dataID = sTemp.substring(64, 66);//数据ID 1
                                batteryQuantity = convertUtils.ConvertQuantity5(sTemp.substring(66, 70));// 电池电压 2
                                dataID = sTemp.substring(70, 72);//数据ID 1
                                successTimes = String.valueOf(Integer.valueOf(sTemp.substring(72, 76), 16));//累计发送成功次数 2
                                dataID = sTemp.substring(76, 78);//数据ID 1
                                failTimes = String.valueOf(Integer.valueOf(sTemp.substring(78, 82), 16));//累计发送失败次数	2
                                dataID = sTemp.substring(82, 84);//数据ID 1
                                valveState = String.valueOf(Integer.valueOf(sTemp.substring(84, 86), 16));// 阀门状态1（0开  1关  2 故障 ）
                            }
                            if (tag.equals("4"))//普通告警参数
                            {
                                String dataID = sTemp.substring(0, 2);//数据ID 1
                                lowVoltageWarning = String.valueOf(Integer.valueOf(sTemp.substring(2, 4), 16));//低电压告警（1告警 0不告警）
                                dataID = sTemp.substring(4, 6);//数据ID 1
                                magneticInterWarning = String.valueOf(Integer.valueOf(sTemp.substring(6, 8), 16));//磁干扰告警（1告警 0不告警）
                            }
                            if (tag.equals("5"))//即时告警参数
                            {
                                String sTemp2 = sTemp;
                                while (true) {
                                    if (sTemp2.length() == 0)
                                        break;
                                    String dataID = sTemp2.substring(0, 2);//数据ID 1
                                    if (dataID.equals("00")) {
                                        eleModuleWarning = String.valueOf(Integer.valueOf(sTemp2.substring(2, 4), 16));//电子模块分离告警（1告警 0不告警）
                                        sTemp2 = sTemp2.substring(4);//截取长度需加上tag+length
                                    }
                                    if (dataID.equals("01")) {
                                        overCurrentWarning = String.valueOf(Integer.valueOf(sTemp2.substring(2, 4), 16));//过流告警（1告警 0不告警）
                                        overCurrentTime = "20" + sTemp2.substring(4, 6) + "-" + sTemp2.substring(6, 8) + "-" + sTemp2.substring(8, 10) + " " + sTemp2.substring(10, 12)
                                        +":" + sTemp2.substring(12, 14) + ":" + sTemp2.substring(14, 16);//过流告警开始时间
                                        overCurrentQuantity = convertUtils.ConvertQuantity3(sTemp2.substring(16, 20));//过流流量
                                        sTemp2 = sTemp2.substring(20);//截取长度需加上tag+length
                                    }
                                    if (dataID.equals("02")) {
                                        refluxWarning = String.valueOf(Integer.valueOf(sTemp2.substring(2, 4), 16)); ;//反流告警（1告警 0不告警）
                                        refluxWarningTime = "20" + sTemp2.substring(4, 6) + "-" + sTemp2.substring(6, 8) + "-" + sTemp2.substring(8, 10) + " " + sTemp2.substring(10, 12) + ":" + sTemp2.substring(12, 14) + ":" + sTemp2.substring(14, 16);
//反流告警时间（1告警 0不告警）
                                        refluxWarningQuantity = convertUtils.ConvertQuantity3(sTemp2.substring(16, 20));//反流告警流量（1告警 0不告警）
                                        sTemp2 = sTemp2.substring(20);//截取长度需加上tag+length
                                    }
                                    if (dataID.equals("03")) {
                                        valveWaring = String.valueOf(Integer.valueOf(sTemp2.substring(2, 4), 16));//阀门异常告警（1异常 0 正常）*/
                                        sTemp2 = sTemp2.substring(4);//截取长度需加上tag+length
                                    }
                                }

                            }
                            if (tag.equals("6"))//周期数据
                            {
                                interMeasureStartTime = "20" + sTemp.substring(0, 2) + "-" + sTemp.substring(2, 4) + "-" + sTemp.substring(4, 6) + " " + sTemp.substring(6, 8) + ":" + sTemp.substring(8, 10) + ":" + sTemp.substring(10, 12);
//间隔计量流量开始时间
                                interMeasureTime = String.valueOf(Integer.valueOf(sTemp.substring(12, 14), 16));//数据记录间隔
                                recordCount = Integer.valueOf(sTemp.substring(14, 16), 16);//记录个数
                                for (int i = 0; i < recordCount; i++) {
                                    String value = convertUtils.ConvertQuantity4(sTemp.substring(16 + (i * 4), 20 + (i * 4)));
                                    recordValues.add(value);
                                }
                            }
                            if (tag.equals("7"))//密集周期数据
                            {
                                concentStartTime = "20" + sTemp.substring(0, 2) + "-" + sTemp.substring(2, 4) + "-" + sTemp.substring(4, 6) + " " + sTemp.substring(6, 8) + ":" + sTemp.substring(8, 10) + ":" + sTemp.substring(10, 12);
//间隔计量流量开始时间
                                concentTime = String.valueOf(Integer.valueOf(sTemp.substring(12, 14), 16));//数据记录间隔
                                concentRecordCount = Integer.valueOf(sTemp.substring(14, 16), 16);//记录个数
                                for (int i = 0; i < concentRecordCount; i++) {
                                    String vale22 = sTemp.substring(16 + (i * 8), 24 + (i * 8));
                                    String value = convertUtils.ConvertQuantity3(vale22.substring(0, 4));
                                    concentRecordValues.add(value);
                                    String value2 = convertUtils.ConvertQuantity3(vale22.substring(4, 8));
                                    concentRecordValues.add(value2);
                                }
                            }
                            sTempHex = sTempHex.substring(length * 2 + 6);//截取长度需加上tag+length
                        } catch (Exception ex) {
                            //logger.info("解析长度异常"+ex.getMessage());
                            break;
                        }
                    }

                }
                //特殊处理水表的mid放置 identifier 字段中
                identifier += ("-" + String.valueOf(mid) + "-" + hexString.substring(34, 36) + "-" + UpFlag);


                // 组装body体


//                root.put("identifier", identifier);
//                root.put("msgType", msgType);
                List<Object> arrynode = new ArrayList<Object>();
                // serviceId=WaterMeterBasic 数据基础信息
                //Map<String,Object>  batteryNode =  new HashMap<>();
                //batteryNode.put("serviceId", "ReadSmallMeterInfo");

                //Map<String,Object>  basicData =  new HashMap<>();;
                root.put("IMEI", IMEI);
                root.put("MeterType", MeterType);
                root.put("Version", Version);
                root.put("RSPR", RSPR);
                root.put("SNR", SNR);
                root.put("COVERLVER", COVERLVER);
                root.put("cellId", convertUtils.fill(CellID, 12));
                root.put("ElectronicNo", meterEleno);
                if (cmd.equals("02")) {//实时上报
                    root.put("tag", "02");//实时上报
                    root.put("currentQuantity", currentQuantity);
                    root.put("dayPositiveQuantity", dayPositiveQuantity);
                    root.put("dayRefluxQuantity", dayRefluxQuantity);
                    root.put("collectTime", collectTime);
                    root.put("dayMaxQuantity", dayMaxQuantity);
                    root.put("dayMaxQuantityTime", dayMaxQuantityTime);
                    root.put("batteryQuantity", batteryQuantity);
//                    root.put("battery", convertUtils.fill(String.valueOf(Math.round((Double.parseDouble(batteryQuantity) / 3.68) * 100)), 3));
                    root.put("successTimes", successTimes);
                    root.put("failTimes", failTimes);


                    root.put("valveState", valveState);
                    root.put("lowVoltageWarning", lowVoltageWarning);
                    root.put("magneticInterWarning", magneticInterWarning);

                    root.put("eleModuleWarning", eleModuleWarning);
                    root.put("overCurrentWarning", overCurrentWarning);
                    root.put("overCurrentTime", overCurrentTime);
                    root.put("overCurrentQuantity", overCurrentQuantity);
                    root.put("refluxWarning", refluxWarning);
                    root.put("refluxWarningTime", refluxWarningTime);
                    root.put("refluxWarningQuantity", refluxWarningQuantity);
                    root.put("valveWaring", valveWaring);

                }
                if (cmd.equals("04")) {
                    root.put("tag", "04");//区分即时告警
                }
                //batteryNode.put("serviceData", basicData);

                //arrynode.add(batteryNode);
                //root.put("data", basicData);
            }
        }

        // 事件
        Map<String, Object> event = new HashMap<>();
        if (lowVoltageWarning.equals("1")) {
            event.put("lowVoltageWarning", lowVoltageWarning)
        }
        if (magneticInterWarning.equals("1")) {
            event.put("magneticInterWarning", magneticInterWarning)
        }
        if (eleModuleWarning.equals("1")) {
            event.put("eleModuleWarning", eleModuleWarning)
        }
        if (overCurrentWarning.equals("1")) {
            event.put("overCurrentWarning", overCurrentWarning)
        }
        if (refluxWarning.equals("1")) {
            event.put("refluxWarning", refluxWarning)
        }
        if (valveWaring.equals("1")) {
            event.put("valveWaring", valveWaring)
        }

        if (event.isEmpty()) {
            String jsonResult = JsonOutput.toJson(root)
            return jsonResult
        } else {
            jsonObj.put("properties", root)
            jsonObj.put("events", event)
            String jsonResult = JsonOutput.toJson(jsonObj)
            return jsonResult
        }
    } catch (Exception e) {
        jsonObj.put("message", e.getMessage())
        String jsonResult = JsonOutput.toJson(jsonObj)
        return jsonResult
    }
}

