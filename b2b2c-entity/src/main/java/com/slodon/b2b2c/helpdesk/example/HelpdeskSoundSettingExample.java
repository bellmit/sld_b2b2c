package com.slodon.b2b2c.helpdesk.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 客服新消息声音设置表example
 */
@Data
public class HelpdeskSoundSettingExample implements Serializable {
    private static final long serialVersionUID = -1623841701930702084L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer settingIdNotEquals;

    /**
     * 用于批量操作
     */
    private String settingIdIn;

    /**
     * 设置id
     */
    private Integer settingId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 商户id
     */
    private Long vendorId;

    /**
     * 是否开启：0-关闭；1-开启
     */
    private Integer isOpen;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照settingId倒序排列
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