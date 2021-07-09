package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.member.pojo.MemberProductLookLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装用户足迹对象VO
 * @Author wuxy
 */
@Data
public class ChatProductLookLogVO implements Serializable {

    private static final long serialVersionUID = 3779158911436710239L;
    @ApiModelProperty("记录id")
    private Integer logId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("产品id")
    private Long productId;

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    public ChatProductLookLogVO(MemberProductLookLog memberProductLookLog) {
        logId = memberProductLookLog.getLogId();
        goodsId = memberProductLookLog.getGoodsId();
        goodsName = memberProductLookLog.getGoodsName();
        goodsImage = FileUrlUtil.getFileUrl(memberProductLookLog.getGoodsImage(), ImageSizeEnum.SMALL);
        productId = memberProductLookLog.getProductId();
        productPrice = memberProductLookLog.getProductPrice();
    }
}
