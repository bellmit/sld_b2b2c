package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.goods.pojo.GoodsComment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品评论管理
 */
@Data
public class SellerGoodsCommentVO {

    @ApiModelProperty("评论id")
    private Integer commentId;

    @ApiModelProperty("评价人ID")
    private Integer memberId;

    @ApiModelProperty("评价人账号")
    private String memberName;

    @ApiModelProperty("评分(1到5)")
    private Integer score;

    @ApiModelProperty("评价内容")
    private String content;

    @ApiModelProperty("评价时间")
    private Date createTime;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("订单货品明细ID")
    private Long orderProductId;

    @ApiModelProperty("回复内容")
    private String replyContent;

    @ApiModelProperty("评价上传图片")
    private String image;

    @ApiModelProperty("评价上传图片")
    private List<String> imageValue;

    public SellerGoodsCommentVO(GoodsComment goodsComment) {
        commentId = goodsComment.getCommentId();
        memberId = goodsComment.getMemberId();
        memberName = goodsComment.getMemberName();
        score = goodsComment.getScore();
        content = StringUtil.isEmpty(goodsComment.getContent())?"此用户未填写评价":goodsComment.getContent();
        createTime = goodsComment.getCreateTime();
        goodsId = goodsComment.getGoodsId();
        goodsName = goodsComment.getGoodsName();
        productId = goodsComment.getProductId();
        orderSn = goodsComment.getOrderSn();
        orderProductId = goodsComment.getOrderProductId();
        replyContent = goodsComment.getReplyContent();
        image = goodsComment.getImage();
        imageValue = dealImageList(goodsComment.getImage());
    }

    public List<String> dealImageList(String images) {
        List<String> list = new ArrayList<>();
        if (!StringUtil.isEmpty(images)) {
            String[] strArray = images.split(",");
            for (String str : strArray) {
                if (StringUtil.isEmpty(str)) {
                    continue;
                }
                list.add(FileUrlUtil.getFileUrl(str, null));
            }
        }
        return list;
    }
}