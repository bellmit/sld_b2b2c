package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分商品表（SPU）
 */
@Data
public class IntegralGoods implements Serializable {
    private static final long serialVersionUID = -7350066943822926098L;
    @ApiModelProperty("自增物理主键")
    private Long id;

    @ApiModelProperty("商品ID")
    private Long integralGoodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("商品副标题，长度建议140个字符内")
    private String goodsBrief;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
    private Integer integralPrice;

    @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
    private BigDecimal cashPrice;

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    @ApiModelProperty("虚拟销量")
    private Integer virtualSales;

    @ApiModelProperty("实际销量")
    private Integer actualSales;

    @ApiModelProperty("商品主规格；0-无主规格，其他id为对应的主规格ID，主规格值切换商品主图会切换")
    private Integer mainSpecId;

    @ApiModelProperty("1-自营；2-商家")
    private Integer isSelf;

    @ApiModelProperty("状态：11-放入仓库无需审核；12-放入仓库审核通过；20-立即上架待审核；21-放入仓库待审核；3-上架（a. 审核通过上架，b. 不需要平台审核，商户创建商品后点击上架操作）；4-审核驳回(平台驳回）；5-商品下架（商户自行下架）；6-违规下架（平台违规下架操作）；7-已删除（状态1、5、6可以删除后进入此状态）")
    private Integer state;

    @ApiModelProperty("0-不推荐；1-推荐（平台是否推荐）")
    private Integer isRecommend;

    @ApiModelProperty("商品上架时间（a. 不需要平台审核时即为商户提交上架的时间，b. 需要审核时为平台审核通过的时间）")
    private Date onlineTime;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("商品推荐，0-不推荐；1-推荐（店铺内是否推荐）")
    private Integer storeIsRecommend;

    @ApiModelProperty("店铺状态：1-店铺正常；0-店铺关闭 默认状态为1")
    private Integer storeState;

    @ApiModelProperty("商品主图路径，每个SPU一张主图")
    private String mainImage;

    @ApiModelProperty("商品视频")
    private String goodsVideo;

    @ApiModelProperty("默认货品id")
    private Long defaultProductId;

    @ApiModelProperty("是否可以开具增值税发票0-不可以；1-可以")
    private Integer isVatInvoice;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("创建人")
    private Long createVendorId;

    @ApiModelProperty("发布时间")
    private Date createTime;

    @ApiModelProperty("更新时间（产品的任何状态更新）")
    private Date updateTime;

    @ApiModelProperty("审核失败原因")
    private String auditReason;

    @ApiModelProperty("商品审核失败附加备注信息")
    private String auditComment;

    @ApiModelProperty("违规下架原因")
    private String offlineReason;

    @ApiModelProperty("违规下架备注信息")
    private String offlineComment;

    @ApiModelProperty("商品规格json（商品发布使用的规格及规格值信息）")
    private String specJson;

    @ApiModelProperty("商品详情信息")
    private String goodsDetails;
}