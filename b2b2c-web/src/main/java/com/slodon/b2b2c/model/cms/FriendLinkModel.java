package com.slodon.b2b2c.model.cms;

import com.slodon.b2b2c.dao.read.cms.FriendLinkReadMapper;
import com.slodon.b2b2c.dao.write.cms.FriendLinkWriteMapper;
import com.slodon.b2b2c.cms.dto.FriendLinkAddDTO;
import com.slodon.b2b2c.cms.dto.FriendLinkUpdateDTO;
import com.slodon.b2b2c.cms.example.FriendLinkExample;
import com.slodon.b2b2c.cms.pojo.FriendLink;
import com.slodon.b2b2c.core.constant.FriendLinkConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.system.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class FriendLinkModel {
    @Resource
    private FriendLinkReadMapper friendLinkReadMapper;

    @Resource
    private FriendLinkWriteMapper friendLinkWriteMapper;

    /**
     * 新增合作伙伴
     *
     * @param friendLink
     * @return
     */
    public Integer saveFriendLink(FriendLink friendLink) {
        int count = friendLinkWriteMapper.insert(friendLink);
        if (count == 0) {
            throw new MallException("添加合作伙伴失败，请重试");
        }
        return count;
    }

    /**
     * 新增合作伙伴
     *
     * @param friendLinkAddDTO
     * @param admin
     * @return
     */
    public Integer saveFriendLink(FriendLinkAddDTO friendLinkAddDTO, Admin admin) {
        //根据链接名称查重
        FriendLinkExample friendLinkExample = new FriendLinkExample();
        friendLinkExample.setLinkName(friendLinkAddDTO.getLinkName());
        List<FriendLink> friendLinks = friendLinkReadMapper.listByExample(friendLinkExample);
        if (!CollectionUtils.isEmpty(friendLinks)) {
            throw new MallException("链接名称重复，请重新填写");
        }
        FriendLink friendLinkInsert = new FriendLink();
        friendLinkInsert.setLinkName(friendLinkAddDTO.getLinkName());
        if (!StringUtils.isEmpty(friendLinkAddDTO.getLinkImage())) {
            friendLinkInsert.setLinkImage(friendLinkAddDTO.getLinkImage());
        }
        friendLinkInsert.setShowType(friendLinkAddDTO.getShowType());
        friendLinkInsert.setLinkUrl(friendLinkAddDTO.getLinkUrl());
        friendLinkInsert.setSort(friendLinkAddDTO.getSort());
        friendLinkInsert.setState(FriendLinkConst.STATE_YES);
        friendLinkInsert.setCreateTime(new Date());
        friendLinkInsert.setCreateAdminId(admin.getAdminId());
        friendLinkInsert.setCreateAdminName(admin.getAdminName());
        int count = friendLinkWriteMapper.insert(friendLinkInsert);
        if (count == 0) {
            throw new MallException("添加合作伙伴失败，请重试");
        }
        return count;
    }

    /**
     * 根据linkId删除合作伙伴
     *
     * @param linkId linkId
     * @return
     */
    public Integer deleteFriendLink(Integer linkId) {
        if (StringUtils.isEmpty(linkId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = friendLinkWriteMapper.deleteByPrimaryKey(linkId);
        if (count == 0) {
            log.error("根据linkId：" + linkId + "删除合作伙伴失败");
            throw new MallException("删除合作伙伴失败,请重试");
        }
        return count;
    }

    /**
     * 批量删除合作伙伴
     *
     * @param linkIds linkIds
     * @return
     */
    public Integer batchDeleteFriendLink(String linkIds) {
        FriendLinkExample friendLinkExample = new FriendLinkExample();
        friendLinkExample.setLinkIdIn(linkIds);
        int count = friendLinkWriteMapper.deleteByExample(friendLinkExample);
        if (count == 0) {
            log.error("根据linkIds：" + linkIds + "批量删除合作伙伴失败");
            throw new MallException("删除合作伙伴失败,请重试");
        }
        return count;
    }

    /**
     * 根据linkId更新合作伙伴
     *
     * @param friendLink
     * @return
     */
    public Integer updateFriendLink(FriendLink friendLink) {
        if (StringUtils.isEmpty(friendLink.getLinkId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = friendLinkWriteMapper.updateByPrimaryKeySelective(friendLink);
        if (count == 0) {
            log.error("根据linkId：" + friendLink.getLinkId() + "更新合作伙伴失败");
            throw new MallException("更新合作伙伴失败,请重试");
        }
        return count;
    }

    /**
     * 根据linkId更新合作伙伴
     *
     * @param friendLinkUpdateDTO
     * @param admin
     * @return
     */
    public Integer updateFriendLink(FriendLinkUpdateDTO friendLinkUpdateDTO, Admin admin) throws Exception {
        //链接名称查重
        FriendLinkExample friendLinkExample = new FriendLinkExample();
        friendLinkExample.setLinkName(friendLinkUpdateDTO.getLinkName());
        friendLinkExample.setLinkIdNotEquals(friendLinkUpdateDTO.getLinkId());
        List<FriendLink> friendLinks = friendLinkReadMapper.listByExample(friendLinkExample);
        if (!CollectionUtils.isEmpty(friendLinks)) {
            throw new MallException("合作伙伴名称重复，请重试");
        }
        FriendLink friendLinkUpdate = new FriendLink();
        PropertyUtils.copyProperties(friendLinkUpdate, friendLinkUpdateDTO);
        friendLinkUpdate.setUpdateTime(new Date());
        friendLinkUpdate.setUpdateAdminId(admin.getAdminId());
        friendLinkUpdate.setUpdateAdminName(admin.getAdminName());
        int count = friendLinkWriteMapper.updateByPrimaryKeySelective(friendLinkUpdate);
        if (count == 0) {
            log.error("根据linkId：" + friendLinkUpdateDTO.getLinkId() + "更新合作伙伴失败");
            throw new MallException("更新合作伙伴失败,请重试");
        }
        return count;
    }

    /**
     * 根据linkId获取合作伙伴详情
     *
     * @param linkId linkId
     * @return
     */
    public FriendLink getFriendLinkByLinkId(Integer linkId) {
        FriendLink friendLink = friendLinkReadMapper.getByPrimaryKey(linkId);
        if (friendLink == null) {
            throw new MallException("获取合作伙伴为空，请重试");
        }
        return friendLink;
    }

    /**
     * 根据条件获取合作伙伴列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FriendLink> getFriendLinkList(FriendLinkExample example, PagerInfo pager) {
        List<FriendLink> friendLinkList;
        if (pager != null) {
            pager.setRowsCount(friendLinkReadMapper.countByExample(example));
            friendLinkList = friendLinkReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            friendLinkList = friendLinkReadMapper.listByExample(example);
        }
        return friendLinkList;
    }
}