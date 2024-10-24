package com.victorlamp.matrixiot.service.system.controller.user;

import cn.hutool.core.collection.CollUtil;
import com.victorlamp.matrixiot.common.enums.CommonStatusEnum;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.system.api.dept.DeptService;
import com.victorlamp.matrixiot.service.system.api.user.AdminUserService;
import com.victorlamp.matrixiot.service.system.controller.user.vo.user.*;
import com.victorlamp.matrixiot.service.system.convert.user.UserConvert;
import com.victorlamp.matrixiot.service.system.entity.dept.DeptDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;
import static com.victorlamp.matrixiot.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - 用户")
@RestController
@RequestMapping("/api/v1/system/user")
@Validated
@Slf4j
public class UserController {

    @Resource
    private AdminUserService userService;
    @Resource
    private DeptService deptService;

    @PostMapping("/create")
    @Operation(summary = "新增用户")
    //@PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserCreateReqVO reqVO) {
        Long id = userService.createUser(reqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户")
    //@PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserUpdateReqVO reqVO) {
        userService.updateUser(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    //@PreAuthorize("@ss.hasPermission('system:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "重置用户密码")
    //@PreAuthorize("@ss.hasPermission('system:user:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(reqVO.getId(), reqVO.getPassword());
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "修改用户状态")
    //@PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
        userService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获得用户分页列表")
    //@PreAuthorize("@ss.hasPermission('system:user:list')")
    public CommonResult<PageResult<UserPageItemRespVO>> getUserPage(@Valid UserPageReqVO reqVO) {
        // 获得用户分页列表
        PageResult<AdminUserDO> pageResult = userService.getUserPage(reqVO);

        if (CollUtil.isEmpty(pageResult.getList())) {
            log.info(String.valueOf(pageResult.getTotal()));
            return success(new PageResult<>(pageResult.getTotal())); // 返回空
        }

        // 获得拼接需要的数据
        Collection<Long> deptIds = convertList(pageResult.getList(), AdminUserDO::getDeptId);
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(deptIds);
        // 拼接结果返回
        List<UserPageItemRespVO> userList = new ArrayList<>(pageResult.getList().size());
        pageResult.getList().forEach(user -> {
            UserPageItemRespVO respVO = UserConvert.INSTANCE.convert(user);
            respVO.setDept(UserConvert.INSTANCE.convert(deptMap.get(user.getDeptId())));
            userList.add(respVO);
        });
        return success(new PageResult<>(userList, pageResult.getTotal(), pageResult.getPageNo(), pageResult.getPageSize()));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取用户精简信息列表", description = "只包含被开启的用户，主要用于前端的下拉选项")
    public CommonResult<List<UserSimpleRespVO>> getSimpleUserList() {
        // 获用户列表，只要开启状态的
        List<AdminUserDO> list = userService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus());
        // 排序后，返回给前端
        return success(UserConvert.INSTANCE.convertList04(list));
    }

    @GetMapping("/query")
    @Operation(summary = "获得用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    //@PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<UserRespVO> getUser(@RequestParam("id") Long id) {
        AdminUserDO user = userService.getUser(id);
        // 获得部门数据
        DeptDO dept = deptService.getDept(user.getDeptId());

        return success(UserConvert.INSTANCE.convert(user).setDept(UserConvert.INSTANCE.convert(dept)));
    }

}
