package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberFollowStoreExample implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 会员ID
     */
    private Integer memberId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺ID
     * 用于批量错做
     */
    private String storeIdIn;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 店铺LOGO
     */
    private String storeLogo;

    /**
     * 店铺所属分类ID
     */
    private Integer storeCategoryId;

    /**
     * 店铺所属分类名称
     */
    private String storeCategoryName;

    /**
     * 店铺所属分类名称,用于模糊查询
     */
    private String storeCategoryNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 是否置顶：0、不置顶；1、置顶
     */
    private Integer isTop;

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