package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品表example
 */
@Data
public class SeckillStageProductExample implements Serializable {

    private static final long serialVersionUID = -2313503934332847793L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer stageProductIdNotEquals;

    /**
     * 用于批量操作
     */
    private String stageProductIdIn;

    /**
     * id
     */
    private Integer stageProductId;

    /**
     * 大于等于开始时间
     */
    private Date startTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date startTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date endTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date endTimeBefore;

    /**
     * 货品ID
     */
    private Long productId;

    /**
     * 货品ID 用于批量操作
     */
    private String productIdIn;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品ID 用于批量操作
     */
    private String goodsIdIn;

    /**
     * 商品主图路径，每个SPU一张主图
     */
    private String mainImage;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品名称,用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 商家ID
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
     * 货品价格
     */
    private BigDecimal productPrice;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 货品秒杀当前库存
     */
    private Integer seckillStock;

    /**
     * 限购数量
     */
    private Integer upperLimit;

    /**
     * 已购买人数
     */
    private Integer buyerCount;

    /**
     * 购买数量
     */
    private Integer buyQuantity;

    /**
     * 状态 1待审核 2审核通过，3拒绝
     */
    private Integer verifyState;

    /**
     * verifyStateIn，用于批量操作
     */
    private String verifyStateIn;

    /**
     * verifyStateNotIn，用于批量操作
     */
    private String verifyStateNotIn;

    /**
     * verifyStateNotEquals，用于批量操作
     */
    private Integer verifyStateNotEquals;

    /**
     * 审核拒绝理由
     */
    private String remark;

    /**
     * 活动id
     */
    private Integer seckillId;

    /**
     * 活动名称
     */
    private String seckillName;

    /**
     * 活动名称,用于模糊查询
     */
    private String seckillNameLike;

    /**
     * 场次id
     */
    private Integer stageId;

    /**
     * 场次id
     */
    private String stageIdIn;

    /**
     * 场次名称
     */
    private String stageName;

    /**
     * 场次名称,用于模糊查询
     */
    private String stageNameLike;

    /**
     * 标签id
     */
    private Integer labelId;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 标签名称,用于模糊查询
     */
    private String labelNameLike;

    /**
     * 货品库存
     */
    private Integer productStock;

    /**
     * 秒杀活动状态(1-未开始 2-进行中 3-已结束)
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
     * 排序条件，条件之间用逗号隔开，如果不传则按照stageProductId倒序排列
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