package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.system.AdminReadMapper;
import com.slodon.b2b2c.dao.write.system.AdminWriteMapper;
import com.slodon.b2b2c.system.example.AdminExample;
import com.slodon.b2b2c.system.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 平台管理员表model
 */
@Component
@Slf4j
public class AdminModel {

    @Resource
    private AdminReadMapper adminReadMapper;
    @Resource
    private AdminWriteMapper adminWriteMapper;

    /**
     * 新增平台管理员表
     *
     * @param admin
     * @return
     */
    public Integer saveAdmin(Admin admin) {
        //查重
        AdminExample example = new AdminExample();
        example.setAdminName(StringUtil.trim(admin.getAdminName()));
        List<Admin> list = adminWriteMapper.listByExample(example);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "账号已存在！");

        int count = adminWriteMapper.insert(admin);
        if (count == 0) {
            throw new MallException("添加平台管理员表失败，请重试");
        }
        return admin.getAdminId();
    }

    /**
     * 根据adminId删除平台管理员表
     *
     * @param adminId adminId
     * @return
     */
    public Integer deleteAdmin(Integer adminId) {
        if (StringUtils.isEmpty(adminId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = adminWriteMapper.deleteByPrimaryKey(adminId);
        if (count == 0) {
            log.error("根据adminId：" + adminId + "删除平台管理员表失败");
            throw new MallException("删除平台管理员表失败,请重试");
        }
        return count;
    }

    /**
     * 根据adminId更新平台管理员表
     *
     * @param admin
     * @return
     */
    public Integer updateAdmin(Admin admin) {
        if (StringUtils.isEmpty(admin.getAdminId())) {
            throw new MallException("请选择要修改的数据");
        }
        //查重
        if (!StringUtil.isEmpty(admin.getAdminName())) {
            AdminExample example = new AdminExample();
            example.setAdminIdNotEquals(admin.getAdminId());
            example.setAdminName(StringUtil.trim(admin.getAdminName()));
            List<Admin> list = adminWriteMapper.listByExample(example);
            AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "账号已存在！");
        }
        int count = adminWriteMapper.updateByPrimaryKeySelective(admin);
        if (count == 0) {
            log.error("根据adminId：" + admin.getAdminId() + "更新平台管理员表失败");
            throw new MallException("更新平台管理员表失败,请重试");
        }
        return count;
    }

    /**
     * 根据adminId获取平台管理员表详情
     *
     * @param adminId adminId
     * @return
     */
    public Admin getAdminByAdminId(Integer adminId) {
        return adminReadMapper.getByPrimaryKey(adminId);
    }

    /**
     * 根据条件获取平台管理员表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Admin> getAdminList(AdminExample example, PagerInfo pager) {
        List<Admin> adminList;
        if (pager != null) {
            pager.setRowsCount(adminReadMapper.countByExample(example));
            adminList = adminReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            adminList = adminReadMapper.listByExample(example);
        }
        return adminList;
    }
}