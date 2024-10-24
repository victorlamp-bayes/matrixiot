package com.victorlamp.matrixiot.service.system.controller.oauth2;

import cn.hutool.core.collection.CollUtil;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.util.object.BeanUtils;
import com.victorlamp.matrixiot.service.system.api.dept.DeptService;
import com.victorlamp.matrixiot.service.system.api.dept.PostService;
import com.victorlamp.matrixiot.service.system.api.user.AdminUserService;
import com.victorlamp.matrixiot.service.system.controller.oauth2.vo.user.OAuth2UserInfoRespVO;
import com.victorlamp.matrixiot.service.system.controller.oauth2.vo.user.OAuth2UserUpdateReqVO;
import com.victorlamp.matrixiot.service.system.controller.user.vo.profile.UserProfileUpdateReqVO;
import com.victorlamp.matrixiot.service.system.entity.dept.DeptDO;
import com.victorlamp.matrixiot.service.system.entity.dept.PostDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;
import static com.victorlamp.matrixiot.service.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 提供给外部应用调用为主
 * <p>
 * 1. 在 getUserInfo 方法上，添加//@PreAuthorize("@ss.hasScope('user.read')") 注解，声明需要满足 scope = user.read
 * 2. 在 updateUserInfo 方法上，添加//@PreAuthorize("@ss.hasScope('user.write')") 注解，声明需要满足 scope = user.write
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - OAuth2.0 用户")
@RestController
@RequestMapping("/api/v1/system/oauth2/user")
@Validated
@Slf4j
public class OAuth2UserController {

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;

    @GetMapping("/query")
    @Operation(summary = "获得用户基本信息")
    //@PreAuthorize("@ss.hasScope('user.read')") //
    public CommonResult<OAuth2UserInfoRespVO> getUserInfo() {
        // 获得用户基本信息
        AdminUserDO user = adminUserService.getUser(getLoginUserId());
        OAuth2UserInfoRespVO resp = BeanUtils.toBean(user, OAuth2UserInfoRespVO.class);
        // 获得部门信息
        if (user.getDeptId() != null) {
            DeptDO dept = deptService.getDept(user.getDeptId());
            resp.setDept(BeanUtils.toBean(dept, OAuth2UserInfoRespVO.Dept.class));
        }
        // 获得岗位信息
        if (CollUtil.isNotEmpty(user.getPostIds())) {
            List<PostDO> posts = postService.getPostList(user.getPostIds());
            resp.setPosts(BeanUtils.toBean(posts, OAuth2UserInfoRespVO.Post.class));
        }
        return success(resp);
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户基本信息")
    //@PreAuthorize("@ss.hasScope('user.write')")
    public CommonResult<Boolean> updateUserInfo(@Valid @RequestBody OAuth2UserUpdateReqVO reqVO) {
        // 这里将 UserProfileUpdateReqVO =》UserProfileUpdateReqVO 对象，实现接口的复用。
        // 主要是，AdminUserService 没有自己的 BO 对象，所以复用只能这么做
        adminUserService.updateUserProfile(getLoginUserId(), BeanUtils.toBean(reqVO, UserProfileUpdateReqVO.class));
        return success(true);
    }

}
