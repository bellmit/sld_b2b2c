package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装秒杀详情VO对象
 * @Author spp
 */
@Data
public class FrontSeckillDetailVO implements Serializable {

    @ApiModelProperty("id")
    private Integer stageProductId;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("距离结束时间")
    private Long distanceEndTime;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品主图路径，每个SPU一张主图")
    private String mainImage;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty("货品秒杀总库存")
    private Integer seckillStock;

    @ApiModelProperty("限购数量")
    private Integer upperLimit;

    @ApiModelProperty("已购买人数")
    private Integer buyerCount;

    @ApiModelProperty("购买数量")
    private Integer buyQuantity;

    @ApiModelProperty("秒杀活动状态(1-未开始 2-进行中 3-已结束)")
    private Integer state;

    @ApiModelProperty("货品库存")
    private Integer productStock;

    /**
     * 其他字段
     */
    @ApiModelProperty("秒杀进度")
    private String secKillProgress;

    @ApiModelProperty("设置/取消提醒,true 取消提醒,false 设置提醒")
    private Boolean isRemind;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    public FrontSeckillDetailVO(SeckillStageProduct seckillStageProduct) {
        stageProductId = seckillStageProduct.getStageProductId();
        startTime = seckillStageProduct.getStartTime();
        endTime = seckillStageProduct.getEndTime();
        productId = seckillStageProduct.getProductId();
        goodsId = seckillStageProduct.getGoodsId();
        mainImage = FileUrlUtil.getFileUrl(seckillStageProduct.getMainImage(), null);
        goodsName = seckillStageProduct.getGoodsName();
        storeId = seckillStageProduct.getStoreId();
        storeName = seckillStageProduct.getStoreName();
        productPrice = seckillStageProduct.getProductPrice();
        seckillPrice = seckillStageProduct.getSeckillPrice();
        seckillStock = seckillStageProduct.getSeckillStock();
        upperLimit = seckillStageProduct.getUpperLimit();
        buyerCount = seckillStageProduct.getBuyerCount();
        buyQuantity = seckillStageProduct.getBuyQuantity();
        productStock = seckillStageProduct.getProductStock();
        state = seckillStageProduct.getState();
        secKillProgress = dealProgress(seckillStageProduct.getBuyQuantity(), seckillStageProduct.getSeckillStock());
    }

    public static String dealProgress(Integer buyQuantity, Integer seckillStock) {
        String value = "";
        if (StringUtil.isNullOrZero(seckillStock)) return null;
        value = buyQuantity * 100 / seckillStock + "%";
        return value;
    }
}
