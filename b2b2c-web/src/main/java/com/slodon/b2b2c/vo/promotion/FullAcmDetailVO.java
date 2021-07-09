package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.FullAmountCycleMinus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装循环满减详情VO对象
 * @Author wuxy
 * @date 2020.11.05 10:10
 */
@Data
public class FullAcmDetailVO implements Serializable {

    private static final long serialVersionUID = -1774900191816978865L;
    @ApiModelProperty("活动id")
    private Integer fullId;

    @ApiModelProperty("活动名称")
    private String fullName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("满指定金额")
    private BigDecimal fullValue;

    @ApiModelProperty("减指定金额")
    private BigDecimal minusValue;

    @ApiModelProperty("赠送积分")
    private Integer sendIntegral;

    @ApiModelProperty("优惠券列表")
    private List<FullCouponVO> couponList;

    @ApiModelProperty("赠品列表")
    private List<FullGiftVO> giftList;

    public FullAcmDetailVO(FullAmountCycleMinus fullAmountCycleMinus) {
        fullId = fullAmountCycleMinus.getFullId();
        fullName = fullAmountCycleMinus.getFullName();
        startTime = fullAmountCycleMinus.getStartTime();
        endTime = fullAmountCycleMinus.getEndTime();
        fullValue = fullAmountCycleMinus.getFullValue();
        minusValue = fullAmountCycleMinus.getMinusValue();
        sendIntegral = fullAmountCycleMinus.getSendIntegral();
    }
}
