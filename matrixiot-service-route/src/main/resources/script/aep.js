function transform(jsonString) {
    let data = JSON.parse(jsonString);
    let valveState = '';
    if (data.valveState === '0' || data.valveState === '00') {
        valveState = 'valve_status_01';
    } else if (data.valveState === '1' || data.valveState === '01') {
        valveState = 'valve_status_02';
    } else if (data.valveState === '2' || data.valveState === '02') {
        valveState = 'valve_status_04';
    } else {
        valveState = 'valve_status_03';
    }
    ;

    const otherParameters = {
        code: data.ElectronicNo
    };

    const pushData = {
        valveState: valveState,
        currentQuantity: parseFloat(data.currentQuantity),
        batteryQuantity: parseFloat(data.batteryQuantity),
        collectTime: data.collectTime,
        dayMaxQuantity: parseFloat(data.dayMaxQuantity),
        successTimes: parseInt(data.successTimes),
        RSPR: data.RSPR,
        failTimes: parseInt(data.failTimes),
        overCurrentWarning: data.overCurrentWarning,
        lowVoltageWarning: data.lowVoltageWarning,
        overCurrentQuantity: data.overCurrentQuantity,
        Version: data.Version,
        valveWaring: data.valveWaring,
        SNR: data.SNR,
        refluxWarningQuantity: data.refluxWarningQuantity,
        refluxWarning: data.refluxWarning,
        magneticInterWarning: data.magneticInterWarning,
        continuedHighTraffic: data.continuedHighTraffic
    };

    const obj = {
        otherParameters,
        pushData
    };

    return JSON.stringify({data: [obj]});
}