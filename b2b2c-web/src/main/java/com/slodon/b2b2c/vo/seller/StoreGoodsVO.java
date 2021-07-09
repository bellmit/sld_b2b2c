package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装店铺商品VO对象
 */
@Data
public class StoreGoodsVO {

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("商品所属分类路径，(例如：分类1/分类2/分类3，前后都无斜杠)")
    private String categoryPath;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("商城价销售价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    @ApiModelProperty("虚拟销量")
    private Integer virtualSales;

    @ApiModelProperty("实际销量")
    private Integer actualSales;

    @ApiModelProperty("1-创建；2-待审核；3-上架（a. 审核通过上架，b. 不需要平台审核，商户创建商品后点击上架操作）；4-审核驳回(平台驳回）；5-商品下架（商户自行下架）；6-违规下架（平台违规下架操作）；7-已删除（状态1、5、6可以删除后进入此状态）")
    private Integer state;

    @ApiModelProperty("商品主图路径，每个SPU一张主图")
    private String goodsImage;

    @ApiModelProperty("是否违规下架(0-否，1-是)")
    private Integer isOffline;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("商品创建时间")
    private Date createTime;

    @ApiModelProperty("是否已经预警过 1==否 2==是")
    private Integer productStockWarningState;

    @ApiModelProperty("商品条形码（标准的商品条形码）")
    private String barCode;

    @ApiModelProperty("默认货品id")
    private Long defaultProductId;

    public StoreGoodsVO(Goods goods, GoodsExtend goodsExtend) {
        this.goodsId = goods.getGoodsId();
        this.goodsName = goods.getGoodsName();
        this.categoryPath = goods.getCategoryPath();
        this.marketPrice = goods.getMarketPrice();
        this.goodsPrice = goods.getGoodsPrice();
        this.goodsStock = goods.getGoodsStock();
        this.state = goods.getState();
        this.virtualSales = goods.getVirtualSales();
        this.actualSales = goods.getActualSales();
        this.goodsImage = FileUrlUtil.getFileUrl(goods.getMainImage(), null);
        this.isOffline = goods.getIsOffline();
        this.createTime = goodsExtend.getCreateTime();
        this.barCode = goods.getBarCode();
        this.defaultProductId = goods.getDefaultProductId();
    }
}