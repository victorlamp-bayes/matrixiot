package groovy

import groovy.json.JsonOutput

def rawDataToProtocol(Object rawData) {
    Map<String, Object> jsonObj = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    try {

        if (rawData.toString().length() == 56) {
            List<String> list = convertUtils.split(rawData.toString(), 2);

            def WSV = convertUtils.reserveDecimal(Integer.parseInt(convertUtils.hexSubHex(convertUtils.hexTransfer(list[18] + list[19] + list[20] + list[21]), "33333333")) * 0.01, 2);
            data.put("WSV", WSV)

            def VOL = convertUtils.hexToDecimal(convertUtils.bitwiseAnd(convertUtils.hexSubHex(list[24], "33"), "FF")) * 15.37
            data.put("VOL", VOL)

            def data1 = list[22] + list[23]
            def status = convertUtils.hexString2binaryString(convertUtils.padLeft(convertUtils.hexSubHex(convertUtils.hexTransfer(data1), "3333"), 4))
            List<String> statusList = convertUtils.split(status, 1);
            data.put("FORCED", statusList.get(9))
            data.put("DISTURB", statusList.get(10))
            data.put("OVERDRAFT", statusList.get(11))
            data.put("ALARM", statusList.get(12))
            data.put("VOL_STATUS", statusList.get(13))
            data.put("VALVE", statusList.get(14) + statusList.get(15))

        } else if (rawData.toString().length() == 54) {
            //188协议
            List<String> list = convertUtils.split(rawData.toString(), 2);

            def WSV = convertUtils.reserveDecimal(Integer.parseInt(convertUtils.hexTransfer(list[16] + list[17] + list[18] + list[19])) * 0.01, 2);
            data.put("WSV", WSV)

            def VOL = convertUtils.reserveDecimal(Integer.parseInt(convertUtils.hexTransfer(list[23]), 16) * 15.37, 2);
            data.put("VOL", VOL)

            def data1 = list[21]
            def status = convertUtils.hexString2binaryString(data1)
            List<String> statusList = convertUtils.split(status, 1);
            data.put("FORCED", statusList.get(3))
            data.put("DISTURB", statusList.get(4))
            data.put("VOL_STATUS", statusList.get(5))
            data.put("VALVE", statusList.get(6) + statusList.get(7))

        } else {
            throw new Exception("Incorrect length");
        }

        return JsonOutput.toJson(data)
    } catch (Exception e) {
        jsonObj.put("message", e.getMessage())
        return JsonOutput.toJson(jsonObj);
    }
}
