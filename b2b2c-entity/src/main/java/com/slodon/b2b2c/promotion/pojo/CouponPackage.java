package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）
 */
@Data
public class CouponPackage implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("优惠券礼包ID")
    private Integer packageId;

    @ApiModelProperty("优惠券礼包名称")
    private String packageName;

    @ApiModelProperty("优惠券礼包说明")
    private String description;

    @ApiModelProperty("优惠券类型(1-注册礼包；2-普通礼包；3-生日礼包）")
    private Integer packageType;

    @ApiModelProperty("统计信息-已经领取数量")
    private Integer receivedNum;

    @ApiModelProperty("发行限制-发行数量")
    private Integer publishNum;

    @ApiModelProperty("发行限制-领取开始时间")
    private Date publishStartTime;

    @ApiModelProperty("发行限制-领取结束时间")
    private Date publishEndTime;

    @ApiModelProperty("礼包状态(1-正常；2-失效；3-删除）")
    private Integer state;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("如果store_id=0即为平台管理员，否则为vendor用户创建")
    private Integer createUserId;

    @ApiModelProperty("创建用户名")
    private String createUser;

    @ApiModelProperty("优惠券id列表；逗号分隔")
    private String couponIds;

    @ApiModelProperty("主题颜色")
    private String themeColor;

    @ApiModelProperty("PC端活动图")
    private String pcActDiagram;

    @ApiModelProperty("APP端活动图")
    private String appActDiagram;
}