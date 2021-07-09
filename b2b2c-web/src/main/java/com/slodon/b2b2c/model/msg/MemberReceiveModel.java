package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.msg.MemberReceiveReadMapper;
import com.slodon.b2b2c.dao.read.msg.MemberTplReadMapper;
import com.slodon.b2b2c.dao.write.msg.MemberReceiveWriteMapper;
import com.slodon.b2b2c.msg.example.MemberReceiveExample;
import com.slodon.b2b2c.msg.pojo.MemberReceive;
import com.slodon.b2b2c.msg.pojo.MemberTpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会员接收消息表model
 */
@Component
@Slf4j
public class MemberReceiveModel {

    @Resource
    private MemberReceiveReadMapper memberReceiveReadMapper;
    @Resource
    private MemberReceiveWriteMapper memberReceiveWriteMapper;
    @Resource
    private MemberTplReadMapper memberTplReadMapper;

    /**
     * 新增会员接收消息表
     *
     * @param memberReceive
     * @return
     */
    public Integer saveMemberReceive(MemberReceive memberReceive) {
        int count = memberReceiveWriteMapper.insert(memberReceive);
        if (count == 0) {
            throw new MallException("添加会员接收消息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据receiveId删除会员接收消息表
     *
     * @param receiveId receiveId
     * @return
     */
    public Integer deleteMemberReceive(Integer receiveId) {
        if (StringUtils.isEmpty(receiveId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberReceiveWriteMapper.deleteByPrimaryKey(receiveId);
        if (count == 0) {
            log.error("根据receiveId：" + receiveId + "删除会员接收消息表失败");
            throw new MallException("删除会员接收消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据receiveId更新会员接收消息表
     *
     * @param memberReceive
     * @return
     */
    public Integer updateMemberReceive(MemberReceive memberReceive) {
        if (StringUtils.isEmpty(memberReceive.getReceiveId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberReceiveWriteMapper.updateByPrimaryKeySelective(memberReceive);
        if (count == 0) {
            log.error("根据receiveId：" + memberReceive.getReceiveId() + "更新会员接收消息表失败");
            throw new MallException("更新会员接收消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据receiveId获取会员接收消息表详情
     *
     * @param receiveId receiveId
     * @return
     */
    public MemberReceive getMemberReceiveByReceiveId(Integer receiveId) {
        return memberReceiveReadMapper.getByPrimaryKey(receiveId);
    }

    /**
     * 根据条件获取会员接收消息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberReceive> getMemberReceiveList(MemberReceiveExample example, PagerInfo pager) {
        List<MemberReceive> memberReceiveList;
        if (pager != null) {
            pager.setRowsCount(memberReceiveReadMapper.countByExample(example));
            memberReceiveList = memberReceiveReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberReceiveList = memberReceiveReadMapper.listByExample(example);
        }
        if (!CollectionUtils.isEmpty(memberReceiveList)) {
            for (MemberReceive receive : memberReceiveList) {
                MemberTpl memberTpl = memberTplReadMapper.getByPrimaryKey(receive.getTplCode());
                AssertUtil.notNull(memberTpl, "用户消息模板不存在");
                receive.setTplName(memberTpl.getTplName());
            }
        }
        return memberReceiveList;
    }

    /**
     * 根据条件获取会员接收消息数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getMemberReceiveCount(MemberReceiveExample example) {
        return memberReceiveReadMapper.countByExample(example);
    }

    /**
     * 根据条件修改消息
     *
     * @param memberReceive
     * @param example
     * @return
     */
    public Integer updateMemberReceiveByExample(MemberReceive memberReceive, MemberReceiveExample example) {
        return memberReceiveWriteMapper.updateByExampleSelective(memberReceive, example);
    }
}