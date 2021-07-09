package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberFollowProductExample implements Serializable {
    private static final long serialVersionUID = -5300596748864783096L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer followIdNotEquals;

    /**
     * 用于批量操作
     */
    private String followIdIn;

    /**
     * 收藏id
     */
    private Integer followId;

    /**
     * 用户ID
     */
    private Integer memberId;

    /**
     * 货品ID
     */
    private Long productId;

    /**
     * 货品ID
     * 用于批量操作
     */
    private String productIdIn;

    /**
     * 收藏商品时的价格
     */
    private BigDecimal productPrice;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品副标题，长度建议140个字符内
     */
    private String goodsBrief;

    /**
     * 商品名称,用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 收藏店铺id
     */
    private Long storeId;

    /**
     * 收藏店铺名称
     */
    private String storeName;

    /**
     * 收藏店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 商品的分类id（三级分类ID）
     */
    private Integer goodsCategoryId;

    /**
     * 商品关联的店铺内部分类id
     */
    private Integer storeCategoryId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照followId倒序排列
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