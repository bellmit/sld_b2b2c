package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.write.member.MemberWxsignWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.member.example.MemberWxsignExample;
import com.slodon.b2b2c.member.pojo.MemberWxsign;
import com.slodon.b2b2c.dao.read.member.MemberWxsignReadMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberWxsignModel {

    @Resource
    private MemberWxsignReadMapper memberWxsignReadMapper;
    @Resource
    private MemberWxsignWriteMapper memberWxsignWriteMapper;

    /**
     * 新增微信联合登录
     *
     * @param memberWxsign
     * @return
     */
    public Integer saveMemberWxsign(MemberWxsign memberWxsign) {
        int count = memberWxsignWriteMapper.insert(memberWxsign);
        if (count == 0) {
            throw new MallException("添加微信联合登录失败，请重试");
        }
        return count;
    }

    /**
     * 根据signId删除微信联合登录
     *
     * @param signId signId
     * @return
     */
    public Integer deleteMemberWxsign(Integer signId) {
        if (StringUtils.isEmpty(signId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberWxsignWriteMapper.deleteByPrimaryKey(signId);
        if (count == 0) {
            log.error("根据signId：" + signId + "删除微信联合登录失败");
            throw new MallException("删除微信联合登录失败,请重试");
        }
        return count;
    }

    /**
     * 根据signId更新微信联合登录
     *
     * @param memberWxsign
     * @return
     */
    public Integer updateMemberWxsign(MemberWxsign memberWxsign) {
        if (StringUtils.isEmpty(memberWxsign.getSignId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberWxsignWriteMapper.updateByPrimaryKeySelective(memberWxsign);
        if (count == 0) {
            log.error("根据signId：" + memberWxsign.getSignId() + "更新微信联合登录失败");
            throw new MallException("更新微信联合登录失败,请重试");
        }
        return count;
    }

    /**
     * 根据signId获取微信联合登录详情
     *
     * @param signId signId
     * @return
     */
    public MemberWxsign getMemberWxsignBySignId(Integer signId) {
        return memberWxsignReadMapper.getByPrimaryKey(signId);
    }

    /**
     * 根据条件获取微信联合登录列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberWxsign> getMemberWxsignList(MemberWxsignExample example, PagerInfo pager) {
        List<MemberWxsign> memberWxsignList;
        if (pager != null) {
            pager.setRowsCount(memberWxsignReadMapper.countByExample(example));
            memberWxsignList = memberWxsignReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberWxsignList = memberWxsignReadMapper.listByExample(example);
        }
        return memberWxsignList;
    }
}