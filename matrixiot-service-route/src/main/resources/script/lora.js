function transform(jsonString) {
    let data = JSON.parse(jsonString);
    let valveState = '';
    if (data.VALVE === '0' || data.VALVE === '00') {
        valveState = 'valve_status_01';
    } else if (data.VALVE === '1' || data.VALVE === '01') {
        valveState = 'valve_status_02';
    } else if (data.VALVE === '2' || data.VALVE === '02') {
        valveState = 'valve_status_04';
    } else {
        valveState = 'valve_status_03';
    }
    ;

    const otherParameters = {
        code: data.electronicNo
    };

    const pushData = {
        valveState: valveState,
        currentQuantity: parseFloat(data.WSV),
        batteryQuantity: parseFloat(data.VOL),
        collectTime: data.timestamp,
        RSPR: 0,
        SNR: 0,
        dayMaxQuantity: 0,
        successTimes: 0,
        failTimes: 0,
        overCurrentWarning: 0,
        lowVoltageWarning: 0,
        overCurrentQuantity: 0,
        Version: "",
        valveWaring: 0,
        refluxWarningQuantity: 0,
        refluxWarning: 0,
        magneticInterWarning: 0,
        continuedHighTraffic: 0
    };

    const obj = {
        otherParameters,
        pushData
    };

    return JSON.stringify({data: [obj]});
}