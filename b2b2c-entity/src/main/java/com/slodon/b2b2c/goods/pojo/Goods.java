package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品公共信息表（SPU）
 */
@Data
public class Goods implements Serializable {
    private static final long serialVersionUID = -8176065831675767589L;
    @ApiModelProperty("自增物理主键")
    private Long id;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("商品副标题，长度建议140个字符内")
    private String goodsBrief;

    @ApiModelProperty("商品关键字，用于检索商品，用逗号分隔")
    private String keyword;

    @ApiModelProperty("品牌ID")
    private Integer brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("1级分类ID")
    private Integer categoryId1;

    @ApiModelProperty("2级分类ID")
    private Integer categoryId2;

    @ApiModelProperty("3级分类ID")
    private Integer categoryId3;

    @ApiModelProperty("商品所属分类路径，(例如：分类1/分类2/分类3，前后都无斜杠)")
    private String categoryPath;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("商城价销售价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("活动价格，发布单减活动时，设置活动价")
    private BigDecimal promotionPrice;

    @ApiModelProperty("活动类型")
    private Integer promotionType;

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    @ApiModelProperty("虚拟销量")
    private Integer virtualSales;

    @ApiModelProperty("实际销量")
    private Integer actualSales;

    @ApiModelProperty("0-没有启用规格；1-启用规格")
    private Integer isSpec;

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

    @ApiModelProperty("评价总数")
    private Integer commentNumber;

    @ApiModelProperty("是否是虚拟商品：1-实物商品；2-虚拟商品")
    private Integer isVirtualGoods;

    @ApiModelProperty("商品主图路径，每个SPU一张主图")
    private String mainImage;

    @ApiModelProperty("商品视频")
    private String goodsVideo;

    @ApiModelProperty("商品条形码（标准的商品条形码）")
    private String barCode;

    @ApiModelProperty("商品服务标签；用英文逗号分隔（仅作展示使用）")
    private String serviceLabelIds;

    @ApiModelProperty("是否违规下架(0-否，1-是)")
    private Integer isOffline;

    @ApiModelProperty("商品是否被删除：0-否，1-是；商品伪删除")
    private Integer isDelete;

    @ApiModelProperty("商品是否被锁定：0-否，1-是；商品参与活动后被锁定，锁定期间不能编辑，活动结束后解除锁定")
    private Integer isLock;

    @ApiModelProperty("默认货品id")
    private Long defaultProductId;

    @ApiModelProperty("是否可以开具增值税发票0-不可以；1-可以")
    private Integer isVatInvoice;

    @ApiModelProperty("发布时间")
    private Date createTime;

    @ApiModelProperty("更新时间（产品的任何状态更新）")
    private Date updateTime;
}