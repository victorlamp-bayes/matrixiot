package com.victorlamp.matrixiot.service.route.controller;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.route.DataRouteService;
import com.victorlamp.matrixiot.service.route.controller.vo.DataRouteCreateReqVO;
import com.victorlamp.matrixiot.service.route.controller.vo.DataRouteUpdateStatusReqVO;
import com.victorlamp.matrixiot.service.route.convert.DataRouteCovert;
import com.victorlamp.matrixiot.service.route.dto.DataRoutePageReqDTO;
import com.victorlamp.matrixiot.service.route.dto.DataRouteUpdateReqDTO;
import com.victorlamp.matrixiot.service.route.entity.DataRoute;
import com.victorlamp.matrixiot.service.route.entity.DataSourceTopic;
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
@Tag(name = "Route", description = "数据路由")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/data-route", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class DataRouteController {

    @Resource
    private DataRouteService dataRouteService;

    @Operation(summary = "创建数据路由")
    @PostMapping("create")
    public CommonResult<DataRoute> createDataRoute(@RequestBody @Valid DataRouteCreateReqVO reqVO) {
        DataRoute dataRoute = dataRouteService.createDataRoute(DataRouteCovert.INSTANCE.toDTO(reqVO));
        return success(dataRoute);
    }

    @Operation(summary = "查询数据路由详情")
    @GetMapping("describe")
    public CommonResult<DataRoute> describeDataRoute(@RequestParam("id") @IdHex24 String id) {
        DataRoute dataRoute = dataRouteService.getDataRoute(id);
        return success(dataRoute);
    }

    @Operation(summary = "获取数据路由列表")
    @GetMapping("list")
    public CommonResult<PageResult<DataRoute>> listDataRoutes(DataRoutePageReqDTO reqDTO) {
        PageResult<DataRoute> dataRoutePageResult = dataRouteService.listDataRoutePage(reqDTO);
        return success(dataRoutePageResult);
    }

    @Operation(summary = "更新数据路由")
    @PatchMapping("update")
    public CommonResult<Boolean> updateDataRoute(@RequestParam("id") @IdHex24 @NotBlank String id, @RequestBody @Valid DataRouteUpdateReqDTO reqDTO) {
        dataRouteService.updateDataRoute(id, reqDTO);
        return success(true);
    }

    @Operation(summary = "更新数据路由状态")
    @PatchMapping("update-status")
    public CommonResult<Boolean> updateDataRouteStatus(@RequestBody @Valid DataRouteUpdateStatusReqVO reqVO) {
        dataRouteService.updateDataRouteStatus(DataRouteCovert.INSTANCE.toDTO(reqVO));
        return success(true);
    }

    @Operation(summary = "删除数据路由")
    @DeleteMapping("delete")
    public CommonResult<Boolean> deleteDataRoute(@RequestParam("id") @IdHex24 String id) {
        dataRouteService.deleteDataRoute(id);
        return success(true);
    }

    @Operation(summary = "获取数据源topic列表")
    @GetMapping("source/topic/list")
    public CommonResult<List<DataSourceTopic>> listDataSourceTopic() {
        return success(dataRouteService.listDataSourceTopic());
    }
}
