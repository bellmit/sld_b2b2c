package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.integral.pojo.IntegralGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 封装积分商品VO对象
 */
@Data
public class IntegralGoodsVO implements Serializable {

    private static final long serialVersionUID = -3240300971214917203L;
    @ApiModelProperty("商品ID")
    private Long integralGoodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("商品副标题")
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

    @ApiModelProperty("11-放入仓库无需审核;12-放入仓库审核通过;20-立即上架待审核;21-放入仓库待审核；3-上架（a. 审核通过上架，b. 不需要平台审核，商户创建商品后点击上架操作）；4-审核驳回(平台驳回）；5-商品下架（商户自行下架）；6-违规下架（平台违规下架操作）；7-已删除（状态1、5、6可以删除后进入此状态）")
    private Integer state;

    @ApiModelProperty("11-放入仓库无需审核;12-放入仓库审核通过;20-立即上架待审核;21-放入仓库待审核；3-上架（a. 审核通过上架，b. 不需要平台审核，商户创建商品后点击上架操作）；4-审核驳回(平台驳回）；5-商品下架（商户自行下架）；6-违规下架（平台违规下架操作）；7-已删除（状态1、5、6可以删除后进入此状态）")
    private String stateValue;

    @ApiModelProperty("商品主图路径，每个SPU一张主图")
    private String mainImage;

    @ApiModelProperty("商品创建时间")
    private Date createTime;

    @ApiModelProperty("是否已经预警过 1==否 2==是")
    private Integer productStockWarningState;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("商品推荐，0-不推荐；1-推荐（店铺内是否推荐）")
    private Integer storeIsRecommend;

    @ApiModelProperty("违规下架原因")
    private String offlineReason;

    @ApiModelProperty("违规下架备注信息")
    private String offlineComment;

    @ApiModelProperty("默认货品id")
    private Long productId;

    @ApiModelProperty("审核拒绝理由")
    private String auditReason;

    @ApiModelProperty("审核拒绝备注")
    private String auditComment;

    @ApiModelProperty("是否达到预警值，true==是")
    private Boolean warning;

    @ApiModelProperty("规格列表")
    private List<IntegralProductVO> productList;

    public IntegralGoodsVO(IntegralGoods integralGoods) {
        this.integralGoodsId = integralGoods.getIntegralGoodsId();
        this.goodsName = integralGoods.getGoodsName();
        this.goodsBrief = integralGoods.getGoodsBrief();
        this.marketPrice = integralGoods.getMarketPrice();
        this.integralPrice = integralGoods.getIntegralPrice();
        this.cashPrice = integralGoods.getCashPrice();
        this.state = integralGoods.getState();
        this.stateValue = dealStateValue(integralGoods.getState());
        this.virtualSales = integralGoods.getVirtualSales();
        this.actualSales = integralGoods.getActualSales();
        this.mainImage = FileUrlUtil.getFileUrl(integralGoods.getMainImage(), null);
        this.createTime = integralGoods.getCreateTime();
        this.storeId = integralGoods.getStoreId();
        this.storeName = integralGoods.getStoreName();
        this.storeIsRecommend = integralGoods.getStoreIsRecommend();
        this.offlineReason = integralGoods.getOfflineReason();
        this.offlineComment = integralGoods.getOfflineComment();
        this.productId = integralGoods.getDefaultProductId();
        this.auditReason = integralGoods.getAuditReason();
        this.auditComment = integralGoods.getAuditComment();
    }

    public static String dealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return Language.translate("未知");
        switch (state) {
            case GoodsConst.GOODS_STATE_UPPER:
                value = "在售";
                break;
            case GoodsConst.GOODS_STATE_WAREHOUSE_NO_AUDIT:
            case GoodsConst.GOODS_STATE_WAREHOUSE_AUDIT_PASS:
            case GoodsConst.GOODS_STATE_LOWER_BY_STORE:
                value = "仓库中";
                break;
            case GoodsConst.GOODS_STATE_LOWER_BY_SYSTEM:
                value = "违规下架";
                break;
            case GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT:
                value = "待审核";
                break;
            case GoodsConst.GOODS_STATE_REJECT:
                value = "审核拒绝";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public Integer getGoodsStock() {
        if (CollectionUtils.isEmpty(productList)) {
            return 0;
        }
        return productList.stream().mapToInt(IntegralProductVO::getProductStock).sum();
    }
}
