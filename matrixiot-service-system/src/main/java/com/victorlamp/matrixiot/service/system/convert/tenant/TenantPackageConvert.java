package com.victorlamp.matrixiot.service.system.convert.tenant;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.packages.TenantPackageCreateReqVO;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.packages.TenantPackageRespVO;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.packages.TenantPackageSimpleRespVO;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.packages.TenantPackageUpdateReqVO;
import com.victorlamp.matrixiot.service.system.entity.tenant.TenantPackageDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 租户套餐 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantPackageConvert {

    TenantPackageConvert INSTANCE = Mappers.getMapper(TenantPackageConvert.class);

    TenantPackageDO convert(TenantPackageCreateReqVO bean);

    TenantPackageDO convert(TenantPackageUpdateReqVO bean);

    TenantPackageRespVO convert(TenantPackageDO bean);

    List<TenantPackageRespVO> convertList(List<TenantPackageDO> list);

    PageResult<TenantPackageRespVO> convertPage(PageResult<TenantPackageDO> page);

    List<TenantPackageSimpleRespVO> convertList02(List<TenantPackageDO> list);

}
