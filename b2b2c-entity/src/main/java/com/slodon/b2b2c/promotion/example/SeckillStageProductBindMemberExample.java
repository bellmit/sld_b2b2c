package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀商品与会员绑定关系example
 */
@Data
public class SeckillStageProductBindMemberExample implements Serializable {

    private static final long serialVersionUID = 5211029375890409496L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer bindIdNotEquals;

    /**
     * 用于批量操作
     */
    private String bindIdIn;

    /**
     * 绑定id
     */
    private Integer bindId;

    /**
     * 秒杀商品id
     */
    private Integer stageProductId;

    /**
     * 秒杀活动id
     */
    private Integer seckillId;

    /**
     * 秒杀场次id
     */
    private Integer stageId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 用户名
     */
    private String memberName;

    /**
     * 用户名,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照bindId倒序排列
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