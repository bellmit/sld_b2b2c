package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StoreAddressExample implements Serializable {
    private static final long serialVersionUID = -4375707414284856111L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer addressIdNotEquals;

    /**
     * 用于批量操作
     */
    private String addressIdIn;

    /**
     * 地址ID
     */
    private Integer addressId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 收件人姓名
     */
    private String contactName;

    /**
     * 收件人姓名,用于模糊查询
     */
    private String contactNameLike;

    /**
     * 联系电话
     */
    private String telphone;

    /**
     * 模糊查询，联系电话
     */
    private String telphoneLike;

    /**
     * 备用联系电话
     */
    private String telphone2;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 地区编码
     */
    private String areaCode;

    /**
     * 街道编码
     */
    private String streetCode;

    /**
     * 省市区组合
     */
    private String areaInfo;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 大于等于开始时间
     */
    private Date createdTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createdTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 是否默认：1-是;0-否
     */
    private Integer isDefault;

    /**
     * 类型：1-发货地址；2-收货地址
     */
    private Integer type;

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