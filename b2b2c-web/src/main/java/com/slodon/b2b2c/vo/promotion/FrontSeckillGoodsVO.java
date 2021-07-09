package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装秒杀商品列表VO对象
 * @Author spp
 */
@Data
public class FrontSeckillGoodsVO implements Serializable {

    @ApiModelProperty("id")
    private Integer stageProductId;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

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

    @ApiModelProperty("货品库存")
    private Integer productStock;

    /**
     * 其他字段
     */
    @ApiModelProperty("秒杀进度")
    private String secKillProgress;

    @ApiModelProperty("秒杀库存状态:1-去抢购, 2-已抢完")
    private Integer secKillStockState;

    @ApiModelProperty("秒杀库存状态:1-去抢购, 2-已抢完")
    private String secKillStockStateValue;

    @ApiModelProperty("场次状态 1-未开始；2-进行中；3-结束")
    private Integer state;

    @ApiModelProperty("场次状态 1-未开始；2-进行中；3-结束")
    private String stateValue;

    @ApiModelProperty("设置/取消提醒,true 取消提醒,false 设置提醒")
    private Boolean isRemind;

    public FrontSeckillGoodsVO(SeckillStageProduct seckillStageProduct, Boolean notice) {
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

        secKillProgress = dealProgress(seckillStageProduct.getBuyQuantity(), seckillStageProduct.getSeckillStock());
        secKillStockState = dealSecKillStockState(seckillStageProduct.getBuyQuantity(), seckillStageProduct.getSeckillStock());
        secKillStockStateValue = dealecKillStockStateValue(secKillStockState);
        state = dealState(seckillStageProduct.getStartTime());
        stateValue = dealStateValue(seckillStageProduct.getStartTime());
        isRemind = notice;
    }

    public static String dealProgress(Integer buyQuantity, Integer seckillStock) {
        String value = "";
        if (StringUtil.isNullOrZero(seckillStock)) return null;
        value = buyQuantity * 100 / seckillStock + "%";
        return value;
    }

    public static Integer dealSecKillStockState(Integer buyQuantity, Integer seckillStock) {
        Integer value = SeckillConst.SECKILL_STOCK_STATE_1;
        if (StringUtil.isNullOrZero(seckillStock)) return null;
        if (!StringUtils.isEmpty(buyQuantity) && buyQuantity < seckillStock) {
            value = SeckillConst.SECKILL_STOCK_STATE_1;
        } else {
            value = SeckillConst.SECKILL_STOCK_STATE_2;
        }
        return value;
    }

    public static String dealecKillStockStateValue(Integer secKillStockState) {
        String value = "";
        if (StringUtil.isNullOrZero(secKillStockState)) return null;
        switch (secKillStockState) {
            case SeckillConst.SECKILL_STOCK_STATE_1:
                value = "去抢购";
                break;
            case SeckillConst.SECKILL_STOCK_STATE_2:
                value = "已抢完";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static Integer dealState(Date startTime) {
        Integer value = SeckillConst.SECKILL_STATE_1;
        if (StringUtils.isEmpty(startTime)) return null;
        if (startTime.after(new Date())) {
            //未开始
            value = SeckillConst.SECKILL_STATE_1;
        } else {
            //进行中
            value = SeckillConst.SECKILL_STATE_2;
        }
        return value;
    }

    public static String dealStateValue(Date startTime) {
        String value = "";
        if (StringUtils.isEmpty(startTime)) return null;
        if (startTime.after(new Date())) {
            value = "未开始";
        } else {
            value = "进行中";
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}

