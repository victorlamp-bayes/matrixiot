package com.victorlamp.matrixiot.service.route.convert;

import com.victorlamp.matrixiot.service.route.controller.vo.DataRouteCreateReqVO;
import com.victorlamp.matrixiot.service.route.controller.vo.DataRouteUpdateStatusReqVO;
import com.victorlamp.matrixiot.service.route.dto.*;
import com.victorlamp.matrixiot.service.route.entity.DataRoute;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataRouteCovert {

    DataRouteCovert INSTANCE = Mappers.getMapper(DataRouteCovert.class);

    DataRouteCreateReqDTO toDTO(DataRouteCreateReqVO reqVO);

    DataRouteUpdateStatusReqDTO toDTO(DataRouteUpdateStatusReqVO reqVO);

    DataRoute toEntity(DataRouteCreateReqDTO reqDTO);
}
