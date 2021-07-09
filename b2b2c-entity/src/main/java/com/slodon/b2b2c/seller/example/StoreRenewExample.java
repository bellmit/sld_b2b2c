package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StoreRenewExample implements Serializable {
    private static final long serialVersionUID = -8362351002816077804L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer renewIdNotEquals;

    /**
     * 用于批量操作
     */
    private String renewIdIn;

    /**
     * 续签id
     */
    private Integer renewId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 模糊查询：店铺名称
     */
    private String storeNameLike;

    /**
     * 店主账号
     */
    private String vendorName;

    /**
     * 模糊查询：店主账号
     */
    private String vendorNameLike;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 模糊查询：联系人
     */
    private String contactNameLike;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系电话，模糊查询
     */
    private String contactPhoneLike;

    /**
     * 大于等于开始时间
     */
    private Date applyTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date applyTimeBefore;

    /**
     * 续签时长
     */
    private Integer duration;

    /**
     * 店铺等级
     */
    private Integer gradeId;

    /**
     * 付款凭证
     */
    private String paymentEvidence;

    /**
     * 付款凭证说明
     */
    private String paymentEvidenceDesc;

    /**
     * 大于等于开始时间
     */
    private Date payTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date payTimeBefore;

    /**
     * 续签状态，1：待付款；2续签成功；
     */
    private Integer state;

    /**
     * 大于等于开始时间
     */
    private Date startTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date startTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date endTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date endTimeBefore;

    /**
     * 支付单号
     */
    private String paySn;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 第三方支付交易流水号
     */
    private String tradeSn;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 支付方式code
     */
    private String paymentCode;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照renewId倒序排列
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