package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsFreightTemplateExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer freightTemplateIdNotEquals;

    /**
     * 用于批量操作
     */
    private String freightTemplateIdIn;

    /**
     * 运费模板ID
     */
    private Integer freightTemplateId;

    /**
     * 运费模板名称
     */
    private String templateName;

    /**
     * 运费模板名称,用于模糊查询
     */
    private String templateNameLike;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Integer deliverTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date deliverTimeBefore;

    /**
     * 是否免费配送，1-免费，0-收费
     */
    private String isFree;

    /**
     * 计费方式：1-按件，2-按重量，3-按体积
     */
    private String chargeType;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照freightTemplateId倒序排列
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