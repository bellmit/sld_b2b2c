package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.StoreCategoryReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreCategoryWriteMapper;
import com.slodon.b2b2c.seller.example.StoreCategoryExample;
import com.slodon.b2b2c.seller.pojo.StoreCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class StoreCategoryModel {

    @Resource
    private StoreCategoryReadMapper storeCategoryReadMapper;
    @Resource
    private StoreCategoryWriteMapper storeCategoryWriteMapper;

    /**
     * 新增店铺分类，二级分类
     *
     * @param storeCategory
     * @return
     */
    public Integer saveStoreCategory(StoreCategory storeCategory) {
        int count = storeCategoryWriteMapper.insert(storeCategory);
        if (count == 0) {
            throw new MallException("添加店铺分类，二级分类失败，请重试");
        }
        return count;
    }

    /**
     * 根据categoryId删除店铺分类，二级分类
     *
     * @param categoryId categoryId
     * @return
     */
    public Integer deleteStoreCategory(Integer categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeCategoryWriteMapper.deleteByPrimaryKey(categoryId);
        if (count == 0) {
            log.error("根据categoryId：" + categoryId + "删除店铺分类，二级分类失败");
            throw new MallException("删除店铺分类，二级分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据categoryId更新店铺分类，二级分类
     *
     * @param storeCategory
     * @return
     */
    public Integer updateStoreCategory(StoreCategory storeCategory) {
        if (StringUtils.isEmpty(storeCategory.getCategoryId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeCategoryWriteMapper.updateByPrimaryKeySelective(storeCategory);
        if (count == 0) {
            log.error("根据categoryId：" + storeCategory.getCategoryId() + "更新店铺分类，二级分类失败");
            throw new MallException("更新店铺分类，二级分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据categoryId获取店铺分类，二级分类详情
     *
     * @param categoryId categoryId
     * @return
     */
    public StoreCategory getStoreCategoryByCategoryId(Integer categoryId) {
        return storeCategoryReadMapper.getByPrimaryKey(categoryId);
    }

    /**
     * 根据条件获取店铺分类，二级分类列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreCategory> getStoreCategoryList(StoreCategoryExample example, PagerInfo pager) {
        List<StoreCategory> storeCategoryList;
        if (pager != null) {
            pager.setRowsCount(storeCategoryReadMapper.countByExample(example));
            storeCategoryList = storeCategoryReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeCategoryList = storeCategoryReadMapper.listByExample(example);
        }
        return storeCategoryList;
    }
}