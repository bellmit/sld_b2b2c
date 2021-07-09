package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.StoreGrade;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装续签支付信息VO对象
 */
@Data
public class RenewPayVO {

    @ApiModelProperty("店铺等级id")
    private Integer gradeId;

    @ApiModelProperty("店铺等级名称")
    private String gradeName;

    @ApiModelProperty("收费标准（每年）")
    private String price;

    @ApiModelProperty("续签时长（年）")
    private Integer duration;

    @ApiModelProperty("付款金额（元）")
    private BigDecimal payAmount;

    @ApiModelProperty("可推荐商品数")
    private Integer recommendLimit;

    @ApiModelProperty("可发布商品数")
    private Integer goodsLimit;

    @ApiModelProperty("申请说明")
    private String description;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("续签id")
    private Integer renewId;

    public RenewPayVO(StoreGrade storeGrade) {
        gradeId = storeGrade.getGradeId();
        gradeName = storeGrade.getGradeName();
        price = storeGrade.getPrice();
        goodsLimit = storeGrade.getGoodsLimit();
        recommendLimit = storeGrade.getRecommendLimit();
        description = storeGrade.getDescription();
    }
}
