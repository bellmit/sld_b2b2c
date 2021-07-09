package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装front端预售列表VO对象
 * @Author wuxy
 */
@Data
public class FrontPresellVO implements Serializable {

    private static final long serialVersionUID = 4488733399064568509L;
    @ApiModelProperty("标签列表")
    private List<PreSellLabelVO> labelList;

    @ApiModelProperty("商品列表")
    private List<FrontPreSellGoodsVO> goodsList;

    @ApiModelProperty("分页信息")
    private Pagination pagination;

    public FrontPresellVO(List<PreSellLabelVO> labelList, List<FrontPreSellGoodsVO> goodsList, PagerInfo pager) {
        this.labelList = labelList;
        this.goodsList = goodsList;
        if (pager != null) {
            this.pagination = new Pagination(pager.getPageIndex(), pager.getPageSize(), pager.getRowsCount());
        }
    }

    @Data
    public static class FrontPreSellGoodsVO implements Serializable {

        private static final long serialVersionUID = -2652619033506200871L;
        @ApiModelProperty("预售活动id")
        private Integer presellId;

        @ApiModelProperty("货品id")
        private Long productId;

        @ApiModelProperty("商品id")
        private Long goodsId;

        @ApiModelProperty("商品名称")
        private String goodsName;

        @ApiModelProperty("商品广告语")
        private String goodsBrief;

        @ApiModelProperty("商品图片")
        private String goodsImage;

        @ApiModelProperty("商品原价")
        private BigDecimal productPrice;

        @ApiModelProperty("预售价格")
        private BigDecimal presellPrice;

        @ApiModelProperty("已预定数")
        private Integer saleNum;

        public FrontPreSellGoodsVO(PresellGoods presellGoods) {
            this.presellId = presellGoods.getPresellId();
            this.productId = presellGoods.getProductId();
            this.goodsId = presellGoods.getGoodsId();
            this.goodsName = presellGoods.getGoodsName();
            this.goodsBrief = presellGoods.getPresellDescription();
            this.goodsImage = FileUrlUtil.getFileUrl(presellGoods.getGoodsImage(), null);
            productPrice=presellGoods.getProductPrice();
            this.presellPrice = presellGoods.getPresellPrice();
            this.saleNum = presellGoods.getActualSale();
        }
    }

    @Data
    private class Pagination implements Serializable {

        private static final long serialVersionUID = -6070620243859489681L;
        @ApiModelProperty("当前页面位置")
        private Integer current;
        @ApiModelProperty("分页大小")
        private Integer pageSize;
        @ApiModelProperty("总数")
        private Integer total;

        private Pagination(Integer current, Integer pageSize, Integer total) {
            this.current = current;
            this.pageSize = pageSize;
            this.total = total;
        }
    }
}
