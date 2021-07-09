package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.Seckill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装秒杀VO对象
 */

@Data
public class SeckillVO implements Serializable {
    private static final long serialVersionUID = 3454354556569899243L;

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

    @ApiModelProperty("活动状态描述(1-未开始;2-进行中;3-已结束)")
    private String stateValue;

    public SeckillVO(Seckill seckill) {
        this.seckillId = seckill.getSeckillId();
        this.seckillName = seckill.getSeckillName();
        this.startTime = seckill.getStartTime();
        this.endTime = seckill.getEndTime();

        //根据时间判断活动状态
        Date nowDate = new Date();
        if (nowDate.before(this.startTime)) {
            this.state = SeckillConst.SECKILL_STATE_1;
            this.stateValue = "未开始";
            //翻译
            this.stateValue = Language.translate(this.stateValue);
        } else if (nowDate.after(this.startTime) && nowDate.before(this.endTime)) {
            this.state = SeckillConst.SECKILL_STATE_2;
            this.stateValue = "进行中";
            this.stateValue = Language.translate(this.stateValue);
        } else if (nowDate.after(this.endTime)) {
            this.stateValue = "已结束";
            this.stateValue = Language.translate(this.stateValue);
            this.state = SeckillConst.SECKILL_STATE_3;
        }
    }
}