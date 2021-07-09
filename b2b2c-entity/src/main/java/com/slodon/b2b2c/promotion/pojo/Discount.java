package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 折扣活动基础信息表
 */
@Data
public class Discount implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("折扣活动ID")
    private Integer discountId;

    @ApiModelProperty("活动名称")
    private String discountName;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("限购数量，0-不限制，其他数值为具体限购数量")
    private Integer buyLimit;

    @ApiModelProperty("是否指定价格1-指定，0-不指定；不指定价格需填折扣比例")
    private Integer isCustomPrice;

    @ApiModelProperty("折扣比例")
    private Integer discountRate;

    @ApiModelProperty("商户ID")
    private Long storeId;

    @ApiModelProperty("状态：1-可用，0-不可用")
    private Integer state;

    @ApiModelProperty("使用json存储规则，对应的业务类进行解析处理")
    private String rule;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建商家ID，vendor表")
    private Long vendorId;

    @ApiModelProperty("创建人名称")
    private String vendorName;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}