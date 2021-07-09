package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装seller首页概况VO对象
 * @Author wuxy
 */
@Data
public class SellerIndexVO implements Serializable {

    private static final long serialVersionUID = -8666666012335449655L;
    @ApiModelProperty("待发货")
    private Integer toDeliveredNum;

    @ApiModelProperty("售后中订单")
    private Integer afsOrderNum;

    @ApiModelProperty("出售中的商品")
    private Integer onSaleGoodsNum;

    @ApiModelProperty("待审核的商品")
    private Integer toAuditGoodsNum;

    @ApiModelProperty("违规的商品")
    private Integer violationGoodsNum;

    @ApiModelProperty("待确认的结算单")
    private Integer toConfirmBillNUm;

    @ApiModelProperty("销量排名")
    private List<GoodsSaleRankVO> goodsSaleRank;

    @ApiModelProperty("七日订单数")
    private List<OrderReportVO> OrderWeeklyReport;

    @ApiModelProperty("七日销售额")
    private List<SellerSalesVolumeVO> weekSaleReport;

    @Data
    public static class GoodsSaleRankVO implements Serializable {

        private static final long serialVersionUID = -3493551235762729971L;
        @ApiModelProperty("商品图片")
        private String goodsImage;

        @ApiModelProperty("商品名称")
        private String goodsName;

        @ApiModelProperty("销量")
        private Integer number;

        public GoodsSaleRankVO(Goods goods) {
            this.goodsImage = FileUrlUtil.getFileUrl(goods.getMainImage(), null);
            this.goodsName = goods.getGoodsName();
            this.number = goods.getActualSales();
        }
    }

    @Data
    public static class OrderReportVO implements Serializable {

        private static final long serialVersionUID = 1567927988058166641L;
        @ApiModelProperty("天")
        private String day;

        @ApiModelProperty("订单数")
        private Integer number;
    }

    @Data
    public static class SellerSalesVolumeVO implements Serializable {

        private static final long serialVersionUID = 1567927988058166641L;
        @ApiModelProperty("天")
        private String day;

        @ApiModelProperty("销售额")
        private BigDecimal amount;
    }

}
