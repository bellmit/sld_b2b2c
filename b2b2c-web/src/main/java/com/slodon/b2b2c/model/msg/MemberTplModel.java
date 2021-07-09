package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.MemberTplReadMapper;
import com.slodon.b2b2c.dao.write.msg.MemberTplWriteMapper;
import com.slodon.b2b2c.msg.dto.MemberTplDTO;
import com.slodon.b2b2c.msg.example.MemberTplExample;
import com.slodon.b2b2c.msg.pojo.MemberTpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberTplModel {

    @Resource
    private MemberTplReadMapper memberTplReadMapper;
    @Resource
    private MemberTplWriteMapper memberTplWriteMapper;

    /**
     * 新增用户消息模板
     *
     * @param memberTpl
     * @return
     */
    public Integer saveMemberTpl(MemberTpl memberTpl) {
        int count = memberTplWriteMapper.insert(memberTpl);
        if (count == 0) {
            throw new MallException("添加用户消息模板失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplCode删除用户消息模板
     *
     * @param tplCode tplCode
     * @return
     */
    public Integer deleteMemberTpl(String tplCode) {
        if (StringUtils.isEmpty(tplCode)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberTplWriteMapper.deleteByPrimaryKey(tplCode);
        if (count == 0) {
            log.error("根据tplCode：" + tplCode + "删除用户消息模板失败");
            throw new MallException("删除用户消息模板失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode更新用户消息模板
     *
     * @param memberTpl
     * @return
     */
    public Integer updateMemberTpl(MemberTpl memberTpl) {
        if (StringUtils.isEmpty(memberTpl.getTplCode())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberTplWriteMapper.updateByPrimaryKeySelective(memberTpl);
        if (count == 0) {
            log.error("根据tplCode：" + memberTpl.getTplCode() + "更新用户消息模板失败");
            throw new MallException("更新用户消息模板失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode更新用户消息模板
     *
     * @param memberTplDTO
     * @return
     */
    @SneakyThrows
    public Integer updateMemberTpl(MemberTplDTO memberTplDTO) {
        if (StringUtils.isEmpty(memberTplDTO.getTplCode())) {
            throw new MallException("请选择要修改的数据");
        }
        MemberTpl memberTpl = new MemberTpl();
        PropertyUtils.copyProperties(memberTpl, memberTplDTO);
        int count = memberTplWriteMapper.updateByPrimaryKeySelective(memberTpl);
        if (count == 0) {
            log.error("根据tplCode：" + memberTpl.getTplCode() + "更新用户消息模板失败");
            throw new MallException("更新用户消息模板失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode获取用户消息模板详情
     *
     * @param tplCode tplCode
     * @return
     */
    public MemberTpl getMemberTplByTplCode(String tplCode) {
        return memberTplReadMapper.getByPrimaryKey(tplCode);
    }

    /**
     * 根据条件获取用户消息模板列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberTpl> getMemberTplList(MemberTplExample example, PagerInfo pager) {
        List<MemberTpl> memberTplList;
        if (pager != null) {
            pager.setRowsCount(memberTplReadMapper.countByExample(example));
            memberTplList = memberTplReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberTplList = memberTplReadMapper.listByExample(example);
        }
        return memberTplList;
    }
}