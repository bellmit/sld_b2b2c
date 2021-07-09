package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.PreSellConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.Presell;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装预售VO对象
 * @Author wuxy
 * @date 2020.11.04 09:28
 */
@Data
public class PreSellVO implements Serializable {

    private static final long serialVersionUID = -261565769696490975L;
    @ApiModelProperty("预售活动id")
    private Integer presellId;

    @ApiModelProperty("预售活动名称")
    private String presellName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("活动状态(1-创建；2-发布；3-失效)")
    private Integer state;

    @ApiModelProperty("活动状态值(1-创建；2-发布；3-失效)")
    private String stateValue;

    @ApiModelProperty("预售类型（1-定金预售，2-全款预售）")
    private Integer type;

    public PreSellVO(Presell presell) {
        presellId = presell.getPresellId();
        presellName = presell.getPresellName();
        startTime = presell.getStartTime();
        endTime = presell.getEndTime();
        storeId = presell.getStoreId();
        storeName = presell.getStoreName();
        state = dealState(presell.getState(), presell.getStartTime(), presell.getEndTime());
        stateValue = dealStateValue(presell.getState(), presell.getStartTime(), presell.getEndTime());
        type = presell.getType();
    }

    public static Integer dealState(Integer state, Date startTime, Date endTime) {
        Integer value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == PreSellConst.ACTIVITY_STATE_1) {
            value = 1;
        } else if (state == PreSellConst.ACTIVITY_STATE_2) {
            Date date = new Date();
            if (date.before(startTime)) {
                value = 2;
            } else if (date.after(endTime)) {
                value = 5;
            } else {
                value = 3;
            }
        } else if (state == PreSellConst.ACTIVITY_STATE_3) {
            value = 4;
        }
        return value;
    }

    public static String dealStateValue(Integer state, Date startTime, Date endTime) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == PreSellConst.ACTIVITY_STATE_1) {
            value = "待发布";
        } else if (state == PreSellConst.ACTIVITY_STATE_2) {
            Date date = new Date();
            if (date.before(startTime)) {
                value = "已发布未开始";
            } else if (date.after(endTime)) {
                value = "已结束";
            } else {
                value = "进行中";
            }
        } else if (state == PreSellConst.ACTIVITY_STATE_3) {
            value = "已失效";
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
