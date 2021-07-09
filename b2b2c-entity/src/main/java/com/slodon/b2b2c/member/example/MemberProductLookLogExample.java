package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberProductLookLogExample implements Serializable {
    private static final long serialVersionUID = 4447539141481703737L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer logIdNotEquals;

    /**
     * 用于批量操作
     */
    private String logIdIn;

    /**
     * 记录id
     */
    private Integer logId;

    /**
     * 会员id，没有登录ID为0
     */
    private Integer memberId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品名称,用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 商品副标题，长度建议140个字符内
     */
    private String goodsBrief;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 货品价格
     */
    private BigDecimal productPrice;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照logId倒序排列
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