package com.victorlamp.matrixiot.service.system.convert.permission;

import com.victorlamp.matrixiot.service.system.controller.permission.vo.role.RoleCreateReqVO;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.role.RoleRespVO;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.role.RoleSimpleRespVO;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.role.RoleUpdateReqVO;
import com.victorlamp.matrixiot.service.system.entity.permission.RoleDO;
import com.victorlamp.matrixiot.service.system.service.permission.bo.RoleCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RoleConvert {

    RoleConvert INSTANCE = Mappers.getMapper(RoleConvert.class);

    RoleDO convert(RoleUpdateReqVO bean);

    RoleRespVO convert(RoleDO bean);

    RoleDO convert(RoleCreateReqVO bean);

    List<RoleSimpleRespVO> convertList02(List<RoleDO> list);

    RoleDO convert(RoleCreateReqBO bean);

}
