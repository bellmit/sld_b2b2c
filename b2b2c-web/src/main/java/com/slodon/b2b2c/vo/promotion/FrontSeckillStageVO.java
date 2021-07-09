package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.SeckillStage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @program: slodon
 * @Description 封装秒杀场次VO对象
 */
@Data
public class FrontSeckillStageVO implements Serializable {
    private static final long serialVersionUID = 6109936539389073444L;

    @ApiModelProperty("场次id")
    private Integer stageId;

    @ApiModelProperty("场次名称")
    private String stageName;

    @ApiModelProperty("秒杀活动id")
    private Integer seckillId;

    @ApiModelProperty("秒杀活动名称")
    private String seckillName;

    @ApiModelProperty("场次开始时间")
    private Date startTime;

    @ApiModelProperty("场次开始时间 例10:00")
    private String time;

    @ApiModelProperty("场次结束时间")
    private Date endTime;

    @ApiModelProperty("场次状态 1-未开始；2-进行中；3-结束")
    private Integer state;

    @ApiModelProperty("场次状态 1-未开始；2-进行中；3-结束")
    private String stateValue;

    public FrontSeckillStageVO(SeckillStage seckillStage) {
        this.stageId = seckillStage.getStageId();
        this.stageName = seckillStage.getStageName();
        this.seckillId = seckillStage.getSeckillId();
        this.seckillName = seckillStage.getSeckillName();
        this.startTime = seckillStage.getStartTime();
        this.time=dealTime(seckillStage.getStartTime());
        this.endTime = seckillStage.getEndTime();
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
            this.stateValue = "结束";
            this.stateValue = Language.translate(this.stateValue);
        }
    }

    public static String dealTime(Date startTime) {
        if (startTime == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(startTime);
    }
}