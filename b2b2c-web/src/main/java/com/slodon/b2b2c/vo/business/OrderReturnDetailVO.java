package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.OrderAfterSaleLog;
import com.slodon.b2b2c.business.pojo.OrderAfterService;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.business.pojo.OrderReturn;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.seller.pojo.StoreAddress;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.i18n.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装售后详情VO对象
 * @Author wuxy
 */
@Data
public class OrderReturnDetailVO implements Serializable {

    private static final long serialVersionUID = 2653086242911255566L;
    @ApiModelProperty("退货id")
    private Integer returnId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("货品id")
    private Long productId;

    @ApiModelProperty("货品图片")
    private String productImage;

    @ApiModelProperty("商品规格")
    private String specValues;

    @ApiModelProperty("货品单价")
    private BigDecimal productShowPrice;

    @ApiModelProperty("售后服务单号")
    private String afsSn;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("商品单价")
    private BigDecimal productPrice;

    @ApiModelProperty("退货数量")
    private Integer returnNum;

    @ApiModelProperty("退款类型：1==仅退款 2=退货退款")
    private Integer returnType;

    @ApiModelProperty("退款方式值：1-仅退款 2-退货退款")
    private String returnTypeValue;

    @ApiModelProperty("退款金额")
    private BigDecimal returnMoneyAmount;

    @ApiModelProperty("退款原因")
    private String applyReasonContent;

    @ApiModelProperty("退款说明")
    private String afsDescription;

    @ApiModelProperty("退款凭证")
    private List<String> applyImageList;

    @ApiModelProperty("申请时间")
    private Date applyTime;

    @ApiModelProperty("商家处理时间")
    private Date storeAuditTime;

    @ApiModelProperty("买家退货时间")
    private Date buyerDeliverTime;

    @ApiModelProperty("退货完成时间")
    private Date completeTime;

    @ApiModelProperty("拒绝原因")
    private String refuseReason;

    @ApiModelProperty("商户备注")
    private String storeRemark;

    @ApiModelProperty("退货退款状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家同意退款申请；201-商家同意退货退款申请；202-商家拒绝退款申请(退款关闭/拒收关闭)；203-商家确认收货；300-平台确认退款(已完成)")
    private Integer state;

    @ApiModelProperty("退货退款状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家同意退款申请；201-商家同意退货退款申请；202-商家拒绝退款申请(退款关闭/拒收关闭)；203-商家确认收货；300-平台确认退款(已完成)")
    private String stateValue;

    @ApiModelProperty("货物状态：0-未收到货，1-已收到货")
    private Integer goodsState;

    @ApiModelProperty("货物状态值：0-未收到货，1-已收到货")
    private String goodsStateValue;

    @ApiModelProperty("平台审核")
    private String platformAudit;

    @ApiModelProperty("平台备注")
    private String platformRemark;

    @ApiModelProperty("售后服务端类型：1-退货退款单，2-换货单，3-仅退款单")
    private Integer afsType;

    @ApiModelProperty("订单货品明细ID")
    private Long orderProductId;

    @ApiModelProperty("截止日期(根据售后状态，待卖家审核--审核截止时间；待买家发货--发货截止时间；待商家收货--收货退款截止时间)")
    private Date deadline;

    @ApiModelProperty("平台电话")
    private String platformPhone;

    @ApiModelProperty("退款方式：退回到余额或者原路退回")
    private String returnMethod;

    @ApiModelProperty("日志列表")
    private List<ReturnLogVO> returnLogList;

    @ApiModelProperty("店铺收件人姓名")
    private String storeContactName;

    @ApiModelProperty("店铺联系电话")
    private String storeTelphone;

    @ApiModelProperty("店铺收货地址省市区组合")
    private String storeAreaInfo;

    @ApiModelProperty("店铺详细地址")
    private String storeAddress;

    @ApiModelProperty("换件/退件物流公司")
    private String buyerExpressName;

    @ApiModelProperty("换件/退件快递单号")
    private String buyerExpressNumber;

