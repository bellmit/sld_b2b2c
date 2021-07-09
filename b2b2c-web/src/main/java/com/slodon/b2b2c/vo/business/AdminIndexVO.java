package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.dto.SaleTotalDayDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装admin首页概况VO对象
 * @Author wuxy
 */
@Data
public class AdminIndexVO implements Serializable {

    private static final long serialVersionUID = -8670323235823098303L;
    @ApiModelProperty("今日营业额")
    private BigDecimal dailySale;

    @ApiModelProperty("今日下单数")
    private Integer orderNum;

    @ApiModelProperty("新增会员数")
    private Integer newMemberNum;

    @ApiModelProperty("会员总数")
    private Integer MemberTotal;

    @ApiModelProperty("新增店铺数")
    private Integer newStoreNum;

    @ApiModelProperty("店铺总数")
    private Integer storeTotal;

    @ApiModelProperty("新增商品数")
    private Integer newGoodsNum;

    @ApiModelProperty("商品总数")
    private Integer goodsTotal;

    @ApiModelProperty("订单增长")
    private List<OrderReportVO> OrderWeeklyReport;

    @ApiModelProperty("会员增长")
    private List<MemberReportVO> memberWeeklyReport;

    @ApiModelProperty("销售总额增长")
    private List<SaleTotalDayDTO> saleTotalWeeklyReport;

    @ApiModelProperty("近一年销售额类别占比")
    private List<SalesVolumeVO> yearSaleCateRate;

    @ApiModelProperty("近一月销售额类别占比")
    private List<SalesVolumeVO> monthSaleCateRate;

    @ApiModelProperty("近一周销售额类别占比")
    private List<SalesVolumeVO> weekSaleCateRate;

    @ApiModelProperty("7日内店铺销售TOP20")
    private List<StoreSaleRankVO> storeSaleRank;

    @ApiModelProperty("7日内商品销售TOP20")
    private List<GoodsSaleRankVO> goodsSaleRank;

    @Data
    public static class OrderReportVO implements Serializable {

        private static final long serialVersionUID = 1567927988058166641L;
        @ApiModelProperty("天")
        private String day;

        @ApiModelProperty("增长数")
        private Integer number;
    }

    @Data
    public static class MemberReportVO implements Serializable {

        private static final long serialVersionUID = 3239533557390193643L;
        @ApiModelProperty("天")
        private String day;

        @ApiModelProperty("增长数")
        private Integer number;
    }

    @Data
    public static class GoodsSaleRankVO implements Serializable {

        private static final long serialVersionUID = -3493551235762729971L;
        @ApiModelProperty("商品名称")
        private String goodsName;

        @ApiModelProperty("销量")
        private Integer number;
    }

    @Data
    public static class StoreSaleRankVO implements Serializable {

        private static final long serialVersionUID = -4824220185532663669L;
        @ApiModelProperty("店铺名称")
        private String storeName;

        @ApiModelProperty("销售额")
        private BigDecimal amount;
    }

}
