package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.StoreGrade;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装店铺等级VO对象
 */
@Data
public class StoreGradeVO {

    @ApiModelProperty("等级ID")
    private Integer gradeId;

    @ApiModelProperty("等级名称")
    private String gradeName;

    @ApiModelProperty("可推荐商品数")
    private Integer recommendLimit;

    @ApiModelProperty("可发布商品数")
    private Integer goodsLimit;

    @ApiModelProperty("收费标准（每年）")
    private String price;

    @ApiModelProperty("申请说明")
    private String description;

    @ApiModelProperty("级别,数目越大级别越高")
    private Integer sort;

    public StoreGradeVO(StoreGrade storeGrade) {
        gradeId = storeGrade.getGradeId();
        gradeName = storeGrade.getGradeName();
        goodsLimit = storeGrade.getGoodsLimit();
        price = storeGrade.getPrice();
        sort = storeGrade.getSort();
        recommendLimit = storeGrade.getRecommendLimit();
        description = storeGrade.getDescription();
    }
}
