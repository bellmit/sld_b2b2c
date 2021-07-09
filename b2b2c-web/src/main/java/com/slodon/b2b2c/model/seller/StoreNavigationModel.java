package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.StoreNavigationReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreNavigationWriteMapper;
import com.slodon.b2b2c.seller.example.StoreNavigationExample;
import com.slodon.b2b2c.seller.pojo.StoreNavigation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class StoreNavigationModel {

    @Resource
    private StoreNavigationReadMapper storeNavigationReadMapper;
    @Resource
    private StoreNavigationWriteMapper storeNavigationWriteMapper;

    /**
     * 新增店铺内导航配置
     *
     * @param storeNavigation
     * @return
     */
    public Integer saveStoreNavigation(StoreNavigation storeNavigation) {
        int count = storeNavigationWriteMapper.insert(storeNavigation);
        if (count == 0) {
            throw new MallException("添加店铺内导航配置失败，请重试");
        }
        return count;
    }

    /**
     * 根据navId删除店铺内导航配置
     *
     * @param navId navId
     * @return
     */
    public Integer deleteStoreNavigation(Integer navId) {
        if (StringUtils.isEmpty(navId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeNavigationWriteMapper.deleteByPrimaryKey(navId);
        if (count == 0) {
            log.error("根据navId：" + navId + "删除店铺内导航配置失败");
            throw new MallException("删除店铺内导航配置失败,请重试");
        }
        return count;
    }

    /**
     * 根据navId更新店铺内导航配置
     *
     * @param storeNavigation
     * @return
     */
    public Integer updateStoreNavigation(StoreNavigation storeNavigation) {
        if (StringUtils.isEmpty(storeNavigation.getNavId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeNavigationWriteMapper.updateByPrimaryKeySelective(storeNavigation);
        if (count == 0) {
            log.error("根据navId：" + storeNavigation.getNavId() + "更新店铺内导航配置失败");
            throw new MallException("更新店铺内导航配置失败,请重试");
        }
        return count;
    }

    /**
     * 根据navId获取店铺内导航配置详情
     *
     * @param navId navId
     * @return
     */
    public StoreNavigation getStoreNavigationByNavId(Integer navId) {
        return storeNavigationReadMapper.getByPrimaryKey(navId);
    }

    /**
     * 根据条件获取店铺内导航配置列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreNavigation> getStoreNavigationList(StoreNavigationExample example, PagerInfo pager) {
        List<StoreNavigation> storeNavigationList;
        if (pager != null) {
            pager.setRowsCount(storeNavigationReadMapper.countByExample(example));
            storeNavigationList = storeNavigationReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeNavigationList = storeNavigationReadMapper.listByExample(example);
        }
        return storeNavigationList;
    }
}