package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.system.NavigationReadMapper;
import com.slodon.b2b2c.dao.write.system.NavigationWriteMapper;
import com.slodon.b2b2c.system.example.NavigationExample;
import com.slodon.b2b2c.system.pojo.Navigation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class NavigationModel {

    @Resource
    private NavigationReadMapper navigationReadMapper;
    @Resource
    private NavigationWriteMapper navigationWriteMapper;

    /**
     * 新增PC导航栏
     *
     * @param navigation
     * @return
     */
    public Integer saveNavigation(Navigation navigation) {
        //根据导航名称查重
        NavigationExample example = new NavigationExample();
        example.setNavName(navigation.getNavName());
        List<Navigation> list = navigationReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("导航名称重复，请重新填写");
        }
        int count = navigationWriteMapper.insert(navigation);
        if (count == 0) {
            throw new MallException("添加PC导航栏失败，请重试");
        }
        return count;
    }

    /**
     * 根据navId删除PC导航栏
     *
     * @param navId navId
     * @return
     */
    public Integer deleteNavigation(Integer navId) {
        if (StringUtils.isEmpty(navId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = navigationWriteMapper.deleteByPrimaryKey(navId);
        if (count == 0) {
            log.error("根据navId：" + navId + "删除PC导航栏失败");
            throw new MallException("删除PC导航栏失败,请重试");
        }
        return count;
    }

    /**
     * 根据navId更新PC导航栏
     *
     * @param navigation
     * @return
     */
    public Integer updateNavigation(Navigation navigation) {
        if (StringUtils.isEmpty(navigation.getNavId())) {
            throw new MallException("请选择要修改的数据");
        }
        if (!StringUtil.isEmpty(navigation.getNavName())) {
            //根据导航名称查重
            NavigationExample example = new NavigationExample();
            example.setNavIdNotEquals(navigation.getNavId());
            example.setNavName(navigation.getNavName());
            List<Navigation> list = navigationReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(list)) {
                throw new MallException("导航名称重复!");
            }
        }
        int count = navigationWriteMapper.updateByPrimaryKeySelective(navigation);
        if (count == 0) {
            log.error("根据navId：" + navigation.getNavId() + "更新PC导航栏失败");
            throw new MallException("更新PC导航栏失败,请重试");
        }
        return count;
    }

    /**
     * 根据navId获取PC导航栏详情
     *
     * @param navId navId
     * @return
     */
    public Navigation getNavigationByNavId(Integer navId) {
        return navigationReadMapper.getByPrimaryKey(navId);
    }

    /**
     * 根据条件获取PC导航栏列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Navigation> getNavigationList(NavigationExample example, PagerInfo pager) {
        List<Navigation> navigationList;
        if (pager != null) {
            pager.setRowsCount(navigationReadMapper.countByExample(example));
            navigationList = navigationReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            navigationList = navigationReadMapper.listByExample(example);
        }
        return navigationList;
    }
}