package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoreNavigationExample implements Serializable {
    private static final long serialVersionUID = -9054898731515847996L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer navIdNotEquals;

    /**
     * 用于批量操作
     */
    private String navIdIn;

    /**
     * 导航id
     */
    private Integer navId;

    /**
     * 导航名称
     */
    private String navName;

    /**
     * 导航名称,用于模糊查询
     */
    private String navNameLike;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否显示，0：否；1：是（默认）
     */
    private Integer isShow;

    /**
     * 链接数据，json类型，包含链接类型、链接地址等
     */
    private String data;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照navId倒序排列
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