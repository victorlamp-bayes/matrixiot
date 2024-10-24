package com.victorlamp.matrixiot.service.management.api;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.dto.thing.*;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigItem;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingGeo;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thing.ThingStatistics;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;

import java.util.List;

public interface ThingService {

    Thing createThing(ThingCreateReqDTO reqDTO);

    Thing getThing(String id);

    void updateThing(String id, ThingUpdateReqDTO reqDTO);

    void updateThingOnlineStatus(String id, boolean online);

    void deleteThing(String id);

    List<Thing> listAll(String productId);

    PageResult<Thing> listThingPage(ThingPageReqDTO reqDTO);

    void importThings(List<ThingImportReqDTO> importThings, String productId, boolean updateSupport);

    void addSubThing(String gatewayId, String subThingId);

    void addAllSubThing(String gatewayId, String productId);

    void batchAddSubThing(String gatewayId, List<String> subThingId);

    void removeSubThing(String gatewayId, String subThingId);

    void batchRemoveSubThing(String gatewayId, List<String> subThingId);

    void removeAllSubThing(String gatewayId);

    PageResult<Thing> listSubThingPage(String gatewayId, SubThingPageReqDTO reqDTO);

    Boolean existsByProductId(String productId);

    Thing getThingByExternalConfigId(String deviceId);

    Thing getThingByExternalConfigDeviceNo(String deviceNo);

    Boolean existsByExternalConfigItem(String name, Object value);

    List<ExternalConfigItem> listExternalConfigItem(String type);

    ThingStatistics getStatistics(String productId);

    ThingGeo getAggregatedThingGeo(ThingGeoReqDTO reqDTO);

    void invokeThingService(ThingData thingData);

    void thirdInvokeThingService(InvokeServiceReqDTO reqDTO);

    /**
     * @param thingId:设备Id
     * @param propertiesDataDTO:必传参数: propertyCommand : Map<String,Object>
     *                                "command": {
     *                                "identifier1": "xxx", (property唯一标识 : value)
     *                                "identifier2": "xxx"
     *                                }
     */
//    void setThingProperties(String thingId, ThingData.Command propertiesDataDTO);

    /**
     * @param thingId:设备Id
     * @param requestDTO:  必传参数:
     *                     "identifier" ：service唯一标识;
     *                     "params": {
     *                     "paramIdentifier1": "xxx",  (param唯一标识 : value)
     *                     "paramIdentifier2": "xxx"
     *                     }
     */
//    void invokeThingService(String thingId, ThingData.Command.ServiceCommandDTO requestDTO);
}
