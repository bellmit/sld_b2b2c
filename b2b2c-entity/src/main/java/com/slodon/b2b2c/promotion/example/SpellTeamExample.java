package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpellTeamExample implements Serializable {
    private static final long serialVersionUID = -7657751389914492224L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer spellTeamIdNotEquals;

    /**
     * 用于批量操作
     */
    private String spellTeamIdIn;

    /**
     * 拼团活动团队id
     */
    private Integer spellTeamId;

    /**
     * 拼团活动id
     */
    private Integer spellId;

    /**
     * 拼团活动名称
     */
    private String spellName;

    /**
     * 拼团活动名称,用于模糊查询
     */
    private String spellNameLike;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 活动商品id（spu）
     */
    private Long goodsId;

    /**
     * 活动商品名称
     */
    private String goodsName;

    /**
     * 活动商品名称,用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 大于等于开团开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于开团结束时间
     */
    private Date createTimeBefore;

    /**
     * 大于等于截团开始时间
     */
    private Date endTimeAfter;

    /**
     * 小于等于截团结束时间
     */
    private Date endTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date finishTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date finishTimeBefore;

    /**
     * 成团类型：1-自助成团；2-虚拟成团（店铺操作完成）
     */
    private Integer finishType;

    /**
     * 团长id
     */
    private Integer leaderMemberId;

    /**
     * 团长名称
     */
    private String leaderMemberName;

    /**
     * 团长名称,用于模糊查询
     */
    private String leaderMemberNameLike;

    /**
     * 团长支付状态：0-未支付；1-已支付
     */
    private Integer leaderPayState;

    /**
     * 要求成团人数
     */
    private Integer requiredNum;

    /**
     * 已参团人数
     */
    private Integer joinedNum;

    /**
     * 团状态(1-进行中；2-成功；3-失败）
     */
    private Integer state;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照spellTeamId倒序排列
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
     * 订单号
     */
    private String orderSnLike;

    /**
     * 会员名称
     */
    private String memberNameLike;

    /**
     * 大于等于下单开始时间
     */
    private Date orderStartTime;

    /**
     * 小于等于下单结束时间
     */
    private Date orderEndTime;

}