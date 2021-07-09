package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VendorExample implements Serializable {
    private static final long serialVersionUID = -8306159072759384023L;

    /**
     * 用于编辑时的重复判断
     */
    private Long vendorIdNotEquals;

    /**
     * 用于批量操作
     */
    private String vendorIdIn;

    /**
     * 商户id
     */
    private Long vendorId;

    /**
     * 商户账号
     */
    private String vendorName;

    /**
     * 商户账号,用于模糊查询
     */
    private String vendorNameLike;

    /**
     * 商户密码
     */
    private String vendorPassword;

    /**
     * 商户手机
     */
    private String vendorMobile;

    /**
     * 商户邮件
     */
    private String vendorEmail;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 是否店铺管理员：0-否，1-是，每个店铺只有一个账号为1
     */
    private String isStoreAdmin;

    /**
     * 大于等于开始时间
     */
    private Date registerTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date registerTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date latestLoginTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date latestLoginTimeBefore;

    /**
     * 最后登录ip
     */
    private String latestLoginIp;

    /**
     * 商家头像
     */
    private String avatarUrl;

    /**
     * 0-禁止，1-允许
     */
    private Integer isAllowLogin;

    /**
     * 角色id
     */
    private Integer rolesId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照vendorId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;
}