package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.Seckill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装已参加秒杀活动VO对象
 */

@Data
public class SellerJoinSeckillVO implements Serializable {

    private static final long serialVersionUID = -3792462776048311535L;
    @ApiModelProperty("秒杀活动id")
    private Integer seckillId;

    @ApiModelProperty("活动名称")
    private String seckillName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动状态(1-未开始;2-进行中;3-已结束)")
    private Integer state;

    @ApiModelProperty("活动状态(1-未开始;2-进行中;3-已结束)")
    private String stateValue;

    public SellerJoinSeckillVO(Seckill seckill) {
        this.seckillId = seckill.getSeckillId();
        this.seckillName = seckill.getSeckillName();
        this.startTime = seckill.getStartTime();
        this.endTime = seckill.getEndTime();

        //根据时间判断活动状态
        Date nowDate = new Date();
        if (nowDate.before(this.startTime)) {
            this.state = SeckillConst.SECKILL_STATE_1;
        } else if (nowDate.after(this.startTime) && nowDate.before(this.endTime)) {
            this.state = SeckillConst.SECKILL_STATE_2;
        } else if (nowDate.after(this.endTime)) {
            this.state = SeckillConst.SECKILL_STATE_3;
        }
        this.stateValue = dealStateValue(this.state);
    }

    public static String dealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        switch (state) {
            case SeckillConst.SECKILL_STATE_1:
                value = "未开始";
                break;
            case SeckillConst.SECKILL_STATE_2:
                value = "进行中";
                break;
            case SeckillConst.SECKILL_STATE_3:
                value = "已结束";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}