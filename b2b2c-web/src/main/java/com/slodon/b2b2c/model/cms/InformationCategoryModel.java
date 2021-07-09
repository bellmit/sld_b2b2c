package com.slodon.b2b2c.model.cms;

import com.slodon.b2b2c.dao.read.cms.InformationCategoryReadMapper;
import com.slodon.b2b2c.dao.read.cms.InformationReadMapper;
import com.slodon.b2b2c.dao.write.cms.InformationCategoryWriteMapper;
import com.slodon.b2b2c.cms.dto.InformationCategoryAddDTO;
import com.slodon.b2b2c.cms.dto.InformationCategoryUpdateDTO;
import com.slodon.b2b2c.cms.example.InformationCategoryExample;
import com.slodon.b2b2c.cms.example.InformationExample;
import com.slodon.b2b2c.cms.pojo.Information;
import com.slodon.b2b2c.cms.pojo.InformationCategory;
import com.slodon.b2b2c.core.constant.InformationCateConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.system.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class InformationCategoryModel {
    @Resource
    private InformationCategoryReadMapper informationCategoryReadMapper;

    @Resource
    private InformationCategoryWriteMapper informationCategoryWriteMapper;

    @Resource
    private InformationReadMapper informationReadMapper;

    /**
     * 新增资讯分类表
     *
     * @param informationCategory
     * @return
     */
    public Integer saveInformationCategory(InformationCategory informationCategory) {
        int count = informationCategoryWriteMapper.insert(informationCategory);
        if (count == 0) {
            throw new MallException("添加资讯分类表失败，请重试");
        }
        return count;
    }

    /**
     * 新增资讯分类表
     *
     * @param informationCategoryAddDTO
     * @param admin
     * @return
     */
    public Integer saveInformationCategory(InformationCategoryAddDTO informationCategoryAddDTO, Admin admin) {
        //资讯分类名称是否重复，去掉头尾空格
        InformationCategoryExample example = new InformationCategoryExample();
        example.setCateName(com.slodon.b2b2c.core.util.StringUtil.trim(informationCategoryAddDTO.getCateName()));
        List<InformationCategory> informationCategories = informationCategoryReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(informationCategories)) {
            throw new MallException("添加资讯分类名称重复，请重试");
        }
        InformationCategory informationCategoryInsert = new InformationCategory();
        informationCategoryInsert.setCateName(informationCategoryAddDTO.getCateName());
        informationCategoryInsert.setSort(informationCategoryAddDTO.getSort());
        informationCategoryInsert.setCreateAdminId(admin.getAdminId());
        informationCategoryInsert.setCreateAdminName(admin.getAdminName());
        informationCategoryInsert.setIsShow(InformationCateConst.IS_SHOW_NO);
        informationCategoryInsert.setCreateTime(new Date());

        int count = informationCategoryWriteMapper.insert(informationCategoryInsert);
        if (count == 0) {
            throw new MallException("添加资讯分类表失败，请重试");
        }
        return count;
    }

    /**
     * 根据cateId删除资讯分类表
     *
     * @param cateId cateId
     * @return
     */
    public Integer deleteInformationCategory(Integer cateId) {
        if (StringUtils.isEmpty(cateId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = informationCategoryWriteMapper.deleteByPrimaryKey(cateId);
        if (count == 0) {
            log.error("根据cateId：" + cateId + "删除资讯分类表失败");
            throw new MallException("删除资讯分类表失败,请重试");
        }
        return count;
    }

    /**
     * 批量删除资讯分类表
     *
     * @param cateIds cateIds
     * @return
     */
    public Integer batchDeleteInformation(String cateIds) {
        String[] split = cateIds.split(",");
        for (String cateId : split) {
            //判断该分类下是否有资讯
            InformationExample informationExample = new InformationExample();
            informationExample.setCateId(Integer.parseInt(cateId));
            List<Information> informationList = informationReadMapper.listByExample(informationExample);
            if (!CollectionUtils.isEmpty(informationList)) {
                throw new MallException("请先删除该分类" + cateIds + "下所有资讯");
            }
        }
        InformationCategoryExample categoryExample = new InformationCategoryExample();
        categoryExample.setCateIdIn(cateIds);
        int count = informationCategoryWriteMapper.deleteByExample(categoryExample);
        if (count == 0) {
            log.error("根据cateIds：" + cateIds + "批量删除资讯分类表失败");
            throw new MallException("删除资讯分类表失败,请重试");
        }
        return count;
    }

    /**
     * 根据cateId更新资讯分类表
     *
     * @param informationCategory
     * @return
     */
    public Integer updateInformationCategory(InformationCategory informationCategory) {
        if (StringUtils.isEmpty(informationCategory.getCateId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = informationCategoryWriteMapper.updateByPrimaryKeySelective(informationCategory);
        if (count == 0) {
            log.error("根据cateId：" + informationCategory.getCateId() + "更新资讯分类表失败");
            throw new MallException("更新资讯分类表失败,请重试");
        }
        return count;
    }

    /**
     * 根据cateId更新资讯分类表
     *
     * @param informationCategoryUpdateDTO
     * @param admin
     * @return
     */
    public Integer updateInformationCategory(InformationCategoryUpdateDTO informationCategoryUpdateDTO, Admin admin) throws Exception {
        if (StringUtils.isEmpty(informationCategoryUpdateDTO.getIsShow())) {
            if (StringUtils.isEmpty(informationCategoryUpdateDTO.getCateName())) {
                throw new MallException("请填写分类名称");
            }

            //分类名称查重
            InformationCategoryExample example = new InformationCategoryExample();
            example.setCateIdNotEquals(informationCategoryUpdateDTO.getCateId());
            example.setCateName(com.slodon.b2b2c.core.util.StringUtil.trim(informationCategoryUpdateDTO.getCateName()));
            List<InformationCategory> informationCategories = informationCategoryReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(informationCategories)) {
                throw new MallException("分类名称重复，请重试");
            }
        }
        InformationCategory informationCategoryUpdate = new InformationCategory();
        PropertyUtils.copyProperties(informationCategoryUpdate, informationCategoryUpdateDTO);
        informationCategoryUpdate.setUpdateTime(new Date());
        informationCategoryUpdate.setUpdateAdminId(admin.getAdminId());
        informationCategoryUpdate.setUpdateAdminName(admin.getAdminName());
        int count = informationCategoryWriteMapper.updateByPrimaryKeySelective(informationCategoryUpdate);
        if (count == 0) {
            log.error("根据cateId：" + informationCategoryUpdateDTO.getCateId() + "更新资讯分类表失败");
            throw new MallException("更新资讯分类表失败,请重试");
        }
        return count;
    }

    /**
     * 根据cateId获取资讯分类表详情
     *
     * @param cateId cateId
     * @return
     */
    public InformationCategory getInformationCategoryByCateId(Integer cateId) {
        return informationCategoryReadMapper.getByPrimaryKey(cateId);
    }

    /**
     * 根据条件获取资讯分类表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<InformationCategory> getInformationCategoryList(InformationCategoryExample example, PagerInfo pager) {
        List<InformationCategory> informationCategoryList;
        if (pager != null) {
            pager.setRowsCount(informationCategoryReadMapper.countByExample(example));
            informationCategoryList = informationCategoryReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            informationCategoryList = informationCategoryReadMapper.listByExample(example);
        }
        return informationCategoryList;
    }
}