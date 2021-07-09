package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.msg.MemberTplReadMapper;
import com.slodon.b2b2c.dao.read.msg.StoreTplReadMapper;
import com.slodon.b2b2c.dao.read.msg.SystemTplTypeReadMapper;
import com.slodon.b2b2c.dao.write.msg.SystemTplTypeWriteMapper;
import com.slodon.b2b2c.msg.example.MemberTplExample;
import com.slodon.b2b2c.msg.example.StoreTplExample;
import com.slodon.b2b2c.msg.example.SystemTplTypeExample;
import com.slodon.b2b2c.msg.pojo.SystemTplType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SystemTplTypeModel {

    @Resource
    private SystemTplTypeReadMapper systemTplTypeReadMapper;
    @Resource
    private SystemTplTypeWriteMapper systemTplTypeWriteMapper;
    @Resource
    private MemberTplReadMapper memberTplReadMapper;
    @Resource
    private StoreTplReadMapper storeTplReadMapper;

    /**
     * 新增消息模板分类-内置表，不做修改
     *
     * @param systemTplType
     * @return
     */
    public Integer saveSystemTplType(SystemTplType systemTplType) {
        int count = systemTplTypeWriteMapper.insert(systemTplType);
        if (count == 0) {
            throw new MallException("添加消息模板分类-内置表，不做修改失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplTypeCode删除消息模板分类-内置表，不做修改
     *
     * @param tplTypeCode tplTypeCode
     * @return
     */
    public Integer deleteSystemTplType(String tplTypeCode) {
        if (StringUtils.isEmpty(tplTypeCode)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = systemTplTypeWriteMapper.deleteByPrimaryKey(tplTypeCode);
        if (count == 0) {
            log.error("根据tplTypeCode：" + tplTypeCode + "删除消息模板分类-内置表，不做修改失败");
            throw new MallException("删除消息模板分类-内置表，不做修改失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplTypeCode更新消息模板分类-内置表，不做修改
     *
     * @param systemTplType
     * @return
     */
    public Integer updateSystemTplType(SystemTplType systemTplType) {
        if (StringUtils.isEmpty(systemTplType.getTplTypeCode())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = systemTplTypeWriteMapper.updateByPrimaryKeySelective(systemTplType);
        if (count == 0) {
            log.error("根据tplTypeCode：" + systemTplType.getTplTypeCode() + "更新消息模板分类-内置表，不做修改失败");
            throw new MallException("更新消息模板分类-内置表，不做修改失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplTypeCode获取消息模板分类-内置表，不做修改详情
     *
     * @param tplTypeCode tplTypeCode
     * @return
     */
    public SystemTplType getSystemTplTypeByTplTypeCode(String tplTypeCode) {
        return systemTplTypeReadMapper.getByPrimaryKey(tplTypeCode);
    }

    /**
     * 根据条件获取消息模板分类-内置表，不做修改列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SystemTplType> getSystemTplTypeList(SystemTplTypeExample example, PagerInfo pager) {
        List<SystemTplType> systemTplTypeList;
        if (pager != null) {
            pager.setRowsCount(systemTplTypeReadMapper.countByExample(example));
            systemTplTypeList = systemTplTypeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            systemTplTypeList = systemTplTypeReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(systemTplTypeList)) {
                //不是会员就是商户
                if (!StringUtil.isNullOrZero(example.getTplSource()) && example.getTplSource() == MsgConst.TPL_SOURCE_MEMBER) {
                    systemTplTypeList.forEach(type -> {
                        MemberTplExample tplExample = new MemberTplExample();
                        tplExample.setTplTypeCode(type.getTplTypeCode());
                        type.setMemberTplList(memberTplReadMapper.listByExample(tplExample));
                    });
                } else {
                    systemTplTypeList.forEach(type -> {
                        StoreTplExample tplExample = new StoreTplExample();
                        tplExample.setTplTypeCode(type.getTplTypeCode());
                        type.setStoreTplList(storeTplReadMapper.listByExample(tplExample));
                    });
                }
            }
        }
        return systemTplTypeList;
    }
}