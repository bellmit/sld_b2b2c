package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.StoreRelatedTemplateReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreRelatedTemplateWriteMapper;
import com.slodon.b2b2c.seller.example.StoreRelatedTemplateExample;
import com.slodon.b2b2c.seller.pojo.StoreRelatedTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class StoreRelatedTemplateModel {

    @Resource
    private StoreRelatedTemplateReadMapper storeRelatedTemplateReadMapper;
    @Resource
    private StoreRelatedTemplateWriteMapper storeRelatedTemplateWriteMapper;

    /**
     * 新增店铺关联模版
     *
     * @param storeRelatedTemplate
     * @return
     */
    public Integer saveStoreRelatedTemplate(StoreRelatedTemplate storeRelatedTemplate) {
        int count = storeRelatedTemplateWriteMapper.insert(storeRelatedTemplate);
        if (count == 0) {
            throw new MallException("添加店铺关联模版失败，请重试");
        }
        return count;
    }

    /**
     * 根据templateId删除店铺关联模版
     *
     * @param templateId templateId
     * @return
     */
    public Integer deleteStoreRelatedTemplate(Integer templateId) {
        if (StringUtils.isEmpty(templateId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeRelatedTemplateWriteMapper.deleteByPrimaryKey(templateId);
        if (count == 0) {
            log.error("根据templateId：" + templateId + "删除店铺关联模版失败");
            throw new MallException("删除店铺关联模版失败,请重试");
        }
        return count;
    }

    /**
     * 根据templateId更新店铺关联模版
     *
     * @param storeRelatedTemplate
     * @return
     */
    public Integer updateStoreRelatedTemplate(StoreRelatedTemplate storeRelatedTemplate) {
        if (StringUtils.isEmpty(storeRelatedTemplate.getTemplateId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeRelatedTemplateWriteMapper.updateByPrimaryKeySelective(storeRelatedTemplate);
        if (count == 0) {
            log.error("根据templateId：" + storeRelatedTemplate.getTemplateId() + "更新店铺关联模版失败");
            throw new MallException("更新店铺关联模版失败,请重试");
        }
        return count;
    }

    /**
     * 根据templateId获取店铺关联模版详情
     *
     * @param templateId templateId
     * @return
     */
    public StoreRelatedTemplate getStoreRelatedTemplateByTemplateId(Integer templateId) {
        return storeRelatedTemplateReadMapper.getByPrimaryKey(templateId);
    }

    /**
     * 根据条件获取店铺关联模版列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreRelatedTemplate> getStoreRelatedTemplateList(StoreRelatedTemplateExample example, PagerInfo pager) {
        List<StoreRelatedTemplate> storeRelatedTemplateList;
        if (pager != null) {
            pager.setRowsCount(storeRelatedTemplateReadMapper.countByExample(example));
            storeRelatedTemplateList = storeRelatedTemplateReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeRelatedTemplateList = storeRelatedTemplateReadMapper.listByExample(example);
        }
        return storeRelatedTemplateList;
    }
}