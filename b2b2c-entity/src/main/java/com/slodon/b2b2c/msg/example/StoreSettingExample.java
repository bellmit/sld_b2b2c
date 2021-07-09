package com.slodon.b2b2c.msg.example;



import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺消息接收设置example
 */
@Data
public class StoreSettingExample implements Serializable {
    private static final long serialVersionUID = 8673300510494871L;
    /**
     * 用于编辑时的重复判断
     */
    private String tplCodeNotEquals;

    /**
     * 用于批量操作
     */
    private String tplCodeIn;

    /**
     * 模板编码
     */
    private String tplCode;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 商户id
     */
    private Long vendorId;

    /**
     * 是否接收：0-关闭；1-开启
     */
    private Integer isReceive;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照tplCode倒序排列
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