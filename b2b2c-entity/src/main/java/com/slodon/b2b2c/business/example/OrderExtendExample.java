package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderExtendExample implements Serializable {
    private static final long serialVersionUID = 1925763780260006550L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer extendIdNotEquals;

    /**
     * 用于批量操作
     */
    private String extendIdIn;

    /**
     * 扩展id
     */
    private Integer extendId;

    /**
     * 关联的订单编号
     */
    private String orderSn;

    /**
     * 关联的订单编号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 配送公司ID
     */
    private Integer shippingExpressId;

    /**
     * 大于等于开始时间
     */
    private Date deliverTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date deliverTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date evaluationTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date evaluationTimeBefore;

    /**
     * 用户订单备注
     */
    private String orderRemark;

    /**
     * 订单赠送积分
     */
    private Integer orderPointsCount;

    /**
     * 优惠券面额
     */
    private Integer voucherPrice;

    /**
     * 优惠券编码
     */
    private String voucherCode;

    /**
     * 会员来源1、pc；2、H5；3、Android；4、IOS; 5-微信小程序
     */
    private Integer orderFrom;

    /**
     * 发货地址ID
     */
    private Integer deliverAddressId;

    /**
     * 收货省份编码
     */
    private String receiverProvinceCode;

    /**
     * 收货城市编码
     */
    private String receiverCityCode;

    /**
     * 收货区县编码
     */
    private String receiverDistrictCode;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人姓名,用于模糊查询
     */
    private String receiverNameLike;

    /**
     * 收货人详细地址信息
     */
    private String receiverInfo;

    /**
     * 发票信息 json格式
     */
    private String invoiceInfo;

    /**
     * 促销信息备注
     */
    private String promotionInfo;

    /**
     * 是否是电子面单
     */
    private Integer isDzmd;

    /**
     * 发票状态0-未开、1-已开
     */
    private Integer invoiceStatus;

    /**
     * 商家优惠券优惠金额
     */
    private BigDecimal storeVoucherAmount;

    /**
     * 平台优惠券优惠金额
     */
    private BigDecimal platformVoucherAmount;

    /**
     * 发货备注
     */
    private String deliverRemark;

    /**
     * 发货类型：0-物流发货，1-无需物流
     */
    private String deliverType;

    /**
     * 发货人
     */
    private String deliverName;

    /**
     * 发货人电话
     */
    private String deliverMobile;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照extendId倒序排列
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