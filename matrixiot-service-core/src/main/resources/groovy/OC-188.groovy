package groovy

import groovy.json.JsonOutput

def protocolToRawData(Object jsonObj) {
    return null
}

def rawDataToProtocol(Object rawData) {
    String identifier = "";
    String msgType = "deviceReq";
    int hasMore = 0;
    int errcode = 0;
    String cmd = "";
    String valveState = "00";// 阀门状态
    String batteryDoor = "0";// 电池门 0关闭 1打开
    String currentQuantity = "";// 当前累计流量
    String batteryVoltage = "0";// 电池状态
    String measureState = "0";// 计量状态
    String meterEleno = "";// 水表电子号
    String operateMode = "10";// 操作模式
    int mid = 0;
    String content = "";//数据内容
    String direction = "";//正反转
    //20180522 新增
    //通讯是否开启（1字节），上报周期（1字节），关阀连续上报天数（1字节HEX），关阀一天上报次数（1字节，HEX），关阀上报时间点（整点，HEX码，4字节）,信噪比（2字节有符号数）
    String imsi = "";//IMSI号
    String isOnLine = "";//通讯是否开启
    String readDays = "";//上报周期
    String closeReadDays = "";// 关阀连续上报天数（1～3）
    String time1 = "";//关阀上报时间点（整点，HEX码，4字节）
    String time2 = "";//关阀上报时间点（整点，HEX码，4字节）
    String time3 = "";//关阀上报时间点（整点，HEX码，4字节）
    String time4 = "";//关阀上报时间点（整点，HEX码，4字节）
    String closeReadTimes = "";//关阀一天上报次数（0～4）
    String snr = "";//信噪比
    String upAmount = "";//水量上报开关值
    String readtime = "";//水表上报时间
    //新添加字段
    int csq = 0;//信号强度
    String day1 = "";//近1天数据
    String day2 = "";//近2天数据
    String day3 = "";//近3天数据
    String day4 = "";//近4天数据
    String day5 = "";//近5天数据
    String day6 = "";//近6天数据
    String day7 = "";//近7天数据
    String day8 = "";//近8天数据
    String day9 = "";//近9天数据
    String day10 = "";//近10天数据
    String day11 = "";//近11天数据
    String day12 = "";//近12天数据
    String day13 = "";//近13天数据
    String day14 = "";//近14天数据
    String day15 = "";//近15天数据
    String batteryQuantity = "";//电池电量

    String ICCID = "";//ICCID
    String meterTime = "";//水表时间
    String sendScore = "";
    String deSwitch = "";//防拆开关
    String deState = "";//防拆状态
    String doSwitch = "";//是否摆动阀门
    String doSwitchScore = "";//阀门摆动间隔
    String touchState = "";//触摸按键
    String flashState = "";//flash异常
    String meterVersion = "";//软件版本号
    String cellId = "";//cellid
    String txPower = "";
    String sendType = "";//发送类型
    String autoSwitchQu = "";//自动关阀吨数
    String warningSwitchQu = "";//警告关阀吨数
    String autoSwitchState = "";//关阀开关
    String dateTime = "";
    String IMEI = "";

    String hourData24 = "";
    String hourData23 = "";
    String hourData22 = "";
    String hourData21 = "";
    String hourData20 = "";
    String hourData19 = "";
    String hourData18 = "";
    String hourData17 = "";
    String hourData16 = "";
    String hourData15 = "";
    String hourData14 = "";
    String hourData13 = "";
    String hourData12 = "";
    String hourData11 = "";
    String hourData10 = "";
    String hourData09 = "";
    String hourData08 = "";
    String hourData07 = "";
    String hourData06 = "";
    String hourData05 = "";
    String hourData04 = "";
    String hourData03 = "";
    String hourData02 = "";
    String hourData01 = "";

    String totalHexString = "";//指令的全部内容

    //力拓达解析报文
    //6810320000011200183150310100D60100000200D6010000CC010000CC010000CC010000CC010000CC010000CC010000CC010000C2010000C2010000C2010000C2010000C2010000B8010000B8010000EF000000000000001316012D16
    Map<String, Object> jsonObj = new HashMap<>();

    Map<String, Object> root = new HashMap<>();
    try {

        def hexString = rawData.toString().toUpperCase();
        def feIndex = hexString.indexOf("68");
        if (feIndex >= 0)
            hexString = hexString.substring(feIndex);//上报数据的全部内容if(feIndex>=0)
        totalHexString = hexString;
        if (hexString.length() == 96) {
            //力拓达协议原有188协议
            hexString = hexString.substring(4 * 2);//上报数据的全部内容
            hexString = convertUtils.convertHexToString(hexString);
            totalHexString = hexString;
        } else {
            //华旭协议
            feIndex = hexString.indexOf("681");
            if (feIndex >= 0)
                hexString = hexString.substring(feIndex);//上报数据的全部内容
            totalHexString = hexString;
        }

        String conMar = "";//控制码
        conMar = hexString.substring(18, 20).toUpperCase();//控制码

        if (conMar.equals("C4") || conMar.equals("E1") || conMar.equals("F1") || conMar.equals("E5")
                || conMar.equals("C1") || conMar.equals("D6") || conMar.equals("D5"))
            cmd = (hexString.substring(18, 20) + "0000").toUpperCase();
        else
            cmd = (hexString.substring(18, 20) + hexString.substring(22, 26)).toUpperCase();

        meterEleno = convertUtils.hexTransfer(hexString.substring(4, 18));//获取水表编码
        content = hexString.substring(28, hexString.length());//数据内容至指令结尾
        mid = Integer.parseInt(hexString.substring(26, 28), 16);
        identifier = cmd;

        if (cmd.equals("313101")) {
            msgType = "deviceReq";
            String sTemp = "";
            String sTempAllString = "";
            int j = 0;
            sTempAllString = hexString.substring(28);
            if (hexString.length() == (93 * 2)) {
                //当前累计用量
                j = 0;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 当前累积量
                String amount = convertUtils.ConvertQuantity6(sTemp);
                currentQuantity = amount;
                // 系统状态字
                j = 4;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 4);
                sTemp = convertUtils.hexStr2BinStr(sTemp);
                valveState = sTemp.substring(6, 8);// 阀门状态
                batteryVoltage = sTemp.substring(5, 6);// 电池状态
                measureState = sTemp.substring(4, 5);// 计量状态
                direction = sTemp.substring(1, 2);// 正反转
                batteryDoor = sTemp.substring(3, 4);// 电池门 拆卸
                touchState = sTemp.substring(2, 3);// 触摸按键
                deSwitch = sTemp.substring(0, 1);
                //近15天数据
                j = 6;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近1天用量
                day1 = convertUtils.ConvertQuantity6(sTemp);
                j = 10;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近2天用量
                day2 = convertUtils.ConvertQuantity6(sTemp);
                j = 14;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近3天用量
                day3 = convertUtils.ConvertQuantity6(sTemp);
                j = 18;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近4天用量
                day4 = convertUtils.ConvertQuantity6(sTemp);
                j = 22;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近5天用量
                day5 = convertUtils.ConvertQuantity6(sTemp);
                j = 26;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近6天用量
                day6 = convertUtils.ConvertQuantity6(sTemp);
                j = 30;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近7天用量
                day7 = convertUtils.ConvertQuantity6(sTemp);
                j = 34;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近8天用量
                day8 = convertUtils.ConvertQuantity6(sTemp);
                j = 38;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近9天用量
                day9 = convertUtils.ConvertQuantity6(sTemp);
                j = 42;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近10天用量
                day10 = convertUtils.ConvertQuantity6(sTemp);
                j = 46;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近11天用量
                day11 = convertUtils.ConvertQuantity6(sTemp);
                j = 50;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近12天用量
                day12 = convertUtils.ConvertQuantity6(sTemp);
                j = 54;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近13天用量
                day13 = convertUtils.ConvertQuantity6(sTemp);
                j = 58;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近14天用量
                day14 = convertUtils.ConvertQuantity6(sTemp);
                j = 62;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近15天用量
                day15 = convertUtils.ConvertQuantity6(sTemp);

                //电池电量
                j = 66;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 2);// 电池电压
                batteryQuantity = String.valueOf(Math.round(Integer.parseInt(sTemp, 16) * 15.37));
                //水表上报时间
                j = 67;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 14);// 电池电压
                readtime = sTemp.substring(0, 4) + ":" + sTemp.substring(4, 6) + ":" + sTemp.substring(6, 8) + " " + sTemp.substring(8, 10) + ":" + sTemp.substring(10, 12) + ":" + sTemp.substring(12, 14);
                //信号强度
                j = 74;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 2);// csq
                csq = Integer.parseInt(sTemp, 16);
                //信噪比
                snr = String.valueOf(Integer.parseInt(sTempAllString.substring(76 * 2, 76 * 2 + 2) + sTempAllString.substring(75 * 2, 75 * 2 + 2), 16));
            }
            //特殊处理：将水表电子号及水表的seq放置 identifier 字段中
            identifier += ("-" + hexString.substring(4, 18) + "-" + hexString.substring(26, 28));

            //力拓达与华旭兼容性处理
            if (hexString.length() == (128 * 2) || hexString.length() == (126 * 2)) {
                // 系统状态字
                j = 16;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 4);
                sTemp = convertUtils.hexStr2BinStr(sTemp);
                valveState = sTemp.substring(6, 8);// 阀门状态
                batteryVoltage = sTemp.substring(5, 6);// 电池状态
                measureState = sTemp.substring(4, 5);// 计量状态
                direction = sTemp.substring(1, 2);// 正反转
                batteryDoor = sTemp.substring(3, 4);// 电池门 拆卸
                touchState = sTemp.substring(2, 3);// 触摸按键
                deSwitch = sTemp.substring(0, 1);
                //近15天数据
                j = 18;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近1天用量
                day1 = convertUtils.ConvertQuantity6(sTemp);
                j = 22;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近2天用量
                day2 = convertUtils.ConvertQuantity6(sTemp);
                j = 26;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近3天用量
                day3 = convertUtils.ConvertQuantity6(sTemp);
                j = 30;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近4天用量
                day4 = convertUtils.ConvertQuantity6(sTemp);
                j = 34;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近5天用量
                day5 = convertUtils.ConvertQuantity6(sTemp);
                j = 38;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近6天用量
                day6 = convertUtils.ConvertQuantity6(sTemp);
                j = 42;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近7天用量
                day7 = convertUtils.ConvertQuantity6(sTemp);
                j = 46;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近8天用量
                day8 = convertUtils.ConvertQuantity6(sTemp);
                j = 50;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近9天用量
                day9 = convertUtils.ConvertQuantity6(sTemp);
                j = 54;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近10天用量
                day10 = convertUtils.ConvertQuantity6(sTemp);
                j = 58;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近11天用量
                day11 = convertUtils.ConvertQuantity6(sTemp);
                j = 62;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近12天用量
                day12 = convertUtils.ConvertQuantity6(sTemp);
                j = 66;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近13天用量
                day13 = convertUtils.ConvertQuantity6(sTemp);
                j = 70;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近14天用量
                day14 = convertUtils.ConvertQuantity6(sTemp);
                j = 74;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 近15天用量
                day15 = convertUtils.ConvertQuantity6(sTemp);
                //当前累计用量
                j = 94;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 8);// 当前累积量
                currentQuantity = convertUtils.ConvertQuantity6(sTemp); ;
                j = 106;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 2);// 工作模式
                operateMode = sTemp;
                j = 109;
                sTemp = sTempAllString.substring(j * 2, j * 2 + 2);// csq
                csq = Integer.parseInt(sTemp, 16);
                if (hexString.length() == (128 * 2)) {
                    int snrt = Integer.parseInt(sTempAllString.substring(111 * 2, 111 * 2 + 2) + sTempAllString.substring(110 * 2, 110 * 2 + 2), 16);
                    snr = String.valueOf(((snrt & 0x8000) > 0) ? (snrt - 0x10000) : (snrt));
                }
            }

