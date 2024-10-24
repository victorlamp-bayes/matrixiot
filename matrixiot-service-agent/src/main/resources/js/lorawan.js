function rawDataToProtocol(rawData) {
    const data = {};
    if (rawData.length === 56) {
        const wsv = ((parseInt(rawData.slice(43, 45) + rawData.slice(41, 43) + rawData.slice(39, 41) + rawData.slice(37, 39), 16) - parseInt("33333333", 16)) * 0.01).toFixed(2);
        data.put("WSV", wsv);
        const vol = ((parseInt(rawData.slice(49, 51), 16) - parseInt("33", 16)) & 0xFF) * 15.37;
        data.put("VOL", vol.toFixed(2));
        const status = (parseInt(rawData.slice(47, 49) + rawData.slice(45, 47), 16) - parseInt("3333", 16)).toString(2).padStart(16, "0");
        data.put("FORCED", status.at(9));
        data.put("DISTURB", status.at(10));
        data.put("OVERDRAFT", status.at(11));
        data.put("ALARM", status.at(12));
        data.put("VOL_STATUS", status.at(13));
        data.put("VALVE", status.slice(14, 16));
        return JSON.stringify(data);
    }

    if (rawData.length === 54) {
        const wsv = ((parseInt(rawData.slice(39, 41) + rawData.slice(37, 39) + rawData.slice(35, 37) + rawData.slice(33, 35), 16) - parseInt("33333333", 16)) * 0.01).toFixed(2);
        data.put("WSV", wsv);
        const vol = ((parseInt(rawData.slice(47, 49), 16) & 0xFF) * 15.37).toFixed(2);
        data.put("VOL", vol);
        const status = parseInt(rawData.slice(43, 45)).toString(2).padStart(8, "0");
        data.put("FORCED", status.at(3));
        data.put("DISTURB", status.at(4));
        data.put("VOL_STATUS", status.at(5));
        data.put("VALVE", status.slice(6, 8));
        return JSON.stringify(data);
    }

    return JSON.stringify({"message": "Incorrect length"});
}

function protocolToRawData() {
}