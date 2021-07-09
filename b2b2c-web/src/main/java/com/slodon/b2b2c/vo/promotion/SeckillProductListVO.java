package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装秒杀商品VO对象
 */
@Data
public class SeckillProductListVO implements Serializable {

    private static final long serialVersionUID = 7327540871880599959L;
    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品图片")
    private String mainImage;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("参加场次")
    private String stageName;

    @ApiModelProperty("参加id")
    private Integer stageId;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动标签")
    private String labelName;

    @ApiModelProperty("审核状态：1-待审核;2-审核通过；3-审核拒绝")
    private Integer state;

    @ApiModelProperty("审核状态值：1-待审核;2-审核通过；3-审核拒绝")
    private String stateValue;

    @ApiModelProperty("审核拒绝原因")
    private String remark;


    public SeckillProductListVO(SeckillStageProduct seckillStageProduct) {
        this.goodsId = seckillStageProduct.getGoodsId();
        this.mainImage = FileUrlUtil.getFileUrl(seckillStageProduct.getMainImage(), ImageSizeEnum.SMALL);
        this.goodsName = seckillStageProduct.getGoodsName();
        this.stageName = seckillStageProduct.getStageName();
        this.startTime = seckillStageProduct.getStartTime();
        this.endTime = seckillStageProduct.getEndTime();
        this.stageId = seckillStageProduct.getStageId();
        this.labelName = seckillStageProduct.getLabelName();
        this.state = seckillStageProduct.getVerifyState();
        this.stateValue = dealStateValue(this.state);
        this.remark = seckillStageProduct.getRemark();
    }

    public static String dealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        switch (state) {
            case SeckillConst.SECKILL_AUDIT_STATE_1:
                value = "待审核";
                break;
            case SeckillConst.SECKILL_AUDIT_STATE_2:
                value = "审核通过";
                break;
            case SeckillConst.SECKILL_AUDIT_STATE_3:
                value = "审核拒绝";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }


}
