package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.StoreTplRoleBindReadMapper;
import com.slodon.b2b2c.dao.write.msg.StoreTplRoleBindWriteMapper;
import com.slodon.b2b2c.msg.example.StoreTplRoleBindExample;
import com.slodon.b2b2c.msg.pojo.StoreTplRoleBind;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class StoreTplRoleBindModel {
    @Resource
    private StoreTplRoleBindReadMapper storeTplRoleBindReadMapper;

    @Resource
    private StoreTplRoleBindWriteMapper storeTplRoleBindWriteMapper;

    /**
     * 新增商家权限组拥有的消息模板表
     *
     * @param storeTplRoleBind
     * @return
     */
    public Integer saveStoreTplRoleBind(StoreTplRoleBind storeTplRoleBind) {
        int count = storeTplRoleBindWriteMapper.insert(storeTplRoleBind);
        if (count == 0) {
            throw new MallException("添加商家权限组拥有的消息模板表失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除商家权限组拥有的消息模板表
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteStoreTplRoleBind(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeTplRoleBindWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除商家权限组拥有的消息模板表失败");
            throw new MallException("删除商家权限组拥有的消息模板表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId更新商家权限组拥有的消息模板表
     *
     * @param storeTplRoleBind
     * @return
     */
    public Integer updateStoreTplRoleBind(StoreTplRoleBind storeTplRoleBind) {
        if (StringUtils.isEmpty(storeTplRoleBind.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeTplRoleBindWriteMapper.updateByPrimaryKeySelective(storeTplRoleBind);
        if (count == 0) {
            log.error("根据bindId：" + storeTplRoleBind.getBindId() + "更新商家权限组拥有的消息模板表失败");
            throw new MallException("更新商家权限组拥有的消息模板表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取商家权限组拥有的消息模板表详情
     *
     * @param bindId bindId
     * @return
     */
    public StoreTplRoleBind getStoreTplRoleBindByBindId(Integer bindId) {
        return storeTplRoleBindReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取商家权限组拥有的消息模板表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreTplRoleBind> getStoreTplRoleBindList(StoreTplRoleBindExample example, PagerInfo pager) {
        List<StoreTplRoleBind> storeTplRoleBindList;
        if (pager != null) {
            pager.setRowsCount(storeTplRoleBindReadMapper.countByExample(example));
            storeTplRoleBindList = storeTplRoleBindReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeTplRoleBindList = storeTplRoleBindReadMapper.listByExample(example);
        }
        return storeTplRoleBindList;
    }
}