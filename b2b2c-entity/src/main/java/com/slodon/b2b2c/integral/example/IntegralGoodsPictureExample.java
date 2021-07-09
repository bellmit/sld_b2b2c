package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品对应图片表example
 */
@Data
public class IntegralGoodsPictureExample implements Serializable {
    private static final long serialVersionUID = -3367432480253912981L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer pictureIdNotEquals;

    /**
     * 用于批量操作
     */
    private String pictureIdIn;

    /**
     * 图片id
     */
    private Integer pictureId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品主spec_value_id，对于没有启用主规格的此值为0
     */
    private Integer specValueId;

    /**
     * 商品主spec_value_id，对于没有启用主规格的此值为0
     */
    private String specValueIdIn;

    /**
     * 图片路径
     */
    private String imagePath;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 上传人
     */
    private Long createId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 是否主图：1、主图；2、非主图，主图只能有一张
     */
    private Integer isMain;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照pictureId倒序排列
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