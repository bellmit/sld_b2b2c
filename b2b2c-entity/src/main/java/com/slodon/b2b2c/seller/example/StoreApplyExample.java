package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StoreApplyExample implements Serializable {
    private static final long serialVersionUID = 9095659065758029675L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer applyIdNotEquals;

    /**
     * 用于批量操作
     */
    private String applyIdIn;

    /**
     * 申请id
     */
    private Integer applyId;

    /**
     * 商户用户id
     */
    private Long vendorId;

    /**
     * 商户登陆用户名
     */
    private String vendorName;

    /**
     * 商户登陆用户名,用于模糊查询
     */
    private String vendorNameLike;

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
     * 店铺类型：1-自营店铺，2-入驻店铺
     */
    private Integer storeType;

    /**
     * 状态：1、店铺信息提交申请；2、店铺信息审核通过；3、店铺信息审核失败；4、开通店铺(支付完成)
     */
    private Integer state;

    /**
     * 状态：用于筛选搜索
     */
    private Integer stateNotEquals;

    /**
     * 申请年限，默认为1年
     */
    private Integer applyYear;

    /**
     * 申请的店铺等级编号
     */
    private Integer storeGradeId;

    /**
     * 申请的店铺分类
     */
    private Integer storeCategoryId;

    /**
     * 大于等于开始时间
     */
    private Date submitTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date submitTimeBefore;

    /**
     * 拒绝理由
     */
    private String refuseReason;

    /**
     * 平台审核信息
     */
    private String auditInfo;

    /**
     * 支付单号
     */
    private String paySn;

    /**
     * 支付单号,用于模糊查询
     */
    private String paySnLike;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 大于等于开始时间
     */
    private Date callbackTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date callbackTimeBefore;

    /**
     * 第三方支付交易流水号
     */
    private String tradeSn;

    /**
     * 第三方支付交易流水号,用于模糊查询
     */
    private String tradeSnLike;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 支付方式名称,用于模糊查询
     */
    private String paymentNameLike;

    /**
     * 支付方式code
     */
    private String paymentCode;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照applyId倒序排列
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