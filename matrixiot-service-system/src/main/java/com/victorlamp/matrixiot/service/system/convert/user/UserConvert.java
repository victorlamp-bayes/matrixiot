package com.victorlamp.matrixiot.service.system.convert.user;

import com.victorlamp.matrixiot.service.system.controller.user.vo.profile.UserProfileRespVO;
import com.victorlamp.matrixiot.service.system.controller.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.victorlamp.matrixiot.service.system.controller.user.vo.profile.UserProfileUpdateReqVO;
import com.victorlamp.matrixiot.service.system.controller.user.vo.user.UserCreateReqVO;
import com.victorlamp.matrixiot.service.system.controller.user.vo.user.UserPageItemRespVO;
import com.victorlamp.matrixiot.service.system.controller.user.vo.user.UserSimpleRespVO;
import com.victorlamp.matrixiot.service.system.controller.user.vo.user.UserUpdateReqVO;
import com.victorlamp.matrixiot.service.system.dto.AdminUserRespDTO;
import com.victorlamp.matrixiot.service.system.entity.dept.DeptDO;
import com.victorlamp.matrixiot.service.system.entity.dept.PostDO;
import com.victorlamp.matrixiot.service.system.entity.permission.RoleDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    UserPageItemRespVO convert(AdminUserDO bean);

    UserPageItemRespVO.Dept convert(DeptDO bean);

    AdminUserDO convert(UserCreateReqVO bean);

    AdminUserDO convert(UserUpdateReqVO bean);

    UserProfileRespVO convert03(AdminUserDO bean);

    List<UserProfileRespVO.Role> convertList(List<RoleDO> list);

    UserProfileRespVO.Dept convert02(DeptDO bean);

    AdminUserDO convert(UserProfileUpdateReqVO bean);

    AdminUserDO convert(UserProfileUpdatePasswordReqVO bean);

    List<UserProfileRespVO.Post> convertList02(List<PostDO> list);

//    List<UserProfileRespVO.SocialUser> convertList03(List<SocialUserDO> list);

    List<UserSimpleRespVO> convertList04(List<AdminUserDO> list);

    AdminUserRespDTO convert4(AdminUserDO bean);

    List<AdminUserRespDTO> convertList4(List<AdminUserDO> users);

}
