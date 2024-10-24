package com.victorlamp.matrixiot.service.management.api;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingEventDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingPropertyDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.thingdata.ThingServiceDataPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingPropertyData;

import java.util.List;

public interface ThingDataService {

//    List<ThingPropertyData> listThingPropertyData(String thingId, ThingPropertyDataPageReqDTO reqDTO);

    ThingData createThingData(ThingData thingData);

    List<ThingPropertyData> getLatestThingPropertyData(String thingId);

    PageResult<ThingData> listThingDataPage(ThingDataPageReqDTO thingId);

    PageResult<ThingData> listThingPropertyDataPage(ThingPropertyDataPageReqDTO reqDTO);

    PageResult<ThingData> listThingEventDataPage(ThingEventDataPageReqDTO reqDTO);

    PageResult<ThingData> listThingServiceDataPage(ThingServiceDataPageReqDTO reqDTO);

    Boolean isThingDataDuplicated(ThingData thingData);

//    PaginationResponseDTO<ThingData.Command> listThingCommandData(
//            String thingId,
//            String status,
//            Long startTime,
//            Long endTime,
//            Integer pageSize,
//            Integer pageNum);

    ThingData createServiceData(ThingData thingData);

    void updateServiceData(String thingId, ThingData thingData);

    ThingData getThingDataByThingIdAndCommandId(String thingId, String commandId);
}
