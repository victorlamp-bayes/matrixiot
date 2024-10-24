package com.victorlamp.matrixiot.service.route;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.route.dto.*;
import com.victorlamp.matrixiot.service.route.entity.DataRoute;
import com.victorlamp.matrixiot.service.route.entity.DataSourceTopic;

import java.util.List;

public interface DataRouteService {

    DataRoute createDataRoute(DataRouteCreateReqDTO reqDTO);

    DataRoute getDataRoute(String id);

    List<DataRoute> listDataRoute(DataRouteListReqDTO reqDTO);

    PageResult<DataRoute> listDataRoutePage(DataRoutePageReqDTO reqDTO);

    void updateDataRoute(String id, DataRouteUpdateReqDTO reqDTO);

    void updateDataRouteStatus(DataRouteUpdateStatusReqDTO reqDTO);

    void deleteDataRoute(String id);

    List<DataSourceTopic> listDataSourceTopic();
}
