package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.member.pojo.MemberProductLookLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品浏览记录表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLookLogVO implements Serializable {

    private static final long serialVersionUID = -7443981875499946002L;
    @ApiModelProperty("分组时间")
    private String time;
    @ApiModelProperty("我的足迹集合")
    private List<ProductLookLogInfo> productLookLogInfoList;

    @Data
    public static class ProductLookLogInfo implements Serializable {

        private static final long serialVersionUID = 3686921908412188137L;
        @ApiModelProperty("记录id")
        private Integer logId;

        @ApiModelProperty("会员id，没有登录ID为0")
        private Integer memberId;

        @ApiModelProperty("商品id")
        private Long goodsId;

        @ApiModelProperty("商品名称")
        private String goodsName;

        @ApiModelProperty("商品副标题，长度建议140个字符内")
        private String goodsBrief;

        @ApiModelProperty("商品图片")
        private String goodsImage;

        @ApiModelProperty("产品id")
        private Long productId;

        @ApiModelProperty("货品价格")
        private BigDecimal productPrice;

        @ApiModelProperty("店铺id")
        private Long storeId;

        @ApiModelProperty("访问时间")
        private Date createTime;

        @ApiModelProperty("收藏id")
        private Integer followId;

        //非足迹列表
        @ApiModelProperty("分享链接")
        private String shareLink;

        @ApiModelProperty("是否收藏商品:false 未收藏, true 收藏")
        private Boolean isFollowProduct;

        public ProductLookLogInfo(MemberProductLookLog memberProductLookLog) {
            logId = memberProductLookLog.getLogId();
            memberId = memberProductLookLog.getMemberId();
            goodsId = memberProductLookLog.getGoodsId();
            goodsName = memberProductLookLog.getGoodsName();
            goodsBrief=memberProductLookLog.getGoodsBrief();
            goodsImage = FileUrlUtil.getFileUrl(memberProductLookLog.getGoodsImage(),null);
            productId = memberProductLookLog.getProductId();
            productPrice = memberProductLookLog.getProductPrice();
            createTime = memberProductLookLog.getCreateTime();
            storeId = memberProductLookLog.getStoreId();
            followId = memberProductLookLog.getLogId();

            shareLink = DomainUrlUtil.SLD_H5_URL+"/#/pages/product/detail?productId="+memberProductLookLog.getProductId()+"&goodsId="+memberProductLookLog.getGoodsId();
        }

    }



}