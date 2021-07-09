package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.LadderGroupGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装front端阶梯团列表VO对象
 * @Author wuxy
 */
@Data
public class FrontLadderGroupVO implements Serializable {

    private static final long serialVersionUID = -6038895036304151719L;
    @ApiModelProperty("标签列表")
    private List<LadderGroupLabelVO> labelList;

    @ApiModelProperty("商品列表")
    private List<FrontLadderGroupGoodsVO> goodsList;

    @ApiModelProperty("分页信息")
    private Pagination pagination;

    public FrontLadderGroupVO(List<LadderGroupLabelVO> labelList, List<FrontLadderGroupGoodsVO> goodsList, PagerInfo pager) {
        this.labelList = labelList;
        this.goodsList = goodsList;
        if (pager != null) {
            this.pagination = new Pagination(pager.getPageIndex(), pager.getPageSize(), pager.getRowsCount());
        }
    }

    @Data
    public static class FrontLadderGroupGoodsVO implements Serializable {

        private static final long serialVersionUID = -2652619033506200871L;
        @ApiModelProperty("阶梯团活动id")
        private Integer groupId;

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

        @ApiModelProperty("拼团价")
        private BigDecimal spellPrice;

        @ApiModelProperty("销量")
        private Integer saleNum;

        public FrontLadderGroupGoodsVO(LadderGroupGoods ladderGroupGoods) {
            this.groupId = ladderGroupGoods.getGroupId();
            this.productId = ladderGroupGoods.getProductId();
            this.goodsId = ladderGroupGoods.getGoodsId();
            this.goodsName = ladderGroupGoods.getGoodsName();
            this.goodsBrief = ladderGroupGoods.getGoodsBrief();
            this.goodsImage = FileUrlUtil.getFileUrl(ladderGroupGoods.getGoodsImage(), null);
            this.productPrice = ladderGroupGoods.getProductPrice();
            this.spellPrice = dealSpellPrice(ladderGroupGoods.getLadderPrice1(), ladderGroupGoods.getLadderPrice2(), ladderGroupGoods.getLadderPrice3());
            this.saleNum = ladderGroupGoods.getSalesVolume();
        }
    }

    @Data
    private class Pagination implements Serializable {

        private static final long serialVersionUID = 149845724969854918L;
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

    public static BigDecimal dealSpellPrice(BigDecimal ladderPrice1, BigDecimal ladderPrice2, BigDecimal ladderPrice3) {
        BigDecimal value = null;
        if (ladderPrice3.compareTo(BigDecimal.ZERO) > 0) {
            value = ladderPrice3;
        } else if (ladderPrice2.compareTo(BigDecimal.ZERO) > 0) {
            value = ladderPrice2;
        } else {
            value = ladderPrice1;
        }
        return value;
    }

}
