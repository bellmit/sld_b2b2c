package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品扩展信息表
 */
@Data
public class GoodsExtend implements Serializable {
    private static final long serialVersionUID = -2008830519901173957L;
    @ApiModelProperty("自增物理主键")
    private Long id;

    @ApiModelProperty("商品扩展ID")
    private Long extendId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品主规格；0-无主规格，其他id为对应的主规格ID，主规格值切换商品主图会切换")
    private Integer mainSpecId;

    @ApiModelProperty("商品规格json（商品发布使用的规格及规格值信息）")
    private String specJson;

    @ApiModelProperty("商品属性json（商品发布使用的属性信息，系统、自定义）")
    private String attributeJson;

    @ApiModelProperty("商品上架时间（a. 不需要平台审核时即为商户提交上架的时间，b. 需要审核时为平台审核通过的时间）")
    private Date onlineTime;

    @ApiModelProperty("创建人")
    private Long createVendorId;

    @ApiModelProperty("商品创建时间")
    private Date createTime;

    @ApiModelProperty("审核失败原因")
    private String auditReason;

    @ApiModelProperty("商品审核失败附加备注信息")
    private String auditComment;

    @ApiModelProperty("被收藏数量")
    private Integer followNumber;

    @ApiModelProperty("点击量")
    private Integer clickNumber;

    @ApiModelProperty("违规下架原因")
    private String offlineReason;

    @ApiModelProperty("违规下架备注信息")
    private String offlineComment;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("运费模板id(与固定运费二选一,必有一项)")
    private Integer freightId;

    @ApiModelProperty("固定运费(与运费模版id二选一,必有一项)")
    private BigDecimal freightFee;

    @ApiModelProperty("顶部关联模版ID")
    private Integer relatedTemplateIdTop;

    @ApiModelProperty("底部关联模版ID")
    private Integer relatedTemplateIdBottom;

    @ApiModelProperty("商品详情信息")
    private String goodsDetails;

    @ApiModelProperty("商品参数，json格式，商品详情顶部显示")
    private String goodsParameter;
}