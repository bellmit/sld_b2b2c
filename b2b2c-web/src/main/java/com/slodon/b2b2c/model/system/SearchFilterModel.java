package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.SearchFilterReadMapper;
import com.slodon.b2b2c.dao.write.system.SearchFilterWriteMapper;
import com.slodon.b2b2c.system.example.SearchFilterExample;
import com.slodon.b2b2c.system.pojo.SearchFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SearchFilterModel {

    @Resource
    private SearchFilterReadMapper searchFilterReadMapper;
    @Resource
    private SearchFilterWriteMapper searchFilterWriteMapper;

    /**
     * 新增搜索敏感词过滤
     *
     * @param searchFilter
     * @return
     */
    public Integer saveSearchFilter(SearchFilter searchFilter) {
        //查重
        SearchFilterExample example = new SearchFilterExample();
        example.setKeyword(searchFilter.getKeyword().trim());
        List<SearchFilter> list = searchFilterReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("敏感词已存在，无需重复添加");
        }
        int count = searchFilterWriteMapper.insert(searchFilter);
        if (count == 0) {
            throw new MallException("添加搜索敏感词过滤失败，请重试");
        }
        return count;
    }

    /**
     * 根据filterId删除搜索敏感词过滤
     *
     * @param filterId filterId
     * @return
     */
    public Integer deleteSearchFilter(Integer filterId) {
        if (StringUtils.isEmpty(filterId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = searchFilterWriteMapper.deleteByPrimaryKey(filterId);
        if (count == 0) {
            log.error("根据filterId：" + filterId + "删除搜索敏感词过滤失败");
            throw new MallException("删除搜索敏感词过滤失败,请重试");
        }
        return count;
    }

    /**
     * 根据filterId更新搜索敏感词过滤
     *
     * @param searchFilter
     * @return
     */
    public Integer updateSearchFilter(SearchFilter searchFilter) {
        if (StringUtils.isEmpty(searchFilter.getFilterId())) {
            throw new MallException("请选择要修改的数据");
        }
        //查重
        SearchFilterExample example = new SearchFilterExample();
        example.setFilterIdNotEquals(searchFilter.getFilterId());
        example.setKeyword(searchFilter.getKeyword().trim());
        List<SearchFilter> list = searchFilterReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("敏感词已存在!");
        }
        int count = searchFilterWriteMapper.updateByPrimaryKeySelective(searchFilter);
        if (count == 0) {
            log.error("根据filterId：" + searchFilter.getFilterId() + "更新搜索敏感词过滤失败");
            throw new MallException("更新搜索敏感词过滤失败,请重试");
        }
        return count;
    }

    /**
     * 根据filterId获取搜索敏感词过滤详情
     *
     * @param filterId filterId
     * @return
     */
    public SearchFilter getSearchFilterByFilterId(Integer filterId) {
        return searchFilterReadMapper.getByPrimaryKey(filterId);
    }

    /**
     * 根据条件获取搜索敏感词过滤列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SearchFilter> getSearchFilterList(SearchFilterExample example, PagerInfo pager) {
        List<SearchFilter> searchFilterList;
        if (pager != null) {
            pager.setRowsCount(searchFilterReadMapper.countByExample(example));
            searchFilterList = searchFilterReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            searchFilterList = searchFilterReadMapper.listByExample(example);
        }
        return searchFilterList;
    }
}