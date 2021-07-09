package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.LadderGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装阶梯团VO对象
 * @Author wuxy
 */
@Data
public class LadderGroupVO implements Serializable {

    private static final long serialVersionUID = -4454372285120210317L;
    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("阶梯团活动名称")
    private String groupName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动标签id")
    private Integer labelId;

    @ApiModelProperty("活动标签名称")
    private String labelName;

    @ApiModelProperty("商品id(spu)")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("状态：1-待发布；2-未开始；3-进行中；4-已失效；5-已结束")
    private Integer state;

    @ApiModelProperty("状态值：1-待发布；2-未开始；3-进行中；4-已失效；5-已结束")
    private String stateValue;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    public LadderGroupVO(LadderGroup ladderGroup) {
        this.groupId = ladderGroup.getGroupId();
        this.groupName = ladderGroup.getGroupName();
        this.startTime = ladderGroup.getStartTime();
        this.endTime = ladderGroup.getEndTime();
        this.labelId = ladderGroup.getLabelId();
        this.labelName = ladderGroup.getLabelName();
        this.goodsId = ladderGroup.getGoodsId();
        this.goodsName = ladderGroup.getGoodsName();
        this.state = dealState(ladderGroup.getState(), ladderGroup.getStartTime(), ladderGroup.getEndTime());
        this.stateValue = dealStateValue(ladderGroup.getState(), ladderGroup.getStartTime(), ladderGroup.getEndTime());
        this.storeId = ladderGroup.getStoreId();
        this.storeName = ladderGroup.getStoreName();
    }

    public static Integer dealState(Integer state, Date startTime, Date endTime) {
        Integer value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == LadderGroupConst.STATE_1) {
            value = 1;
        } else if (state == LadderGroupConst.STATE_2) {
            Date date = new Date();
            if (date.before(startTime)) {
                value = 2;
            } else if (date.after(endTime)) {
                value = 5;
            } else {
                value = 3;
            }
        } else if (state == LadderGroupConst.STATE_3) {
            value = 4;
        }
        return value;
    }

    public static String dealStateValue(Integer state, Date startTime, Date endTime) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == LadderGroupConst.STATE_1) {
            value = "待发布";
        } else if (state == LadderGroupConst.STATE_2) {
            Date date = new Date();
            if (date.before(startTime)) {
                value = "未开始";
            } else if (date.after(endTime)) {
                value = "已结束";
            } else {
                value = "进行中";
            }
        } else if (state == LadderGroupConst.STATE_3) {
            value = "已失效";
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
