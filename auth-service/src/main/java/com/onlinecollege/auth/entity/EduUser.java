package com.onlinecollege.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户（运管账号）
 */
@Data
@TableName("edu_user")
public class EduUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 登录名 */
    private String username;

    /** BCrypt 加密后的密码 */
    private String password;

    /** 昵称 / 显示名 */
    @TableField("nick_name")
    private String nickName;

    /** 角色，逗号分隔：ADMIN / EDITOR / VIEWER */
    private String roles;

    /** 头像 URL */
    private String avatar;

    /** 1=启用 0=禁用 */
    private Integer enabled;

    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
