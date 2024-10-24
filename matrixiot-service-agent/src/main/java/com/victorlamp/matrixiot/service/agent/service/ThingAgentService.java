package com.victorlamp.matrixiot.service.agent.service;

import com.victorlamp.matrixiot.service.agent.dto.ReplyInvokeThingServiceRequestDTO;
import com.victorlamp.matrixiot.service.agent.dto.ReplySetThingPropertyRequestDTO;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingData;

public interface ThingAgentService {

    void postThingPropertyEvent(String thingId, String payload);

    void replySetThingProperty(String thingId, ReplySetThingPropertyRequestDTO requestDTO);

    void replyInvokeThingService(String thingId, ReplyInvokeThingServiceRequestDTO requestDTO);

    void postOriginData(String deviceData);

    void registerDeviceToExternal(Product product, Thing thing);

    void subscribeToExternal();

    void sendCommand(ThingData thingData);

    void replyCommandResponse(String message);

    void replyService(String message);
}
