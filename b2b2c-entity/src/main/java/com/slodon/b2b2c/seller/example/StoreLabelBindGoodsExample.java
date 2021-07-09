package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StoreLabelBindGoodsExample implements Serializable {
    private static final long serialVersionUID = 7107280040568445185L;

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
     * 店铺内商品分类ID
     */
    private Integer innerLabelId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 大于等于开始时间
     */
    private Date bindTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date bindTimeBefore;

    /**
     * 绑定人ID
     */
    private Long bindVendorId;

    /**
     * 绑定人名字
     */
    private String bindVendorName;

    /**
     * 绑定人名字,用于模糊查询
     */
    private String bindVendorNameLike;

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