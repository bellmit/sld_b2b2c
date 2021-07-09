package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.member.MemberFollowStoreReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreReadMapper;
import com.slodon.b2b2c.dao.write.member.MemberFollowStoreWriteMapper;
import com.slodon.b2b2c.dao.write.seller.StoreWriteMapper;
import com.slodon.b2b2c.member.example.MemberFollowStoreExample;
import com.slodon.b2b2c.member.pojo.MemberFollowStore;
import com.slodon.b2b2c.seller.example.StoreExample;
import com.slodon.b2b2c.seller.pojo.Store;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MemberFollowStoreModel {

    @Resource
    private MemberFollowStoreReadMapper memberFollowStoreReadMapper;
    @Resource
    private StoreReadMapper storeReadMapper;

    @Resource
    private StoreWriteMapper storeWriteMapper;
    @Resource
    private MemberFollowStoreWriteMapper memberFollowStoreWriteMapper;

    /**
     * 新增会员收藏商铺表
     *
     * @param memberFollowStore
     * @return
     */
    public Integer saveMemberFollowStore(MemberFollowStore memberFollowStore) {
        int count = memberFollowStoreWriteMapper.insert(memberFollowStore);
        if (count == 0) {
            throw new MallException("添加会员收藏商铺表失败，请重试");
        }
        return count;
    }

    /**
     * 根据followId删除会员收藏商铺表
     *
     * @param followId followId
     * @return
     */
    public Integer deleteMemberFollowStore(Integer followId) {
        if (StringUtils.isEmpty(followId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberFollowStoreWriteMapper.deleteByPrimaryKey(followId);
        if (count == 0) {
            log.error("根据followId：" + followId + "删除会员收藏商铺表失败");
            throw new MallException("删除会员收藏商铺表失败,请重试");
        }
        return count;
    }

    /**
     * 根据followId更新会员收藏商铺表
     *
     * @param memberFollowStore
     * @return
     */
    public Integer updateMemberFollowStore(MemberFollowStore memberFollowStore) {
        if (StringUtils.isEmpty(memberFollowStore.getFollowId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberFollowStoreWriteMapper.updateByPrimaryKeySelective(memberFollowStore);
        if (count == 0) {
            log.error("根据followId：" + memberFollowStore.getFollowId() + "更新会员收藏商铺表失败");
            throw new MallException("更新会员收藏商铺表失败,请重试");
        }
        return count;
    }

    /**
     * 根据followId获取会员收藏商铺表详情
     *
     * @param followId followId
     * @return
     */
    public MemberFollowStore getMemberFollowStoreByFollowId(Integer followId) {
        return memberFollowStoreReadMapper.getByPrimaryKey(followId);
    }

    /**
     * 根据条件获取会员收藏商铺表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberFollowStore> getMemberFollowStoreList(MemberFollowStoreExample example, PagerInfo pager) {
        List<MemberFollowStore> memberFollowStoreList;
        if (pager != null) {
            pager.setRowsCount(memberFollowStoreReadMapper.countByExample(example));
            memberFollowStoreList = memberFollowStoreReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberFollowStoreList = memberFollowStoreReadMapper.listByExample(example);
        }
        return memberFollowStoreList;
    }

    /**
     * 根据条件获取会员收藏商铺数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getMemberFollowStoreCount(MemberFollowStoreExample example) {
        return memberFollowStoreReadMapper.countByExample(example);
    }

    /**
     * 关注/取消关注商铺
     *
     * @param storeIds
     * @param isCollect
     * @param memberId
     * @return
     */
    @Transactional
    public Boolean editMemberFollowStore(String storeIds, Boolean isCollect, Integer memberId) {
        //基于商铺id查询商铺表数据
        StoreExample example = new StoreExample();
        example.setStoreIdIn(storeIds);
        List<Store> list = storeReadMapper.listByExample(example);
        if (isCollect) {
            //关注
            for (Store store : list) {
                //根据 storeId 判重
                MemberFollowStoreExample followStoreExample = new MemberFollowStoreExample();
                followStoreExample.setMemberId(memberId);
                followStoreExample.setStoreId(store.getStoreId());
                List<MemberFollowStore> memberFollowStoreList = memberFollowStoreReadMapper.listByExample(followStoreExample);
                if (CollectionUtils.isEmpty(memberFollowStoreList)) {
                    //memberFollowStoreList 为空,直接新增; 否则,非空,已经关注过该店铺,跳过
                    MemberFollowStore memberFollowStore = new MemberFollowStore();
                    memberFollowStore.setMemberId(memberId);
                    memberFollowStore.setStoreId(store.getStoreId());
                    memberFollowStore.setStoreName(store.getStoreName());
                    memberFollowStore.setStoreLogo(store.getStoreLogo());
                    memberFollowStore.setStoreCategoryId(store.getStoreCategoryId());
                    memberFollowStore.setStoreCategoryName(store.getStoreCategoryName());
                    memberFollowStore.setCreateTime(new Date());
                    int count = memberFollowStoreWriteMapper.insert(memberFollowStore);
                    AssertUtil.notNullOrZero(count, "添加会员关注商铺表失败，请重试");
                    //店铺收藏数增加
                    Store storeNew = new Store();
                    storeNew.setStoreId(store.getStoreId());
                    storeNew.setFollowNumber(store.getFollowNumber() + 1);
                    storeWriteMapper.updateByPrimaryKeySelective(storeNew);
                }
            }
        } else {
            //店铺收藏数减少
            String[] storeIdList = storeIds.split(",");
            for (String storeId : storeIdList) {
                MemberFollowStoreExample memberFollowStoreExample = new MemberFollowStoreExample();
                memberFollowStoreExample.setStoreId(Long.valueOf(storeId));
                memberFollowStoreExample.setMemberId(memberId);
                List<MemberFollowStore> followStoreList = memberFollowStoreWriteMapper.listByExample(memberFollowStoreExample);
                if (!CollectionUtils.isEmpty(followStoreList)){
                    Store store = storeReadMapper.getByPrimaryKey(Long.valueOf(storeId));
                    Store storeNew = new Store();
                    storeNew.setStoreId(store.getStoreId());
                    storeNew.setFollowNumber(store.getFollowNumber() - 1);
                    storeWriteMapper.updateByPrimaryKeySelective(storeNew);
                }
            }
            //取消关注
            MemberFollowStoreExample followStoreExample = new MemberFollowStoreExample();
            followStoreExample.setStoreIdIn(storeIds);
            followStoreExample.setMemberId(memberId);
            int count = memberFollowStoreWriteMapper.deleteByExample(followStoreExample);
        }
        return true;
    }

    /**
     * 特别关注/取消特别关注商铺
     *
     * @param followId
     * @param isTop
     * @return
     */
    public Boolean editSpecialMemberFollowStore(Integer followId, Integer isTop) {

        //根据followId查询关注的店铺,修改
        MemberFollowStore memberFollowStore = memberFollowStoreReadMapper.getByPrimaryKey(followId);
        memberFollowStore.setIsTop(isTop);
        int count = memberFollowStoreWriteMapper.updateByPrimaryKeySelective(memberFollowStore);
        if (count == 0) {
            throw new MallException("特别关注/取消特别关注商铺操作失败，请重试");
        }
        return true;
    }
}