package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.SeckillStage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装秒杀场次VO对象
 */

@Data
public class SeckillStageVO implements Serializable {
    private static final long serialVersionUID = 3567843223567898765L;

    @ApiModelProperty("场次id")
    private Integer stageId;

    @ApiModelProperty("场次名称")
    private String stageName;

    @ApiModelProperty("参加商品数量")
    private Integer  productCount;

    @ApiModelProperty("场次状态")
    private Integer state;

    @ApiModelProperty("活动状态描述(1-未开始;2-进行中;3-已结束)")
    private String stateValue;

    public SeckillStageVO(SeckillStage seckillStage) {
        this.stageId = seckillStage.getStageId();
        this.stageName = seckillStage.getStageName();
        //根据时间判断场次状态
        Date nowDate = new Date();
        if (nowDate.before(seckillStage.getStartTime())) {
            this.state = SeckillConst.SECKILL_STAGE_STATE_1;
            this.stateValue = "未开始";
            //翻译
            this.stateValue = Language.translate(this.stateValue);
        } else if (nowDate.after(seckillStage.getStartTime()) && nowDate.before(seckillStage.getEndTime())) {
            this.state = SeckillConst.SECKILL_STAGE_STATE_2;
            this.stateValue = "进行中";
            this.stateValue = Language.translate(this.stateValue);
        } else if (nowDate.after(seckillStage.getEndTime())) {
            this.state = SeckillConst.SECKILL_STAGE_STATE_3;
            this.stateValue = "已结束";
            this.stateValue = Language.translate(this.stateValue);
        }
    }
}