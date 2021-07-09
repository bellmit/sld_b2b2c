package com.slodon.b2b2c.model.promotion;


import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.promotion.SeckillStageProductBindMemberReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SeckillStageProductReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SeckillStageProductBindMemberWriteMapper;
import com.slodon.b2b2c.promotion.example.SeckillStageProductBindMemberExample;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProductBindMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 秒杀商品与会员绑定关系model
 */
@Component
@Slf4j
public class SeckillStageProductBindMemberModel {
    @Resource
    private SeckillStageProductBindMemberReadMapper seckillStageProductBindMemberReadMapper;
    @Resource
    private SeckillStageProductReadMapper seckillStageProductReadMapper;

    @Resource
    private SeckillStageProductBindMemberWriteMapper seckillStageProductBindMemberWriteMapper;

    /**
     * 新增秒杀商品与会员绑定关系
     *
     * @param seckillStageProductBindMember
     * @return
     */
    public Integer saveSeckillStageProductBindMember(SeckillStageProductBindMember seckillStageProductBindMember) {
        int count = seckillStageProductBindMemberWriteMapper.insert(seckillStageProductBindMember);
        if (count == 0) {
            throw new MallException("添加秒杀商品与会员绑定关系失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除秒杀商品与会员绑定关系
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteSeckillStageProductBindMember(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = seckillStageProductBindMemberWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除秒杀商品与会员绑定关系失败");
            throw new MallException("删除秒杀商品与会员绑定关系失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId更新秒杀商品与会员绑定关系
     *
     * @param seckillStageProductBindMember
     * @return
     */
    public Integer updateSeckillStageProductBindMember(SeckillStageProductBindMember seckillStageProductBindMember) {
        if (StringUtils.isEmpty(seckillStageProductBindMember.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = seckillStageProductBindMemberWriteMapper.updateByPrimaryKeySelective(seckillStageProductBindMember);
        if (count == 0) {
            log.error("根据bindId：" + seckillStageProductBindMember.getBindId() + "更新秒杀商品与会员绑定关系失败");
            throw new MallException("更新秒杀商品与会员绑定关系失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取秒杀商品与会员绑定关系详情
     *
     * @param bindId bindId
     * @return
     */
    public SeckillStageProductBindMember getSeckillStageProductBindMemberByBindId(Integer bindId) {
        return seckillStageProductBindMemberReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取秒杀商品与会员绑定关系列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillStageProductBindMember> getSeckillStageProductBindMemberList(SeckillStageProductBindMemberExample example, PagerInfo pager) {
        List<SeckillStageProductBindMember> seckillStageProductBindMemberList;
        if (pager != null) {
            pager.setRowsCount(seckillStageProductBindMemberReadMapper.countByExample(example));
            seckillStageProductBindMemberList = seckillStageProductBindMemberReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            seckillStageProductBindMemberList = seckillStageProductBindMemberReadMapper.listByExample(example);
        }
        return seckillStageProductBindMemberList;
    }

    /**
     * 根据秒杀商品stageProductId获取秒杀商品与会员绑定关系详情
     *
     * @param stageProductId bindId
     * @return
     */
    public Boolean editNotice(Integer stageProductId,Integer memberId,String memberName) {
        Boolean notice =false;
        SeckillStageProduct stageProduct = seckillStageProductReadMapper.getByPrimaryKey(stageProductId);
        AssertUtil.notNull(stageProduct,"秒杀商品信息为空");
        AssertUtil.isTrue(!stageProduct.getVerifyState().equals(SeckillConst.SECKILL_AUDIT_STATE_2), "秒杀商品不是审核通过状态");
        SeckillStageProductBindMemberExample example = new SeckillStageProductBindMemberExample();
        example.setMemberId(memberId);
        example.setStageProductId(stageProductId);
        List<SeckillStageProductBindMember> list = seckillStageProductBindMemberReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(list)){
            //为空,去设置消息提醒,通知修改为true,新增SeckillStageProductBindMember;
            notice =true;
            SeckillStageProductBindMember insertOne = new SeckillStageProductBindMember();
            insertOne.setStageProductId(stageProductId);
            insertOne.setSeckillId(stageProduct.getSeckillId());
            insertOne.setStageId(stageProduct.getStageId());
            insertOne.setMemberId(memberId);
            insertOne.setMemberName(memberName);
            insertOne.setCreateTime(new Date());
            int count = seckillStageProductBindMemberWriteMapper.insert(insertOne);
            AssertUtil.notNullOrZero(count,"设置消息提醒失败");
        }else {
            int count = seckillStageProductBindMemberWriteMapper.deleteByPrimaryKey(list.get(0).getBindId());
            AssertUtil.notNullOrZero(count,"取消消息提醒失败");
        }
        return notice;
    }
}