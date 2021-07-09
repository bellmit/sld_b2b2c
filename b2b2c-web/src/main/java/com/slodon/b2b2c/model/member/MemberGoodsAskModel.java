package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.write.member.MemberGoodsAskWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.member.MemberGoodsAskReadMapper;
import com.slodon.b2b2c.member.example.MemberGoodsAskExample;
import com.slodon.b2b2c.member.pojo.MemberGoodsAsk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberGoodsAskModel {
    @Resource
    private MemberGoodsAskReadMapper memberGoodsAskReadMapper;

    @Resource
    private MemberGoodsAskWriteMapper memberGoodsAskWriteMapper;

    /**
     * 新增商品咨询管理
     *
     * @param memberGoodsAsk
     * @return
     */
    public Integer saveMemberGoodsAsk(MemberGoodsAsk memberGoodsAsk) {
        int count = memberGoodsAskWriteMapper.insert(memberGoodsAsk);
        if (count == 0) {
            throw new MallException("添加商品咨询管理失败，请重试");
        }
        return count;
    }

    /**
     * 根据askId删除商品咨询管理
     *
     * @param askId askId
     * @return
     */
    public Integer deleteMemberGoodsAsk(Integer askId) {
        if (StringUtils.isEmpty(askId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberGoodsAskWriteMapper.deleteByPrimaryKey(askId);
        if (count == 0) {
            log.error("根据askId：" + askId + "删除商品咨询管理失败");
            throw new MallException("删除商品咨询管理失败,请重试");
        }
        return count;
    }

    /**
     * 根据askId更新商品咨询管理
     *
     * @param memberGoodsAsk
     * @return
     */
    public Integer updateMemberGoodsAsk(MemberGoodsAsk memberGoodsAsk) {
        if (StringUtils.isEmpty(memberGoodsAsk.getAskId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberGoodsAskWriteMapper.updateByPrimaryKeySelective(memberGoodsAsk);
        if (count == 0) {
            log.error("根据askId：" + memberGoodsAsk.getAskId() + "更新商品咨询管理失败");
            throw new MallException("更新商品咨询管理失败,请重试");
        }
        return count;
    }

    /**
     * 根据askId获取商品咨询管理详情
     *
     * @param askId askId
     * @return
     */
    public MemberGoodsAsk getMemberGoodsAskByAskId(Integer askId) {
        return memberGoodsAskReadMapper.getByPrimaryKey(askId);
    }

    /**
     * 根据条件获取商品咨询管理列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberGoodsAsk> getMemberGoodsAskList(MemberGoodsAskExample example, PagerInfo pager) {
        List<MemberGoodsAsk> memberGoodsAskList;
        if (pager != null) {
            pager.setRowsCount(memberGoodsAskReadMapper.countByExample(example));
            memberGoodsAskList = memberGoodsAskReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberGoodsAskList = memberGoodsAskReadMapper.listByExample(example);
        }
        return memberGoodsAskList;
    }
}