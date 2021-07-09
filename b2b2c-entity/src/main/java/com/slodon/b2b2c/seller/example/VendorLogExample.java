package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VendorLogExample implements Serializable {
    private static final long serialVersionUID = -7831248536691973047L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer logIdNotEquals;

    /**
     * 用于批量操作
     */
    private String logIdIn;

    /**
     * 日志id
     */
    private Integer logId;

    /**
     * 商家用户id
     */
    private Long vendorId;

    /**
     * 商家用户名称
     */
    private String vendorName;

    /**
     * 商家用户名称,用于模糊查询
     */
    private String vendorNameLike;

    /**
     * 操作URL
     */
    private String operationUrl;

    /**
     * 操作行为
     */
    private String operationContent;

    /**
     * 操作行为,用于模糊查询
     */
    private String operationContentLike;

    /**
     * 大于等于开始时间
     */
    private Date optTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date optTimeBefore;

    /**
     * ip
     */
    private String ip;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照logId倒序排列
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
     * 店铺id
     */
    private Long storeId;

}