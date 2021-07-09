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

/**
 * @program: slodon
 * @Description 封装秒杀商品VO对象
 */
@Data
public class SeckillStageGoodsVO implements Serializable {

    private static final long serialVersionUID = 4546575642259686579L;
    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("场次名称")
    private String stageName;

    @ApiModelProperty("状态 1待审核 2审核通过，3拒绝")
    private Integer verifyState;

    @ApiModelProperty("状态 1待审核 2审核通过，3拒绝")
    private String verifyStateValue;

    @ApiModelProperty("审核拒绝理由")
    private String remark;

    @ApiModelProperty("场次id")
    private Integer stageId;

    public SeckillStageGoodsVO(SeckillStageProduct seckillStageProduct) {
        this.goodsId = seckillStageProduct.getGoodsId();
        this.goodsImage = FileUrlUtil.getFileUrl(seckillStageProduct.getMainImage(), ImageSizeEnum.SMALL);
        this.goodsName = seckillStageProduct.getGoodsName();
        this.storeName = seckillStageProduct.getStoreName();
        this.labelName = seckillStageProduct.getLabelName();
        this.stageName = seckillStageProduct.getStageName();
        this.verifyState = seckillStageProduct.getVerifyState();
        this.verifyStateValue = dealVerifyStateValue(this.verifyState);
        this.remark = seckillStageProduct.getRemark();
        this.stageId = seckillStageProduct.getStageId();
    }

    public static String dealVerifyStateValue(Integer verifyState) {
        String value = null;
        if (StringUtils.isEmpty(verifyState)) return null;
        switch (verifyState) {
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
