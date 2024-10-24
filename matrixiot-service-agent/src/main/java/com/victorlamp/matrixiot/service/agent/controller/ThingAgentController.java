package com.victorlamp.matrixiot.service.agent.controller;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.service.agent.dto.ReplyInvokeThingServiceRequestDTO;
import com.victorlamp.matrixiot.service.agent.dto.ReplySetThingPropertyRequestDTO;
import com.victorlamp.matrixiot.service.agent.service.ThingAgentService;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;

@OpenAPIDefinition
@Tag(name = "IoT - 设备代理服务")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/agent", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ThingAgentController {
    private final ThingAgentService thingAgentService;

    @Operation(summary = "上报设备属性/事件")
    @PostMapping("property-event/{thingId}")
    CommonResult<Boolean> postThingPropertyEvent(@PathVariable("thingId") @IdHex24 String thingId,
                                                 @RequestBody String payload) {
        thingAgentService.postThingPropertyEvent(thingId, payload);
        return success(true);
    }

    @Operation(summary = "回复设置属性")
    @PostMapping("property-set/reply/{thingId}")
    CommonResult<Boolean> replySetThingProperty(@PathVariable("thingId") @IdHex24 String thingId,
                                                @RequestBody @Valid ReplySetThingPropertyRequestDTO requestDTO) {
        thingAgentService.replySetThingProperty(thingId, requestDTO);
        return success(true);
    }

    @Operation(summary = "回复服务调用")
    @PostMapping("service-invoke/reply/{thingId}")
    CommonResult<Boolean> replyInvokeThingService(@PathVariable("thingId") @IdHex24 String thingId,
                                                  @RequestBody @Valid ReplyInvokeThingServiceRequestDTO requestDTO) {
        thingAgentService.replyInvokeThingService(thingId, requestDTO);
        return success(true);
    }

    @Operation(summary = "三方设备上报数据")
    @PostMapping("third-device/post")
    CommonResult<Boolean> postOriginData(@RequestBody String deviceData) {
        thingAgentService.postOriginData(deviceData);
        return success(true);
    }

    @Operation(summary = "AEP设备命令响应回调")
    @PostMapping("third-device/send/reply")
    CommonResult<Boolean> replyCommandResponse(@RequestBody String message) {
        thingAgentService.replyCommandResponse(message);
        return success(true);
    }

    @Operation(summary = "OC设备命令响应回调")
    @PostMapping("service/reply")
    CommonResult<Boolean> replyService(@RequestBody String message) {
        thingAgentService.replyService(message);
        return success(true);
    }

    @Operation(summary = "OneNET设备上报验证")
    @GetMapping("third-device/post")
    public CommonResult<String> checkDeviceDataChangedNotify(@RequestParam(value = "msg") String msg) {
        return success(msg);
    }
}
