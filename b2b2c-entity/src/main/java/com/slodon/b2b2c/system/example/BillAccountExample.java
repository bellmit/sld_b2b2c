package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 结算账号表example
 */
@Data
public class BillAccountExample implements Serializable {
    private static final long serialVersionUID = -5128136171342107013L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer accountIdNotEquals;

    /**
     * 用于批量操作
     */
    private String accountIdIn;

    /**
     * 账号id
     */
    private Integer accountId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 账户类型：1-银行账号；2-支付宝账号
     */
    private Integer accountType;

    /**
     * 支付宝姓名
     */
    private String alipayName;

    /**
     * 支付宝姓名,用于模糊查询
     */
    private String alipayNameLike;

    /**
     * 支付宝账号
     */
    private String alipayAccount;

    /**
     * 银行开户名
     */
    private String bankAccountName;

    /**
     * 银行开户名,用于模糊查询
     */
    private String bankAccountNameLike;

    /**
     * 公司银行账号
     */
    private String bankAccountNumber;

    /**
     * 开户银行支行
     */
    private String bankBranch;

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
     * 是否默认账号：1-默认账号，0-非默认账号
     */
    private Integer isDefault;

    /**
     * 创建人id
     */
    private Long createVendorId;

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
     * 排序条件，条件之间用逗号隔开，如果不传则按照accountId倒序排列
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