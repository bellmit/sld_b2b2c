package com.slodon.b2b2c.msg.example;

import java.io.Serializable;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class MemberSettingExample implements Serializable {
    private static final long serialVersionUID = 7964921492298839301L;
    /**
     * 用于编辑时的重复判断
     */
    private String tplCodeNotEquals;

    /**
     * 用于批量操作
     */
    private String tplCodeIn;

    /**
     * 消息模板编号
     */
    private String tplCode;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 是否接收 1是，0否
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