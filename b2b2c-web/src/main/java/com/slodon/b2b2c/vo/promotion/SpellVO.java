package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.Spell;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装拼团VO对象
 * @Author wuxy
 */
@Data
public class SpellVO implements Serializable {

    private static final long serialVersionUID = 7178919310905952023L;
    @ApiModelProperty("拼团活动id")
    private Integer spellId;

    @ApiModelProperty("拼团活动名称")
    private String spellName;

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

    @ApiModelProperty("活动状态值(1-待发布；2-未开始；3-进行中；4-已失效；5-已结束)")
    private String stateValue;

    public SpellVO(Spell spell) {
        spellId = spell.getSpellId();
        spellName = spell.getSpellName();
        startTime = spell.getStartTime();
        endTime = spell.getEndTime();
        storeId = spell.getStoreId();
        storeName = spell.getStoreName();
        state = dealState(spell.getState(), spell.getStartTime(), spell.getEndTime());
        stateValue = dealStateValue(spell.getState(), spell.getStartTime(), spell.getEndTime());
    }

    public static Integer dealState(Integer state, Date startTime, Date endTime) {
        Integer value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == SpellConst.ACTIVITY_STATE_1) {
            value = 1;
        } else if (state == SpellConst.ACTIVITY_STATE_2) {
            Date date = new Date();
            if (date.before(startTime)) {
                value = 2;
            } else if (date.after(endTime)) {
                value = 5;
            } else {
                value = 3;
            }
        } else if (state == SpellConst.ACTIVITY_STATE_3) {
            value = 4;
        }
        return value;
    }

    public static String dealStateValue(Integer state, Date startTime, Date endTime) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == SpellConst.ACTIVITY_STATE_1) {
            value = "待发布";
        } else if (state == SpellConst.ACTIVITY_STATE_2) {
            Date date = new Date();
            if (date.before(startTime)) {
                value = "未开始";
            } else if (date.after(endTime)) {
                value = "已结束";
            } else {
                value = "进行中";
            }
        } else if (state == SpellConst.ACTIVITY_STATE_3) {
            value = "已失效";
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
