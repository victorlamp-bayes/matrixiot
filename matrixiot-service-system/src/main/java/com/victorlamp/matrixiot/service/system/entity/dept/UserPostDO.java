package com.victorlamp.matrixiot.service.system.entity.dept;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.victorlamp.matrixiot.service.framework.mybatis.core.dataobject.BaseDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 用户和岗位关联
 *
 * @author ruoyi
 */
@TableName("system_user_post")
@KeySequence("system_user_post_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserPostDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = -6475066688766342280L;

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户 ID
     * <p>
     * 关联 {@link AdminUserDO#getId()}
     */
    private Long userId;
    /**
     * 角色 ID
     * <p>
     * 关联 {@link PostDO#getId()}
     */
    private Long postId;

}
