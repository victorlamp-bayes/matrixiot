package com.victorlamp.matrixiot.service.system.controller.tenant.vo.packages;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 租户套餐创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPackageCreateReqVO extends TenantPackageBaseVO {

}
