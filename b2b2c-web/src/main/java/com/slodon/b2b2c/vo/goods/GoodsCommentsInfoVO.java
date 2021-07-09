package com.slodon.b2b2c.vo.goods;


import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.goods.pojo.GoodsComment;
import com.slodon.b2b2c.member.pojo.Member;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品评价信息vo
 */
@Data
public class GoodsCommentsInfoVO {
    private String              avgScore;                         //平均评分，0-5，保留1位小数
    private String              highPercent;                      //好评率
    private Integer             commentsCount;                    //评价总数
    private Integer             hasPicCount;                      //有图数
    private Integer             highCount;                        //好评数
    private Integer             middleCount;                      //中评数
    private Integer             lowCount;                         //差评数
    private List<CommentsVO>    list = new ArrayList<>();         //评价列表
    private Map<String, Object> pagination;                        //分页

    /**
     * 评价vo
     */
    @Data
    public static class CommentsVO{
        public CommentsVO(GoodsComment goodsComment, Member member) {
            commentId = goodsComment.getCommentId();
            memberId = goodsComment.getMemberId();
            memberName = goodsComment.getMemberName();
            memberAvatar = StringUtil.isEmpty(member.getMemberAvatar()) ? member.getWxAvatarImg() : FileUrlUtil.getFileUrl(member.getMemberAvatar(), null);
            score = goodsComment.getScore();
            content = StringUtil.isEmpty(goodsComment.getContent())?"此用户未填写评价":goodsComment.getContent();
            createTime = goodsComment.getCreateTime();
            specValues = goodsComment.getSpecValues();
            if (!StringUtils.isEmpty(goodsComment.getImage())){
                for (String imagePath : goodsComment.getImage().split(",")) {
                    images.add(FileUrlUtil.getFileUrl(imagePath,null));
                }
            }
            replyContent = goodsComment.getReplyContent();
        }

        private Integer      commentId;         //评价id
        private Integer      memberId;         //评价人ID
        private String       memberName;         //评价人账号
        private String       memberAvatar;      //用户头像
        private Integer      score;         //评分(1到5)
        private String       content;         //评价内容
        private Date         createTime;         //评价时间
        private String       specValues;         //规格值集合
        private List<String> images = new ArrayList<>();         //评价图片
        private String       replyContent;   //商家回复内容
    }
}
