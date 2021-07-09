package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberGoodsAskExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer askIdNotEquals;

    /**
     * 用于批量操作
     */
    private String askIdIn;

    /**
     * 咨询id
     */
    private Integer askId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 咨询人ID
     */
    private Integer memberId;

    /**
     * 咨询人账号
     */
    private String memberName;

    /**
     * 咨询人账号,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 咨询内容
     */
    private String askContent;

    /**
     * 咨询内容,用于模糊查询
     */
    private String askContentLike;

    /**
     * 回复人ID
     */
    private Long replyVendorId;

    /**
     * 回复人账号
     */
    private String replyVendorName;

    /**
     * 回复人账号,用于模糊查询
     */
    private String replyVendorNameLike;

    /**
     * 回复内容
     */
    private String replyContent;

    /**
     * 回复内容,用于模糊查询
     */
    private String replyContentLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date replyTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date replyTimeBefore;

    /**
     * 状态：1、咨询；2、已经回答；3、前台显示；4、删除
     */
    private Integer state;

    /**
     * 是否匿名提问：0、真实姓名，1、匿名
     */
    private Integer isAnonymous;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照askId倒序排列
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