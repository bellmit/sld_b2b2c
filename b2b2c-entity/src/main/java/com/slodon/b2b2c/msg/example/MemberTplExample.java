package com.slodon.b2b2c.msg.example;


import java.io.Serializable;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class MemberTplExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private String tplCodeNotEquals;

    /**
     * 用于批量操作
     */
    private String tplCodeIn;

    /**
     * 用户消息模板编号
     */
    private String tplCode;

    /**
     * 模板名称
     */
    private String tplName;

    /**
     * 模板名称,用于模糊查询
     */
    private String tplNameLike;

    /**
     * 模板类型
     */
    private String tplTypeCode;

    /**
     * 短信开关：0-关闭；1-开启
     */
    private Integer smsSwitch;

    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 短信内容,用于模糊查询
     */
    private String smsContentLike;

    /**
     * 邮件开关：0-关闭；1-开启
     */
    private Integer emailSwitch;

    /**
     * 站内信开关：0-关闭；1-开启
     */
    private Integer msgSwitch;

    /**
     * 微信开关：0-关闭；1-开启
     */
    private Integer wxSwitch;

    /**
     * 邮件内容
     */
    private String emailContent;

    /**
     * 邮件内容,用于模糊查询
     */
    private String emailContentLike;

    /**
     * 站内信内容
     */
    private String msgContent;

    /**
     * 站内信内容,用于模糊查询
     */
    private String msgContentLike;

    /**
     * 微信内容
     */
    private String wxContent;

    /**
     * 微信内容,用于模糊查询
     */
    private String wxContentLike;

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