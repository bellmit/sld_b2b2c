package com.slodon.b2b2c.helpdesk.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class HelpdeskTransferExample implements Serializable {
    private static final long serialVersionUID = 6417882750946144624L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer transferIdNotEquals;

    /**
     * 用于批量操作
     */
    private String transferIdIn;

    /**
     * 转接ID
     */
    private Integer transferId;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 源vendor_id
     */
    private Long srcVendorId;

    /**
     * 转接后vendor_id
     */
    private Long dstVendorId;

    /**
     * 大于等于开始时间
     */
    private Date switchTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date switchTimeBefore;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照transferId倒序排列
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