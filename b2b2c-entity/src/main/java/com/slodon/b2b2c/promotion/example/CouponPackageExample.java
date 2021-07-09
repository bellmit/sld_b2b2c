package com.slodon.b2b2c.promotion.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class CouponPackageExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer packageIdNotEquals;

    /**
     * 用于批量操作
     */
    private String packageIdIn;

    /**
     * 优惠券礼包ID
     */
    private Integer packageId;

    /**
     * 优惠券礼包名称
     */
    private String packageName;

    /**
     * 优惠券礼包名称,用于模糊查询
     */
    private String packageNameLike;

    /**
     * 优惠券礼包说明
     */
    private String description;

    /**
     * 优惠券类型(1-注册礼包；2-普通礼包；3-生日礼包）
     */
    private Integer packageType;

    /**
     * 统计信息-已经领取数量
     */
    private Integer receivedNum;

    /**
     * 发行限制-发行数量
     */
    private Integer publishNum;

    /**
     * 大于等于开始时间
     */
    private Date publishStartTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date publishStartTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date publishEndTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date publishEndTimeBefore;

    /**
     * 礼包状态(1-正常；2-失效；3-删除）
     */
    private Integer state;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 如果store_id=0即为平台管理员，否则为vendor用户创建
     */
    private Integer createUserId;

    /**
     * 创建用户名
     */
    private String createUser;

    /**
     * 优惠券id列表；逗号分隔
     */
    private String couponIds;

    /**
     * 主题颜色
     */
    private String themeColor;

    /**
     * PC端活动图
     */
    private String pcActDiagram;

    /**
     * APP端活动图
     */
    private String appActDiagram;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照packageId倒序排列
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