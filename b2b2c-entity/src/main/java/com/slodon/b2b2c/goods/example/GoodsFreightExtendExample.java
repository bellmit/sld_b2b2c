package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GoodsFreightExtendExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer freightExtendIdNotEquals;

    /**
     * 用于批量操作
     */
    private String freightExtendIdIn;

    /**
     * 运费模板扩展ID
     */
    private Integer freightExtendId;

    /**
     * 运费模板ID
     */
    private Integer freightTemplateId;

    /**
     * 首件数量(按件，重量，体积）
     */
    private Integer baseNumber;

    /**
     * 首件运费
     */
    private BigDecimal basePrice;

    /**
     * 续件数量(按件，重量，体积）
     */
    private Integer addNumber;

    /**
     * 续件运费
     */
    private BigDecimal addPrice;

    /**
     * 省级和市级地区code组成的json串
     */
    private String provinceInfo;

    /**
     * 市级地区code组成的串，以，隔开
     */
    private String cityCode;

    /**
     * 市级地区name组成的串，以，隔开
     */
    private String cityName;

    /**
     * 市级地区name组成的串，以，隔开,用于模糊查询
     */
    private String cityNameLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照freightExtendId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;
}