package com.victorlamp.matrixiot.service.management.controller.thingmodel;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.common.validation.groups.Create;
import com.victorlamp.matrixiot.service.common.validation.groups.Patch;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.*;
import com.victorlamp.matrixiot.service.management.enums.ThingModelServiceParamDirectionEnum;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@OpenAPIDefinition
@Tag(name = "设备管理 - 物模型")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/thing-model", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ThingModelController {

    @Resource
    private com.victorlamp.matrixiot.service.management.api.ThingModelService thingModelService;

    //*** 物模型 ***//

    @Operation(summary = "更新物模型", description = "物模型只能被整体更新，需要传入完整参数，整体替换原值")
    @PutMapping("update")
    CommonResult<Boolean> updateThingModel(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestBody @Valid ThingModel thingModel) {
        thingModelService.updateThingModel(productId, thingModel);
        return success(true);
    }

    @Operation(summary = "获得物模型详情")
    @GetMapping("describe")
    CommonResult<ThingModel> describeThingModel(@RequestParam("productId") @IdHex24 @NotBlank String productId) {
        return success(thingModelService.describeThingModel(productId));
    }

    //*** 物模型 - 脚本 ***//

    @Operation(summary = "创建/更新解析脚本")
    @PutMapping("script/update")
    CommonResult<Boolean> updateThingModelScript(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestBody @Valid ThingModelScript script) {
        thingModelService.updateThingModelScript(productId, script);
        return success(true);
    }

    @Operation(summary = "描述数据解析脚本")
    @GetMapping("script/describe")
    CommonResult<ThingModelScript> describeThingModelScript(@RequestParam("productId") @IdHex24 @NotBlank String productId) {
        return success(thingModelService.describeThingModelScript(productId));
    }

    @Operation(summary = "获取预置脚本列表")
    @GetMapping("script/list-preset")
    CommonResult<List<PresetScript>> listPresetScript() {
        return success(thingModelService.listPresetScript());
    }

    //*** 物模型 - 属性 ***//

    @Operation(summary = "创建/新增物模型属性")
    @PostMapping("property/create")
    @Validated(Create.class)
    CommonResult<Boolean> createThingModelProperty(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestBody @Valid ThingModelProperty property) {
        thingModelService.createThingModelProperty(productId, property);
        return success(true);
    }

    @Operation(summary = "更新物模型属性")
    @PutMapping("property/update")
    @Validated(Patch.class)
    CommonResult<Boolean> updateThingModelProperty(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier, @RequestBody @Valid ThingModelProperty property) {
        thingModelService.updateThingModelProperty(productId, identifier, property);
        return success(true);
    }

    @Operation(summary = "删除物模型属性")
    @DeleteMapping("property/delete")
    CommonResult<Boolean> deleteThingModelProperty(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier) {
        thingModelService.deleteThingModelProperty(productId, identifier);
        return success(true);
    }

    @Operation(summary = "描述物模型属性")
    @GetMapping("property/describe")
    CommonResult<ThingModelProperty> describeThingModelProperty(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier) {
        return success(thingModelService.describeThingModelProperty(productId, identifier));
    }

    @Operation(summary = "列出物模型属性")
    @GetMapping("property/list")
    CommonResult<List<ThingModelProperty>> listThingModelProperties(@RequestParam("productId") @IdHex24 @NotBlank String productId) {
        return success(thingModelService.listThingModelProperty(productId));
    }

    //*** 物模型 - 事件 ***//

    @Operation(summary = "创建/新增物模型事件")
    @PostMapping("event/create")
    CommonResult<Boolean> createThingModelEvent(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestBody @Valid ThingModelEvent event) {
        thingModelService.createThingModelEvent(productId, event);
        return success(true);
    }

    @Operation(summary = "更新物模型事件")
    @PutMapping("event/update")
    CommonResult<Boolean> updateThingModelEvent(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier, @RequestBody @Valid ThingModelEvent event) {
        thingModelService.updateThingModelEvent(productId, identifier, event);
        return success(true);
    }

    @Operation(summary = "删除物模型事件")
    @DeleteMapping("event/delete")
    CommonResult<Boolean> deleteThingModelEvent(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier) {
        thingModelService.deleteThingModelEvent(productId, identifier);
        return success(true);
    }

    @Operation(summary = "描述物模型事件")
    @GetMapping("event/describe")
    CommonResult<ThingModelEvent> describeThingModelEvent(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier) {
        return success(thingModelService.describeThingModelEvent(productId, identifier));
    }

    @Operation(summary = "列表物模型事件")
    @GetMapping("event/list")
    CommonResult<List<ThingModelEvent>> listThingModelEvents(@RequestParam("productId") @IdHex24 @NotBlank String productId) {
        return success(thingModelService.listThingModelEvent(productId));
    }

    //*** 物模型 - 服务 ***//

    @Operation(summary = "创建/新增物模型服务")
    @PostMapping("service/create")
    CommonResult<Boolean> createThingModelService(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestBody @Valid ThingModelService service) {
        thingModelService.createThingModelService(productId, service);
        return success(true);
    }

    @Operation(summary = "更新物模型服务")
    @PutMapping("service/update")
    CommonResult<Boolean> updateThingModelService(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier, @RequestBody @Valid ThingModelService service) {
        thingModelService.updateThingModelService(productId, identifier, service);
        return success(true);
    }

    @Operation(summary = "删除物模型服务")
    @DeleteMapping("service/delete")
    CommonResult<Boolean> deleteThingModelService(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier) {
        thingModelService.deleteThingModelService(productId, identifier);
        return success(true);
    }

    @Operation(summary = "描述物模型服务")
    @GetMapping("service/describe")
    CommonResult<ThingModelService> describeThingModelService(@RequestParam("productId") @IdHex24 @NotBlank String productId, @RequestParam("identifier") String identifier) {
        return success(thingModelService.describeThingModelService(productId, identifier));
    }

    @Operation(summary = "列出物模型服务")
    @GetMapping("service/list")
    CommonResult<List<ThingModelService>> listThingModelServices(
            @RequestParam("productId") @IdHex24 @NotBlank String productId) {
        return success(thingModelService.listThingModelService(productId));
    }

    //*** 物模型 - 服务参数 ***//

    @Operation(summary = "创建/新增物模型服务参数")
    @PostMapping("service-param/create")
    CommonResult<Boolean> createThingModelServiceParameter(@RequestParam("productId") @IdHex24 @NotBlank String productId,
                                                           @RequestParam("serviceIdentifier") @NotBlank String serviceIdentifier,
                                                           @RequestBody @Valid ThingModelServiceParam serviceParam,
                                                           @RequestParam("paramDirection") ThingModelServiceParamDirectionEnum paramDirection) {
        thingModelService.createThingModelServiceParam(productId, serviceIdentifier, serviceParam, paramDirection);
        return success(true);
    }

    @Operation(summary = "更新物模型服务参数")
    @PatchMapping("service-param/update")
    CommonResult<Boolean> updateThingModelServiceParameter(@RequestParam("productId") @IdHex24 @NotBlank String productId,
                                                           @RequestParam("serviceIdentifier") @NotBlank String serviceIdentifier,
                                                           @RequestParam("paramIdentifier") @NotBlank String paramIdentifier,
                                                           @RequestBody @Valid ThingModelServiceParam serviceParam,
                                                           @RequestParam("paramDirection") ThingModelServiceParamDirectionEnum paramDirection) {
        thingModelService.updateThingModelServiceParam(productId, serviceIdentifier, paramIdentifier, serviceParam, paramDirection);
        return success(true);
    }

    @Operation(summary = "删除物模型服务参数")
    @DeleteMapping("service-param/delete")
    CommonResult<Boolean> deleteThingModelServiceParameter(@RequestParam("productId") @IdHex24 @NotBlank String productId,
                                                           @RequestParam("serviceIdentifier") @NotBlank String serviceIdentifier,
                                                           @RequestParam("paramIdentifier") @NotBlank String paramIdentifier,
                                                           @RequestParam("paramDirection") ThingModelServiceParamDirectionEnum paramDirection) {
        thingModelService.deleteThingModelServiceParam(productId, serviceIdentifier, paramIdentifier, paramDirection);
        return success(true);
    }

    @Operation(summary = "描述物模型服务参数")
    @GetMapping("service-param/describe")
    CommonResult<ThingModelServiceParam> describeThingModelServiceParameter(@RequestParam("productId") @IdHex24 @NotBlank String productId,
                                                                            @RequestParam("serviceIdentifier") @NotBlank String serviceIdentifier,
                                                                            @RequestParam("paramIdentifier") @NotBlank String paramIdentifier,
                                                                            @RequestParam("paramDirection") ThingModelServiceParamDirectionEnum paramDirection) {
        return success(thingModelService.describeThingModelServiceParam(productId, serviceIdentifier, paramIdentifier, paramDirection));
    }

    @Operation(summary = "列表物模型服务参数")
    @GetMapping("service-param/list")
    CommonResult<List<ThingModelServiceParam>> listThingModelServiceParameters(@RequestParam("productId") @IdHex24 @NotBlank String productId,
                                                                               @RequestParam("serviceIdentifier") @NotBlank String serviceIdentifier,
                                                                               @RequestParam("paramDirection") ThingModelServiceParamDirectionEnum paramDirection) {
        return success(thingModelService.listThingModelServiceParam(productId, serviceIdentifier, paramDirection));
    }
}
