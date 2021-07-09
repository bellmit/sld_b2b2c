package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsCommentReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsCommentWriteMapper;
import com.slodon.b2b2c.goods.dto.GoodsCommentReplyUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsCommentExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsComment;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.vo.goods.GoodsCommentsInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.List;

@Component
@Slf4j
public class GoodsCommentModel {

    @Resource
    private GoodsCommentReadMapper goodsCommentReadMapper;
    @Resource
    private GoodsCommentWriteMapper goodsCommentWriteMapper;
    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private MemberReadMapper memberReadMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增商品评论管理
     *
     * @param goodsComment
     * @return
     */
    public Integer saveGoodsComment(GoodsComment goodsComment) {
        int count = goodsCommentWriteMapper.insert(goodsComment);
        if (count == 0) {
            throw new MallException("添加商品评论管理失败，请重试");
        }
        return count;
    }

    /**
     * 根据commentId删除商品评论管理
     *
     * @param commentId commentId
     * @return
     */
    public Integer deleteGoodsComment(Integer commentId) {
        if (StringUtils.isEmpty(commentId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsCommentWriteMapper.deleteByPrimaryKey(commentId);
        if (count == 0) {
            log.error("根据commentId：" + commentId + "删除商品评论管理失败");
            throw new MallException("删除商品评论管理失败,请重试");
        }
        return count;
    }

    /**
     * 根据commentId更新商品评论管理
     *
     * @param goodsComment
     * @return
     */
    public Integer updateGoodsComment(GoodsComment goodsComment) {
        if (StringUtils.isEmpty(goodsComment.getCommentId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsCommentWriteMapper.updateByPrimaryKeySelective(goodsComment);
        if (count == 0) {
            log.error("根据commentId：" + goodsComment.getCommentId() + "更新商品评论管理失败");
            throw new MallException("更新商品评论管理失败,请重试");
        }
        return count;
    }

    /**
     * 根据commentId获取商品评论管理详情
     *
     * @param commentId commentId
     * @return
     */
    public GoodsComment getGoodsCommentByCommentId(Integer commentId) {
        return goodsCommentReadMapper.getByPrimaryKey(commentId);
    }

    /**
     * 根据条件获取商品评论管理列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsComment> getGoodsCommentList(GoodsCommentExample example, PagerInfo pager) {
        List<GoodsComment> goodsCommentList;
        if (pager != null) {
            pager.setRowsCount(goodsCommentReadMapper.countByExample(example));
            goodsCommentList = goodsCommentReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsCommentList = goodsCommentReadMapper.listByExample(example);
        }
        return goodsCommentList;
    }


    /**
     * 根据commentIds删除商品评论管理
     *
     * @param commentIds
     * @return
     */
    public Integer deleteGoodsComment(String commentIds) {
        GoodsCommentExample example = new GoodsCommentExample();
        example.setCommentIdIn(commentIds);
        int count = goodsCommentWriteMapper.deleteByExample(example);
        AssertUtil.notNullOrZero(count, "删除商品评论管理失败,请重试");
        return count;
    }

    /**
     * 根据commentId编辑回复商品评论
     *
     * @param goodsCommentReplyUpdateDTO
     * @return
     */
    public Integer editReply(GoodsCommentReplyUpdateDTO goodsCommentReplyUpdateDTO) throws Exception {
        GoodsComment updateOne = new GoodsComment();
        PropertyUtils.copyProperties(updateOne, goodsCommentReplyUpdateDTO);
        int count = goodsCommentWriteMapper.updateByPrimaryKeySelective(updateOne);
        AssertUtil.notNullOrZero(count, "回复商品评论失败,请重试");
        return count;
    }

    /**
     * 根据条件获取商品评价列表-front-商品详情
     *
     * @param goodsId
     * @param type
     * @param pager   分页信息
     * @return
     */
    public GoodsCommentsInfoVO getGoodsCommentList(Long goodsId, String type, PagerInfo pager) {
        GoodsCommentsInfoVO goodsCommentsInfoVO = new GoodsCommentsInfoVO();
        Goods goods = goodsReadMapper.getByPrimaryKey(goodsId);
        AssertUtil.notNull(goods, "商品不存在");

        //查找所有的评价   状态为审核通过
        GoodsCommentExample example = new GoodsCommentExample();
        example.setGoodsId(goodsId);
        example.setState(GoodsConst.COMMENT_AUDIT);
        //评价总数
        int commentsCount = goodsCommentReadMapper.countByExample(example);

        example.setGradeName("high");
        //好评数
        int highCount = goodsCommentReadMapper.countByExample(example);

        example.setGradeName("hasPic");
        //有图数
        int hasPicCount = goodsCommentReadMapper.countByExample(example);

        example.setGradeName("middle");
        //中评数
        int middleCount = goodsCommentReadMapper.countByExample(example);

        example.setGradeName("low");
        //差评数
        int lowCount = goodsCommentReadMapper.countByExample(example);

        if (commentsCount != 0) {
            //好评率
            goodsCommentsInfoVO.setHighPercent(highCount * 100 / commentsCount + "%");
            //平均评分，0-5，保留1位小数
            //该店铺评价总数
            example.setGoodsId(null);
            example.setGradeName(null);
            example.setStoreId(goods.getStoreId());
            int storeCommentsCount = goodsCommentReadMapper.countByExample(example);
            int sumScore = goodsCommentReadMapper.avgScoreByExample(example);
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            String averageScore = decimalFormat.format((float)sumScore/storeCommentsCount);
            goodsCommentsInfoVO.setAvgScore(StringUtil.isEmpty(averageScore)?"0.0":averageScore);
        } else {
            goodsCommentsInfoVO.setHighPercent("0%");
            goodsCommentsInfoVO.setAvgScore("0.0");
        }

        //赋值
//        goodsCommentsInfoVO.setAvgScore(new BigDecimal(goods.getCommentNumber()));
        goodsCommentsInfoVO.setCommentsCount(commentsCount);
        goodsCommentsInfoVO.setHighCount(highCount);
        goodsCommentsInfoVO.setHasPicCount(hasPicCount);
        goodsCommentsInfoVO.setMiddleCount(middleCount);
        goodsCommentsInfoVO.setLowCount(lowCount);

        //评价列表
        example.setGradeName(type);
        example.setGoodsId(goodsId);
        List<GoodsComment> list = goodsCommentReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsComment -> {
                Member member = memberReadMapper.getByPrimaryKey(goodsComment.getMemberId());
                // 会员头像
                if (StringUtil.isEmpty(member.getMemberAvatar()) && StringUtil.isEmpty(member.getWxAvatarImg())) {
                    member.setMemberAvatar(stringRedisTemplate.opsForValue().get("default_image_user_portrait"));
                }
                goodsCommentsInfoVO.getList().add(new GoodsCommentsInfoVO.CommentsVO(goodsComment, member));
            });
        }

        //分页信息
        if (pager != null) {
            goodsCommentsInfoVO.setPagination(pager.apiPage());
        }
        return goodsCommentsInfoVO;
    }
}