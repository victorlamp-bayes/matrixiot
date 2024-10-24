package com.victorlamp.matrixiot.service.management.api;

import com.victorlamp.matrixiot.service.management.entity.thingmodel.*;
import com.victorlamp.matrixiot.service.management.enums.ThingModelServiceParamDirectionEnum;

import java.util.List;

public interface ThingModelService {

    //*** 物模型 ***//
    void createThingModel(String productId);

    ThingModel describeThingModel(String productId);

    void updateThingModel(String productId, ThingModel thingModel);

    void deleteThingModel(String productId);

    //*** 物模型 - 脚本 ***//

    List<PresetScript> listPresetScript();

    ThingModelScript describeThingModelScript(String productId);

    void updateThingModelScript(String productId, ThingModelScript script);

    //*** 物模型 - 属性 ***//
    void createThingModelProperty(String productId, ThingModelProperty property);

    ThingModelProperty describeThingModelProperty(String productId, String identifier);

    List<ThingModelProperty> listThingModelProperty(String productId);

    void updateThingModelProperty(String productId, String identifier, ThingModelProperty property);

    void deleteThingModelProperty(String productId, String identifier);

    //*** 物模型 - 事件 ***//
    void createThingModelEvent(String productId, ThingModelEvent event);

    ThingModelEvent describeThingModelEvent(String productId, String identifier);

    List<ThingModelEvent> listThingModelEvent(String productId);

    void updateThingModelEvent(String productId, String identifier, ThingModelEvent event);

    void deleteThingModelEvent(String productId, String identifier);

    //*** 物模型 - 服务 ***//
    void createThingModelService(String productId, com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelService service);

    com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelService describeThingModelService(String productId, String identifier);

    List<com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelService> listThingModelService(String productId);

    void updateThingModelService(String productId, String identifier, com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelService service);

    void deleteThingModelService(String productId, String identifier);

    //*** 物模型 - 服务参数 ***//
    void createThingModelServiceParam(String productId, String serviceIdentifier, ThingModelServiceParam serviceParam, ThingModelServiceParamDirectionEnum paramDirection);

    ThingModelServiceParam describeThingModelServiceParam(String productId, String serviceIdentifier, String paramIdentifier, ThingModelServiceParamDirectionEnum paramDirection);

    List<ThingModelServiceParam> listThingModelServiceParam(String productId, String serviceIdentifier, ThingModelServiceParamDirectionEnum paramDirection);

    void updateThingModelServiceParam(String productId, String serviceIdentifier, String paramIdentifier, ThingModelServiceParam serviceParam, ThingModelServiceParamDirectionEnum paramDirection);

    void deleteThingModelServiceParam(String productId, String serviceIdentifier, String paramIdentifier, ThingModelServiceParamDirectionEnum paramDirection);

    /////////////////////////////////////////////////
//
//    ResponseDTO<ThingModelDTO> createThingModel(String productId, ThingModelRequestDTO requestDTO);
//
//    void updateThingModel(String productId, ThingModelRequestDTO requestDTO);
//
//    ResponseDTO<ThingModelDTO> describeThingModel(String productId);
//
//    void deleteThingModel(String productId, String identifiers);
//
//    void updateThingModelScript(String productId, ThingModelDTO.ScriptDTO requestDTO);
//
//    ResponseDTO<ThingModelDTO.ScriptDTO> describeThingModelScript(String productId);
//
//    ResponseDTO<List<GroovyFileDTO>> listPresetScripts();
//
//    void createThingModelProperty(String productId, ThingModelDTO.PropertyDTO requestDTO);
//
//    void updateThingModelProperty(String productId, String identifier, ThingModelDTO.PropertyDTO requestDTO);
//
//    void deleteThingModelProperty(String productId, String identifier);
//
//    ResponseDTO<ThingModelDTO.PropertyDTO> describeThingModelProperty(String productId, String identifier);
//
//    ResponseDTO<List<ThingModelDTO.PropertyDTO>> listThingModelProperties(String productId);
//
//    void createThingModelService(String productId, ThingModelDTO.ServiceDTO requestDTO);
//
//    void updateThingModelService(String productId, String identifier, ThingModelDTO.ServiceDTO requestDTO);
//
//    void deleteThingModelService(String productId, String identifier);
//
//    ResponseDTO<ThingModelDTO.ServiceDTO> describeThingModelService(String productId, String identifier);
//
//    ResponseDTO<List<ThingModelDTO.ServiceDTO>> listThingModelServices(String productId);
//
//    void createThingModelEvent(String productId, ThingModelDTO.EventDTO requestDTO);
//
//    void updateThingModelEvent(String productId, String identifier, ThingModelDTO.EventDTO requestDTO);
//
//    void deleteThingModelEvent(String productId, String identifier);
//
//    ResponseDTO<ThingModelDTO.EventDTO> describeThingModelEvent(String productId, String identifier);
//
//    ResponseDTO<List<ThingModelDTO.EventDTO>> listThingModelEvents(String productId);
//
//    void createThingModelServiceParameter(String productId, String identifier, ThingModelDTO.ServiceDTO.ParamDTO requestDTO);
//
//    void updateThingModelServiceParameter(String productId, String identifier, ThingModelDTO.ServiceDTO.ParamDTO requestDTO);
//
//    void deleteThingModelServiceParameter(String productId, String identifier, String paramIdentifier);
//
//    ResponseDTO<ThingModelDTO.ServiceDTO.ParamDTO> describeThingModelServiceParameter(String productId, String identifier, String paramIdentifier);
//
//    ResponseDTO<List<ThingModelDTO.ServiceDTO.ParamDTO>> listThingModelServiceParameters(String productId, String identifier);
}