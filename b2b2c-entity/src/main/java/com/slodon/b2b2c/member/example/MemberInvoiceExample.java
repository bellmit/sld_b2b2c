package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员发票信息表example
 */
@Data
public class MemberInvoiceExample implements Serializable {
    private static final long serialVersionUID = 4452085590611881108L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer invoiceIdNotEquals;

    /**
     * 用于批量操作
     */
    private String invoiceIdIn;

    /**
     * 发票id
     */
    private Integer invoiceId;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 是否默认发票：0-非默认发票，1-默认发票
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
     * 抬头类型：1-个人；2-公司
     */
    private Integer titleType;

    /**
     * 发票类型：1-普通发票；2-增值税发票
     */
    private Integer invoiceType;

    /**
     * 发票内容：1-商品明细；2-商品类别
     */
    private Integer invoiceContent;

    /**
     * 发票抬头(通用信息）
     */
    private String invoiceTitle;

    /**
     * 发票抬头(通用信息）,用于模糊查询
     */
    private String invoiceTitleLike;

    /**
     * 纳税人识别号(通用信息）
     */
    private String taxCode;

    /**
     * 注册地址(专用发票）
     */
    private String registerAddr;

    /**
     * 注册电话(专用发票）
     */
    private String registerPhone;

    /**
     * 开户银行(专用发票）
     */
    private String bankName;

    /**
     * 开户银行(专用发票）,用于模糊查询
     */
    private String bankNameLike;

    /**
     * 银行帐户(专用发票）
     */
    private String bankAccount;

    /**
     * 收票人姓名(通用信息）
     */
    private String receiverName;

    /**
     * 收票人姓名(通用信息）,用于模糊查询
     */
    private String receiverNameLike;

    /**
     * 收票人手机号(通用信息）
     */
    private String receiverMobile;

    /**
     * 收票邮箱(通用信息）
     */
    private String receiverEmail;

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
     * 收票人详细地址(通用信息）
     */
    private String receiverAddress;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照invoiceId倒序排列
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