//            root.put("identifier", identifier);
//            root.put("msgType", msgType);
            root.put("meterEleno",meterEleno);
            root.put("valveState",valveState);
            root.put("batteryVoltage",batteryVoltage);
            root.put("measureState",measureState);
            root.put("direction",direction);
            root.put("batteryDoor",batteryDoor);
            root.put("touchState",touchState);
            root.put("deSwitch",deSwitch);
            root.put("day1",day1);
            root.put("day2",day2);
            root.put("day3",day3);
            root.put("day4",day4);
            root.put("day5",day5);
            root.put("day6",day6);
            root.put("day7",day7);
            root.put("day8",day8);
            root.put("day9",day9);
            root.put("day10",day10);
            root.put("day11",day11);
            root.put("day12",day12);
            root.put("day13",day13);
            root.put("day14",day14);
            root.put("day15",day15);
            root.put("csq",csq);
            root.put("snr",snr);
            root.put("readtime",readtime);
            root.put("currentQuantity",currentQuantity);
            root.put("batteryQuantity",batteryQuantity);
            root.put("operateMode",operateMode);
        }

        String jsonResult = JsonOutput.toJson(root)
        return jsonResult

    } catch (Exception e) {
        jsonObj.put("message", e.getMessage())
        String jsonResult = JsonOutput.toJson(jsonObj)
        return jsonResult
    }
}
