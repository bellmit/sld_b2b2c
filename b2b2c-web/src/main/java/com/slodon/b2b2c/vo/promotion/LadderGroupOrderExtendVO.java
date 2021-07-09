package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.LadderGroupOrderExtend;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装阶梯团查看团队VO对象
 * @Author wuxy
 */
@Data
public class LadderGroupOrderExtendVO implements Serializable {

    private static final long serialVersionUID = -3858796000850945722L;
    @ApiModelProperty("扩展id")
    private Integer extendId;

    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("货品id（sku）")
    private Long productId;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("定金支付单号（关联order_pay表）")
    private String depositPaySn;

    @ApiModelProperty("尾款支付单号（关联order_pay表）")
    private String remainPaySn;

    @ApiModelProperty("订单子状态：101-待付定金；102-待付尾款；103-已完成付款")
    private Integer orderSubState;

    @ApiModelProperty("订单子状态：102-已付定金；103-已付尾款")
    private String orderSubStateValue;

    @ApiModelProperty("预付定金")
    private BigDecimal advanceDeposit;

    @ApiModelProperty("尾款金额")
    private BigDecimal remainAmount;

    @ApiModelProperty("尾款支付的开始时间")
    private Date remainStartTime;

    @ApiModelProperty("尾款支付的截止时间")
    private Date remainEndTime;

    @ApiModelProperty("参团时间")
    private Date participateTime;

    @ApiModelProperty("成功时间")
    private Date successTime;

    public LadderGroupOrderExtendVO(LadderGroupOrderExtend ladderGroupOrderExtend) {
        this.extendId = ladderGroupOrderExtend.getExtendId();
        this.groupId = ladderGroupOrderExtend.getGroupId();
        this.orderSn = ladderGroupOrderExtend.getOrderSn();
        this.memberId = ladderGroupOrderExtend.getMemberId();
        this.memberName = ladderGroupOrderExtend.getMemberName();
        this.goodsId = ladderGroupOrderExtend.getGoodsId();
        this.goodsName = ladderGroupOrderExtend.getGoodsName();
        this.goodsImage = FileUrlUtil.getFileUrl(ladderGroupOrderExtend.getGoodsImage(), null);
        this.productId = ladderGroupOrderExtend.getProductId();
        this.productPrice = ladderGroupOrderExtend.getProductPrice();
        this.depositPaySn = ladderGroupOrderExtend.getDepositPaySn();
        this.remainPaySn = ladderGroupOrderExtend.getRemainPaySn();
        this.orderSubState = ladderGroupOrderExtend.getOrderSubState();
        this.orderSubStateValue = dealOrderSubStateValue(ladderGroupOrderExtend.getOrderSubState());
        this.advanceDeposit = ladderGroupOrderExtend.getAdvanceDeposit();
        this.remainAmount = ladderGroupOrderExtend.getRemainAmount();
        this.remainStartTime = ladderGroupOrderExtend.getRemainStartTime();
        this.remainEndTime = ladderGroupOrderExtend.getRemainEndTime();
        this.participateTime = ladderGroupOrderExtend.getParticipateTime();
        this.successTime = ladderGroupOrderExtend.getSuccessTime();
    }

    public static String dealOrderSubStateValue(Integer orderSubState) {
        String value = null;
        if (StringUtils.isEmpty(orderSubState)) return null;
        switch (orderSubState) {
            case LadderGroupConst.ORDER_SUB_STATE_2:
                value = "已付定金";
                break;
            case LadderGroupConst.ORDER_SUB_STATE_3:
                value = "已付尾款";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}