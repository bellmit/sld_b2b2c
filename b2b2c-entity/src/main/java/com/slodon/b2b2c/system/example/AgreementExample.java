package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AgreementExample implements Serializable {
    private static final long serialVersionUID = -2981773240866738202L;
    /**
     * 用于编辑时的重复判断
     */
    private String agreementCodeNotEquals;

    /**
     * 用于批量操作
     */
    private String agreementCodeIn;

    /**
     * 协议编码
     */
    private String agreementCode;

    /**
     * 标题
     */
    private String title;

    /**
     * 标题,用于模糊查询
     */
    private String titleLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 最后修改人id
     */
    private Integer updateAdminId;

    /**
     * 最后修改人名称
     */
    private String updateAdminName;

    /**
     * 最后修改人名称,用于模糊查询
     */
    private String updateAdminNameLike;

    /**
     * 内容
     */
    private String content;

    /**
     * 内容,用于模糊查询
     */
    private String contentLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照agreementCode倒序排列
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