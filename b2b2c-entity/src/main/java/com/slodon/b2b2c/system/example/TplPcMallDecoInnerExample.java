package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 平台首页内置模版example
 */
@Data
public class TplPcMallDecoInnerExample implements Serializable {
    private static final long serialVersionUID = -3841686413767726905L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer decoIdNotEquals;

    /**
     * 用于批量操作
     */
    private String decoIdIn;

    /**
     * 商城装修页面组装信息id
     */
    private Integer decoId;

    /**
     * 装修页类型
     */
    private String decoType;

    /**
     * 装修页名称
     */
    private String decoName;

    /**
     * 装修页名称,用于模糊查询
     */
    private String decoNameLike;

    /**
     * 装修页主导航条数据id，无则为0
     */
    private Integer masterNavigationBarId;

    /**
     * 装修页主轮播图数据id，无则为0
     */
    private Integer masterBannerId;

    /**
     * 有序模板数据id集合，按存储顺序确定位置
     */
    private String rankedTplDataIds;

    /**
     * 是否启用该装修页；0==不启用，1==启用
     */
    private Integer isEnable;

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
     * 店铺id(0为平台)
     */
    private Long storeId;

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
}