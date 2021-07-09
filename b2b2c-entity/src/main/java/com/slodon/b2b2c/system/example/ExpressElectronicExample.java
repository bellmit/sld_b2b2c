package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExpressElectronicExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer electronicIdNotEquals;

    /**
     * 用于批量操作
     */
    private String electronicIdIn;

    /**
     * 主键ID
     */
    private Integer electronicId;

    /**
     * 物流id
     */
    private Integer expressId;

    /**
     * 物流名称
     */
    private String expressName;

    /**
     * 物流名称,用于模糊查询
     */
    private String expressNameLike;

    /**
     * 物流编号
     */
    private String expressCode;

    /**
     * 状态是否启用：1-启用，0-不启用
     */
    private String expressState;

    /**
     * 公司网址
     */
    private String expressWebsite;

    /**
     * 是否需要平台商户申请客户号：1-需要，0-不需要
     */
    private Integer isApply;

    /**
     * 客户帐号
     */
    private String customerName;

    /**
     * 客户帐号,用于模糊查询
     */
    private String customerNameLike;

    /**
     * 客户密码
     */
    private String customerPwd;

    /**
     * 月结号
     */
    private String mouthCode;

    /**
     * 所属网点
     */
    private String sendSite;

    /**
     * 所属人员
     */
    private String sendStaff;

    /**
     * 物流图片
     */
    private String expressImg;

    /**
     * 电子面单模板大小,参考快递鸟模板样式表(非必填)
     */
    private String templateSize;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照electronicId倒序排列
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