    public OrderReturnDetailVO(OrderReturn orderReturn, OrderAfterService orderAfterService, OrderProduct orderProduct, StoreAddress storeAddress) {
        returnId = orderReturn.getReturnId();
        goodsName = orderProduct.getGoodsName();
        goodsId = orderProduct.getGoodsId();
        productId = orderProduct.getProductId();
        productImage = FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null);
        specValues = orderProduct.getSpecValues();
        productShowPrice = orderProduct.getProductShowPrice();
        afsSn = orderReturn.getAfsSn();
        orderSn = orderReturn.getOrderSn();
        memberId = orderReturn.getMemberId();
        memberName = orderReturn.getMemberName();
        productPrice = orderProduct.getProductShowPrice();
        returnNum = orderReturn.getReturnNum();
        returnType = orderReturn.getReturnType();
        returnTypeValue = OrderReturnVO.dealReturnTypeValue(orderReturn.getReturnType());
        returnMoneyAmount = orderReturn.getReturnMoneyAmount().add(orderReturn.getReturnExpressAmount());
        applyReasonContent = orderAfterService.getApplyReasonContent();
        afsDescription = orderAfterService.getAfsDescription();
        applyImageList = dealApplyImageList(orderAfterService.getApplyImage());
        applyTime = orderAfterService.getBuyerApplyTime();
        storeAuditTime = orderAfterService.getStoreAuditTime();
        buyerDeliverTime = orderAfterService.getBuyerDeliverTime();
        completeTime = orderReturn.getCompleteTime();
        refuseReason = orderReturn.getRefuseReason();
        storeRemark = orderAfterService.getStoreRemark();
        goodsState = orderAfterService.getGoodsState();
        goodsStateValue = orderAfterService.getGoodsState() == 0 ? "未收货" : "已收货";
        state = orderReturn.getState();
        stateValue = dealStateValue(orderReturn.getState());
        platformAudit = dealPlatformAudit(orderReturn.getState());
        platformRemark = orderAfterService.getPlatformRemark();
        afsType = orderAfterService.getAfsType();
        orderProductId = orderAfterService.getOrderProductId();
        if (storeAddress != null){
            this.storeContactName = storeAddress.getContactName();
            this.storeTelphone = storeAddress.getTelphone();
            this.storeAreaInfo = storeAddress.getAreaInfo();
            this.storeAddress = storeAddress.getAddress();
        }
        buyerExpressNumber = orderAfterService.getBuyerExpressNumber();
        buyerExpressName = orderAfterService.getBuyerExpressName();
    }

    @Data
    public static class ReturnLogVO implements Serializable {

        private static final long serialVersionUID = 6366412100902530206L;
        @ApiModelProperty("日志id")
        private Integer logId;

        @ApiModelProperty("状态")
        private String state;

        @ApiModelProperty("内容")
        private String content;

        @ApiModelProperty("操作时间")
        private Date createTime;

        public ReturnLogVO(OrderAfterSaleLog orderAfterSaleLog) {
            logId = orderAfterSaleLog.getLogId();
            state = orderAfterSaleLog.getState();
            content = orderAfterSaleLog.getContent();
            createTime = orderAfterSaleLog.getCreateTime();
        }
    }

    public static String dealStateValue(Integer state) {
        String value = "";
        if (state == null) return Language.translate("未知");
        switch (state) {
            case OrdersAfsConst.RETURN_STATE_100:
            case OrdersAfsConst.RETURN_STATE_101:
                value = "等待商家处理";
                break;
            case OrdersAfsConst.RETURN_STATE_102:
                value = "等待商家收货";
                break;
            case OrdersAfsConst.RETURN_STATE_202:
                value = "退款失败";
                break;
            case OrdersAfsConst.RETURN_STATE_200:
            case OrdersAfsConst.RETURN_STATE_201:
                value = "商家审核通过";
                break;
            case OrdersAfsConst.RETURN_STATE_203:
                value = "等待平台审核退款";
                break;
            case OrdersAfsConst.RETURN_STATE_300:
                value = "退款完成";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static String dealPlatformAudit(Integer state) {
        String value;
        if (state == null) return null;
        switch (state) {
            case OrdersAfsConst.RETURN_STATE_200:
            case OrdersAfsConst.RETURN_STATE_203:
                value = "待处理";
                break;
            case OrdersAfsConst.RETURN_STATE_300:
                value = "审核完成";
                break;
            default:
                value = null;
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public List<String> dealApplyImageList(String applyImage) {
        List<String> list = new ArrayList<>();
        if (!StringUtil.isEmpty(applyImage)) {
            String[] strArray = applyImage.split(",");
            for (String str : strArray) {
                if (StringUtil.isEmpty(str)) {
                    continue;
                }
                list.add(FileUrlUtil.getFileUrl(str, null));
            }
        }
        return list;
    }
}
