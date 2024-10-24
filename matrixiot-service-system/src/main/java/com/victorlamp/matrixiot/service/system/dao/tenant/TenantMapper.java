package com.victorlamp.matrixiot.service.system.dao.tenant;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.framework.mybatis.core.mapper.BaseMapperX;
import com.victorlamp.matrixiot.service.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.tenant.TenantExportReqVO;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.tenant.TenantPageReqVO;
import com.victorlamp.matrixiot.service.system.entity.tenant.TenantDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 租户 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantMapper extends BaseMapperX<TenantDO> {

    default PageResult<TenantDO> selectPage(TenantPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TenantDO>()
                .likeIfPresent(TenantDO::getName, reqVO.getName())
                .likeIfPresent(TenantDO::getContactName, reqVO.getContactName())
                .likeIfPresent(TenantDO::getContactMobile, reqVO.getContactMobile())
                .eqIfPresent(TenantDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(TenantDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TenantDO::getId));
    }

    default List<TenantDO> selectList(TenantExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<TenantDO>()
                .likeIfPresent(TenantDO::getName, reqVO.getName())
                .likeIfPresent(TenantDO::getContactName, reqVO.getContactName())
                .likeIfPresent(TenantDO::getContactMobile, reqVO.getContactMobile())
                .eqIfPresent(TenantDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(TenantDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TenantDO::getId));
    }

    default TenantDO selectByName(String name) {
        return selectOne(TenantDO::getName, name);
    }

    default TenantDO selectByWebsite(String website) {
        return selectOne(TenantDO::getWebsite, website);
    }

    default Long selectCountByPackageId(Long packageId) {
        return selectCount(TenantDO::getPackageId, packageId);
    }

    default List<TenantDO> selectListByPackageId(Long packageId) {
        return selectList(TenantDO::getPackageId, packageId);
    }
}
