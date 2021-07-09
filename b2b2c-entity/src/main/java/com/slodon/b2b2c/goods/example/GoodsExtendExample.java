package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsExtendExample implements Serializable {
    private static final long serialVersionUID = 9040861231121296664L;
    /**
     * 用于编辑时的重复判断
     */
    private Long extendIdNotEquals;

    /**
     * 用于批量操作
     */
    private String extendIdIn;

    /**
     * 自增物理主键
     */
    private Long id;

    /**
     * 商品扩展ID
     */
    private Long extendId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品主规格；0-无主规格，其他id为对应的主规格ID，主规格值切换商品主图会切换
     */
    private Integer mainSpecId;

    /**
     * 商品规格json（商品发布使用的规格及规格值信息）
     */
    private String specJson;

    /**
     * 商品属性json（商品发布使用的属性信息，系统、自定义）
     */
    private String attributeJson;

    /**
     * 大于等于开始时间
     */
    private Date onlineTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date onlineTimeBefore;

    /**
     * 创建人
     */
    private Long createVendorId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 审核失败原因
     */
    private String auditReason;

    /**
     * 审核失败原因,用于模糊查询
     */
    private String auditReasonLike;

    /**
     * 商品审核失败附加备注信息
     */
    private String auditComment;

    /**
     * 被收藏数量
     */
    private Integer followNumber;

    /**
     * 点击量
     */
    private Integer clickNumber;

    /**
     * 违规下架原因
     */
    private String offlineReason;

    /**
     * 违规下架原因,用于模糊查询
     */
    private String offlineReasonLike;

    /**
     * 违规下架备注信息
     */
    private String offlineComment;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 运费模板id(与固定运费二选一,必有一项)
     */
    private Integer freightId;

    /**
     * 固定运费(与运费模版id二选一,必有一项)
     */
    private BigDecimal freightFee;

    /**
     * 顶部关联模版ID
     */
    private Integer relatedTemplateIdTop;

    /**
     * 底部关联模版ID
     */
    private Integer relatedTemplateIdBottom;

    /**
     * 商品详情信息
     */
    private String goodsDetails;

    /**
     * 商品参数，json格式，商品详情顶部显示
     */
    private String goodsParameter;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照extendId倒序排列
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
     * 用于批量操作
     */
    private String goodsIdIn;

    /**
     * 店铺id
     */
    private Long storeId;
}