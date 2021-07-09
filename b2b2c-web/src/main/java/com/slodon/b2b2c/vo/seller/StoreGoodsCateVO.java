package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.StoreBindCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装经营类目VO对象
 */
@Data
public class StoreGoodsCateVO {

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("申请分类id（一级）")
    private Integer goodsCategoryId1;

    @ApiModelProperty("申请分类id（二级）")
    private Integer goodsCategoryId2;

    @ApiModelProperty("申请分类id（三级）")
    private Integer goodsCategoryId3;

    @ApiModelProperty("申请分类名称,提交类目组合")
    private String goodsCateName;

    @ApiModelProperty("分佣比例")
    private BigDecimal scaling;

    @ApiModelProperty("申请分类名称（一级）")
    private String goodsCateName1;

    @ApiModelProperty("申请分类名称（二级）")
    private String goodsCateName2;

    @ApiModelProperty("申请分类名称（三级）")
    private String goodsCateName3;

    public StoreGoodsCateVO(StoreBindCategory storeBindCategory) {
        bindId = storeBindCategory.getBindId();
        goodsCategoryId1 = storeBindCategory.getGoodsCategoryId1();
        goodsCategoryId2 = storeBindCategory.getGoodsCategoryId2();
        goodsCategoryId3 = storeBindCategory.getGoodsCategoryId3();
        goodsCateName = storeBindCategory.getGoodsCateName();
        scaling = storeBindCategory.getScaling();
    }
}
