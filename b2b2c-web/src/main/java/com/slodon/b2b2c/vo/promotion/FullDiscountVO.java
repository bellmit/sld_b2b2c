package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.FullAmountCycleMinus;
import com.slodon.b2b2c.promotion.pojo.FullAmountLadderDiscount;
import com.slodon.b2b2c.promotion.pojo.FullAmountStepMinus;
import com.slodon.b2b2c.promotion.pojo.FullNumLadderDiscount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装满优惠列表VO对象
 * @Author wuxy
 */
@Data
public class FullDiscountVO implements Serializable {

    private static final long serialVersionUID = 7316989709636749179L;
    @ApiModelProperty("活动id")
    private Integer fullId;

    @ApiModelProperty("活动名称")
    private String fullName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动状态( 1-已创建，2-已发布，3-进行中，4-已结束，5-已失效)")
    private Integer state;

    @ApiModelProperty("活动状态值( 1-已创建，2-已发布，3-进行中，4-已结束，5-已失效)")
    private String stateValue;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    public FullDiscountVO(FullAmountCycleMinus fullAmountCycleMinus) {
        fullId = fullAmountCycleMinus.getFullId();
        fullName = fullAmountCycleMinus.getFullName();
        startTime = fullAmountCycleMinus.getStartTime();
        endTime = fullAmountCycleMinus.getEndTime();
        state = dealState(fullAmountCycleMinus.getState(), fullAmountCycleMinus.getStartTime(), fullAmountCycleMinus.getEndTime());
        stateValue = dealStateValue(fullAmountCycleMinus.getState(), fullAmountCycleMinus.getStartTime(), fullAmountCycleMinus.getEndTime());
        storeId = fullAmountCycleMinus.getStoreId();
        storeName = fullAmountCycleMinus.getStoreName();
    }

    public FullDiscountVO(FullAmountLadderDiscount fullAmountLadderDiscount) {
        fullId = fullAmountLadderDiscount.getFullId();
        fullName = fullAmountLadderDiscount.getFullName();
        startTime = fullAmountLadderDiscount.getStartTime();
        endTime = fullAmountLadderDiscount.getEndTime();
        state = dealState(fullAmountLadderDiscount.getState(), fullAmountLadderDiscount.getStartTime(), fullAmountLadderDiscount.getEndTime());
        stateValue = dealStateValue(fullAmountLadderDiscount.getState(), fullAmountLadderDiscount.getStartTime(), fullAmountLadderDiscount.getEndTime());
        storeId = fullAmountLadderDiscount.getStoreId();
        storeName = fullAmountLadderDiscount.getStoreName();
    }

    public FullDiscountVO(FullAmountStepMinus fullAmountStepMinus) {
        fullId = fullAmountStepMinus.getFullId();
        fullName = fullAmountStepMinus.getFullName();
        startTime = fullAmountStepMinus.getStartTime();
        endTime = fullAmountStepMinus.getEndTime();
        state = dealState(fullAmountStepMinus.getState(), fullAmountStepMinus.getStartTime(), fullAmountStepMinus.getEndTime());
        stateValue = dealStateValue(fullAmountStepMinus.getState(), fullAmountStepMinus.getStartTime(), fullAmountStepMinus.getEndTime());
        storeId = fullAmountStepMinus.getStoreId();
        storeName = fullAmountStepMinus.getStoreName();
    }

    public FullDiscountVO(FullNumLadderDiscount fullNumLadderDiscount) {
        fullId = fullNumLadderDiscount.getFullId();
        fullName = fullNumLadderDiscount.getFullName();
        startTime = fullNumLadderDiscount.getStartTime();
        endTime = fullNumLadderDiscount.getEndTime();
        state = dealState(fullNumLadderDiscount.getState(), fullNumLadderDiscount.getStartTime(), fullNumLadderDiscount.getEndTime());
        stateValue = dealStateValue(fullNumLadderDiscount.getState(), fullNumLadderDiscount.getStartTime(), fullNumLadderDiscount.getEndTime());
        storeId = fullNumLadderDiscount.getStoreId();
        storeName = fullNumLadderDiscount.getStoreName();
    }

    public static Integer dealState(Integer state, Date startTime, Date endTime) {
        Integer value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == PromotionConst.STATE_CREATED_1) {
            value = 1;
        } else if (state == PromotionConst.STATE_RELEASE_2) {
            Date date = new Date();
            if (date.before(startTime)) {
                value = 2;
            } else if (date.after(endTime)) {
                value = 4;
            } else {
                value = 3;
            }
        } else {
            value = 5;
        }
        return value;
    }

    public static String dealStateValue(Integer state, Date startTime, Date endTime) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == PromotionConst.STATE_CREATED_1) {
            value = "待发布";
        } else if (state == PromotionConst.STATE_RELEASE_2) {
            Date date = new Date();
            if (date.before(startTime)) {
                value = "未开始";
            } else if (date.after(endTime)) {
                value = "已结束";
            } else {
                value = "进行中";
            }
        } else {
            value = "已失效";
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
