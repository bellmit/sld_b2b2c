package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpellTeamMemberExample implements Serializable {
    private static final long serialVersionUID = 5912431134748630242L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer spellTeamMemberIdNotEquals;

    /**
     * 用于批量操作
     */
    private String spellTeamMemberIdIn;

    /**
     * 拼团活动团队成员id
     */
    private Integer spellTeamMemberId;

    /**
     * 拼团活动团队id
     */
    private Integer spellTeamId;

    /**
     * 拼团活动id
     */
    private Integer spellId;

    /**
     * 拼团商品id
     */
    private Integer spellGoodsId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单编号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 活动商品id（sku）
     */
    private Long productId;

    /**
     * 参团会员id
     */
    private Integer memberId;

    /**
     * 参团会员名称
     */
    private String memberName;

    /**
     * 参团会员名称,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 会员头像
     */
    private String memberAvatar;

    /**
     * 团长标识(0-非团长；1-团长）
     */
    private Integer isLeader;

    /**
     * 大于等于开始时间
     */
    private Date participateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date participateTimeBefore;

    /**
     * 支付状态：0-未支付；1-已支付
     */
    private Integer payState;

    /**
     * payStateIn，用于批量操作
     */
    private String payStateIn;

    /**
     * payStateNotIn，用于批量操作
     */
    private String payStateNotIn;

    /**
     * payStateNotEquals，用于批量操作
     */
    private Integer payStateNotEquals;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照spellTeamMemberId倒序排列
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