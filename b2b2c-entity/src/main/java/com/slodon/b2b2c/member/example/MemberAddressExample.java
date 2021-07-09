package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberAddressExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer addressIdNotEquals;

    /**
     * 用于批量操作
     */
    private String addressIdIn;

    /**
     * 地址id
     */
    private Integer addressId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 收货人
     */
    private String memberName;

    /**
     * 收货人,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 区县编码
     */
    private String districtCode;

    /**
     * 省市区组合
     */
    private String addressAll;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 手机
     */
    private String telMobile;

    /**
     * 电话
     */
    private String telPhone;

    /**
     * 是否默认地址：1-默认地址，0-非默认地址
     */
    private Integer isDefault;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照addressId倒序排列
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