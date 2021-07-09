package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品对应属性表(保存商品时插入)example
 */
@Data
public class GoodsBindAttributeValueExample implements Serializable {
    private static final long serialVersionUID = -909041650599184695L;
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
     * 商品ID
     */
    private Long goodsId;

    /**
     * 检索属性ID
     */
    private Integer attributeId;

    /**
     * 属性名称
     */
    private String attributeName;

    /**
     * 属性名称,用于模糊查询
     */
    private String attributeNameLike;

    /**
     * 属性值ID
     */
    private Integer attributeValueId;

    /**
     * 属性值
     */
    private String attributeValue;

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