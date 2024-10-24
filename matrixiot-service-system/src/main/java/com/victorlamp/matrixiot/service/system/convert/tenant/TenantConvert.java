package com.victorlamp.matrixiot.service.system.convert.tenant;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.tenant.TenantCreateReqVO;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.tenant.TenantRespVO;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.tenant.TenantSimpleRespVO;
import com.victorlamp.matrixiot.service.system.controller.tenant.vo.tenant.TenantUpdateReqVO;
import com.victorlamp.matrixiot.service.system.controller.user.vo.user.UserCreateReqVO;
import com.victorlamp.matrixiot.service.system.entity.tenant.TenantDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 租户 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantConvert {

    TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

    TenantDO convert(TenantCreateReqVO bean);

    TenantDO convert(TenantUpdateReqVO bean);

    TenantRespVO convert(TenantDO bean);

    TenantSimpleRespVO convert03(TenantDO bean);

    List<TenantRespVO> convertList(List<TenantDO> list);

    PageResult<TenantRespVO> convertPage(PageResult<TenantDO> page);

    default UserCreateReqVO convert02(TenantCreateReqVO bean) {
        UserCreateReqVO reqVO = new UserCreateReqVO();
        reqVO.setUsername(bean.getUsername());
        reqVO.setPassword(bean.getPassword());
        reqVO.setNickname(bean.getContactName()).setMobile(bean.getContactMobile());
        return reqVO;
    }

}
