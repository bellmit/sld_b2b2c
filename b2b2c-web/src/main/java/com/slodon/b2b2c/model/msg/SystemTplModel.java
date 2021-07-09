package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.SystemTplReadMapper;
import com.slodon.b2b2c.dao.write.msg.SystemTplWriteMapper;
import com.slodon.b2b2c.msg.example.SystemTplExample;
import com.slodon.b2b2c.msg.pojo.SystemTpl;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class SystemTplModel {
    @Resource
    private SystemTplReadMapper systemTplReadMapper;

    @Resource
    private SystemTplWriteMapper systemTplWriteMapper;

    /**
     * 新增系统消息模板
     *
     * @param systemTpl
     * @return
     */
    public Integer saveSystemTpl(SystemTpl systemTpl) {
        int count = systemTplWriteMapper.insert(systemTpl);
        if (count == 0) {
            throw new MallException("添加系统消息模板失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplCode删除系统消息模板
     *
     * @param tplCode tplCode
     * @return
     */
    public Integer deleteSystemTpl(String tplCode) {
        if (StringUtils.isEmpty(tplCode)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = systemTplWriteMapper.deleteByPrimaryKey(tplCode);
        if (count == 0) {
            log.error("根据tplCode：" + tplCode + "删除系统消息模板失败");
            throw new MallException("删除系统消息模板失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode更新系统消息模板
     *
     * @param systemTpl
     * @return
     */
    public Integer updateSystemTpl(SystemTpl systemTpl) {
        if (StringUtils.isEmpty(systemTpl.getTplCode())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = systemTplWriteMapper.updateByPrimaryKeySelective(systemTpl);
        if (count == 0) {
            log.error("根据tplCode：" + systemTpl.getTplCode() + "更新系统消息模板失败");
            throw new MallException("更新系统消息模板失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode获取系统消息模板详情
     *
     * @param tplCode tplCode
     * @return
     */
    public SystemTpl getSystemTplByTplCode(Integer tplCode) {
        return systemTplReadMapper.getByPrimaryKey(tplCode);
    }

    /**
     * 根据条件获取系统消息模板列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SystemTpl> getSystemTplList(SystemTplExample example, PagerInfo pager) {
        List<SystemTpl> systemTplList;
        if (pager != null) {
            pager.setRowsCount(systemTplReadMapper.countByExample(example));
            systemTplList = systemTplReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            systemTplList = systemTplReadMapper.listByExample(example);
        }
        return systemTplList;
    }
}