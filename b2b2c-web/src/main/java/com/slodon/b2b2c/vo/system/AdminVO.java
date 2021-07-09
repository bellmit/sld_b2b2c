package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.core.constant.AdminConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.system.pojo.Admin;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装操作员VO对象
 * @Author wuxy
 */
@Data
public class AdminVO implements Serializable {

    private static final long serialVersionUID = -1218073469981164572L;
    @ApiModelProperty("管理员id")
    private Integer adminId;

    @ApiModelProperty("登录名")
    private String adminName;

    @ApiModelProperty("角色id")
    private Integer roleId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("是否超级管理员：1-是；0-否")
    private Integer isSuper;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("状态：1-正常；2-冻结")
    private Integer state;

    @ApiModelProperty("状态值：1-正常；2-冻结")
    private String stateValue;

    public AdminVO(Admin admin) {
        adminId = admin.getAdminId();
        adminName = admin.getAdminName();
        roleId = admin.getRoleId();
        roleName = admin.getRoleName();
        createTime = admin.getCreateTime();
        isSuper = admin.getIsSuper();
        phone = CommonUtil.dealMobile(admin.getPhone());
        email = admin.getEmail();
        state = admin.getState();
        stateValue = dealStateValue(admin.getState());
    }

    public static String dealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        switch (state) {
            case AdminConst.ADMIN_STATE_NORM:
                value = "正常";
                break;
            case AdminConst.ADMIN_STATE_FREEZE:
                value = "冻结";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
