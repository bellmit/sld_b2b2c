package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderAfterServiceExample implements Serializable {
    private static final long serialVersionUID = 4128643325985605741L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer afsIdNotEquals;

    /**
     * 用于批量操作
     */
    private String afsIdIn;

    /**
     * 售后服务id
     */
    private Integer afsId;

    /**
     * 售后服务单号
     */
    private String afsSn;

    /**
     * 售后服务单号,用于模糊查询
     */
    private String afsSnLike;

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
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 用户ID
     */
    private Integer memberId;

    /**
     * 用户名称
     */
    private String memberName;

    /**
     * 用户名称,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 订单货品明细ID
     */
    private Long orderProductId;

    /**
     * 申请售后的货品数量
     */
    private Integer afsNum;

    /**
     * 申请售后的货品数量,用于模糊查询
     */
    private String afsNumLike;

    /**
     * 商户的售后服务详细收货地址，从store_address表获取
     */
    private String storeAfsAddress;

    /**
     * 换件/退件物流公司
     */
    private String buyerExpressName;

    /**
     * 换件/退件物流公司,用于模糊查询
     */
    private String buyerExpressNameLike;

    /**
     * 换件/退件快递单号
     */
    private String buyerExpressNumber;

    /**
     * 换件/退件快递单号,用于模糊查询
     */
    private String buyerExpressNumberLike;

    /**
     * 换件/退件物流公司快递代码
     */
    private String buyerExpressCode;

    /**
     * 大于等于开始时间
     */
    private Date buyerDeliverTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date buyerDeliverTimeBefore;

    /**
     * 申请提交图片
     */
    private String applyImage;

    /**
     * 申请人姓名
     */
    private String contactName;

    /**
     * 申请人姓名,用于模糊查询
     */
    private String contactNameLike;

    /**
     * 申请人联系电话
     */
    private String contactPhone;

    /**
     * 售后服务端类型：1-退货退款单，2-换货单，3-仅退款单
     */
    private Integer afsType;

    /**
     * 申请售后服务原因
     */
    private String applyReasonContent;

    /**
     * 申请售后服务原因,用于模糊查询
     */
    private String applyReasonContentLike;

    /**
     * 申请售后详细问题描述
     */
    private String afsDescription;

    /**
     * 大于等于开始时间
     */
    private Date buyerApplyTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date buyerApplyTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date storeAuditTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date storeAuditTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date platformAuditTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date platformAuditTimeBefore;

    /**
     * 用户备注
     */
    private String buyerRemark;

    /**
     * 平台备注
     */
    private String platformRemark;

    /**
     * 商户备注
     */
    private String storeRemark;

    /**
     * 大于等于开始时间
     */
    private Date storeReceiveTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date storeReceiveTimeBefore;

    /**
     * 货物状态：0-未收到货，1-已收到货
     */
    private Integer goodsState;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照afsId倒序排列
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