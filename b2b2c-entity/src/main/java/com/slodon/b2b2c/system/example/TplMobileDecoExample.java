package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TplMobileDecoExample implements Serializable {
    private static final long serialVersionUID = 4152058320959944053L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer decoIdNotEquals;

    /**
     * 用于批量操作
     */
    private String decoIdIn;

    /**
     * 装修页id
     */
    private Integer decoId;

    /**
     * 装修页名称
     */
    private String name;

    /**
     * 装修页名称,用于模糊查询
     */
    private String nameLike;

    /**
     * 装修页类型(首页home/专题topic/店铺seller/活动activity)
     */
    private String type;

    /**
     * 店铺id,0==平台
     */
    private Long storeId;

    /**
     * 是否启用
     */
    private Integer android;

    /**
     * 是否启用
     */
    private Integer ios;

    /**
     * 是否启用
     */
    private Integer h5;

    /**
     * 是否启用
     */
    private Integer weixinXcx;

    /**
     * 是否启用
     */
    private Integer alipayXcx;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建人名称,用于模糊查询
     */
    private String createUserNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 更新人id
     */
    private Long updateUserId;

    /**
     * 更新人名称
     */
    private String updateUserName;

    /**
     * 更新人名称,用于模糊查询
     */
    private String updateUserNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 装修页数据
     */
    private String data;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照decoId倒序排列
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
     * 查询数据
     */
    private String queryData;
}