package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsBrandExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer brandIdNotEquals;

    /**
     * 用于批量操作
     */
    private String brandIdIn;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌名称,用于模糊查询
     */
    private String brandNameLike;

    /**
     * 品牌描述（一段文字）
     */
    private String brandDesc;

    /**
     * 品牌首字母
     */
    private String brandInitial;

    /**
     * 品牌图片
     */
    private String image;

    /**
     * 1-推荐；0-不推荐
     */
    private Integer isRecommend;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建管理员id(如果是商户申请的即为审核管理员id)
     */
    private Integer createAdminId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 更新管理员id
     */
    private Integer updateAdminId;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 申请商户id(如果为系统后台创建，此项为0)
     */
    private Long applyVendorId;

    /**
     * 申请店铺id(如果为系统后台创建，此项为0)
     */
    private Long applyStoreId;

    /**
     * 审核失败理由，默认为空
     */
    private String failReason;

    /**
     * 审核失败理由，默认为空,用于模糊查询
     */
    private String failReasonLike;

    /**
     * (状态操作只有系统管理员可操作，店铺申请后店铺就不能再操作了)状态 1、系统创建显示中；2、提交审核（待审核，不显示）；3、审核失败；4、删除
     */
    private Integer state;

    /**
     * 三级商品分类id，商家申请品牌绑定
     */
    private Integer goodsCategoryId3;

    /**
     * 分类路径，一级分类名称/二级分类名称/三级分类名称，商家申请品牌绑定
     */
    private String goodsCategoryPath;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照brandId倒序排列
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
     * 店铺名称，用于模糊查询
     */
    private String storeNameLike;

    /**
     * 店铺名称，用于模糊查询
     */
    private String stateNotIn;
}