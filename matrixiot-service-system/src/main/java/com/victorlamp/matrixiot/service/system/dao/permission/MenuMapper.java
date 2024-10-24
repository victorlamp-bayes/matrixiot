package com.victorlamp.matrixiot.service.system.dao.permission;

import com.victorlamp.matrixiot.service.framework.mybatis.core.mapper.BaseMapperX;
import com.victorlamp.matrixiot.service.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.victorlamp.matrixiot.service.system.controller.permission.vo.menu.MenuListReqVO;
import com.victorlamp.matrixiot.service.system.entity.permission.MenuDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapperX<MenuDO> {

    default MenuDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(MenuDO::getParentId, parentId, MenuDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(MenuDO::getParentId, parentId);
    }

    default List<MenuDO> selectList(MenuListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<MenuDO>()
                .likeIfPresent(MenuDO::getName, reqVO.getName())
                .eqIfPresent(MenuDO::getStatus, reqVO.getStatus()));
    }

    default List<MenuDO> selectListByPermission(String permission) {
        return selectList(MenuDO::getPermission, permission);
    }
}
