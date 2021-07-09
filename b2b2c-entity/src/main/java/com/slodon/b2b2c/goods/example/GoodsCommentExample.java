package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品评论管理example
 */
@Data
public class GoodsCommentExample implements Serializable {
    private static final long serialVersionUID = -5927965608924534008L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer commentIdNotEquals;

    /**
     * 用于批量操作
     */
    private String commentIdIn;

    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 评价人ID
     */
    private Integer memberId;

    /**
     * 评价人账号
     */
    private String memberName;

    /**
     * 评价人账号,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 评分(1到5)
     */
    private Integer score;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价内容,用于模糊查询
     */
    private String contentLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称为3到50个字符(商品副标题)
     */
    private String goodsName;

    /**
     * 商品名称为3到50个字符(商品副标题),用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 货品ID
     */
    private Long productId;

    /**
     * 商品规格集合
     */
    private String specValues;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单编号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 订单货品明细ID
     */
    private Long orderProductId;

    /**
     * 回复人ID
     */
    private Integer replyId;

    /**
     * 回复人名称
     */
    private String replyName;

    /**
     * 回复人名称,用于模糊查询
     */
    private String replyNameLike;

    /**
     * 回复内容
     */
    private String replyContent;

    /**
     * 回复内容,用于模糊查询
     */
    private String replyContentLike;

    /**
     * 状态：1、评价；2、审核通过，前台显示；3、删除
     */
    private Integer state;

    /**
     * stateIn，用于批量操作
     */
    private String stateIn;

    /**
     * stateNotIn，用于批量操作
     */
    private String stateNotIn;

    /**
     * stateNotEquals，用于批量操作
     */
    private Integer stateNotEquals;

    /**
     * 审核上架人
     */
    private Integer adminId;

    /**
     * 评价上传图片
     */
    private String image;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照commentId倒序排列
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

    /**
     * 等级名称，用于判断等级范围
     */
    private String gradeName;

    /**
     * 等级字符串 批量查询
     */
    private String scores;
}