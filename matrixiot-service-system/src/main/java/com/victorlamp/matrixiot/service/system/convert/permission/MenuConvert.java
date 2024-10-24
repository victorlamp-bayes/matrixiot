package com.victorlamp.matrixiot.service.system.convert.permission;

import com.victorlamp.matrixiot.service.system.controller.permission.vo.menu.MenuCreateReqVO;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.menu.MenuRespVO;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.menu.MenuSimpleRespVO;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.menu.MenuUpdateReqVO;
import com.victorlamp.matrixiot.service.system.entity.permission.MenuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MenuConvert {

    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);

    List<MenuRespVO> convertList(List<MenuDO> list);

    MenuDO convert(MenuCreateReqVO bean);

    MenuDO convert(MenuUpdateReqVO bean);

    MenuRespVO convert(MenuDO bean);

    List<MenuSimpleRespVO> convertList02(List<MenuDO> list);

}
