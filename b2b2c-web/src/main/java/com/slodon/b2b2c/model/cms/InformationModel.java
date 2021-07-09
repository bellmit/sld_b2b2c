package com.slodon.b2b2c.model.cms;

import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.cms.InformationCategoryReadMapper;
import com.slodon.b2b2c.dao.read.cms.InformationReadMapper;
import com.slodon.b2b2c.dao.write.cms.InformationWriteMapper;
import com.slodon.b2b2c.cms.dto.InformationAddDTO;
import com.slodon.b2b2c.cms.dto.InformationUpdateDTO;
import com.slodon.b2b2c.cms.example.InformationExample;
import com.slodon.b2b2c.cms.pojo.Information;
import com.slodon.b2b2c.cms.pojo.InformationCategory;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import com.slodon.b2b2c.system.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class InformationModel {
    @Resource
    private InformationReadMapper informationReadMapper;

    @Resource
    private InformationWriteMapper informationWriteMapper;

    @Resource
    private InformationCategoryReadMapper informationCategoryReadMapper;

    /**
     * 新增资讯表
     *
     * @param information
     * @return
     */
    public Integer saveInformation(Information information) {
        int count = informationWriteMapper.insert(information);
        if (count == 0) {
            throw new MallException("添加资讯表失败，请重试");
        }
        return count;
    }

    /**
     * 新增资讯表
     *
     * @param informationAddDTO
     * @param admin
     * @return
     */
    public Integer saveInformation(InformationAddDTO informationAddDTO, Admin admin) throws Exception{
        //判断标题名称是否重复
        InformationExample informationExample = new InformationExample();
        informationExample.setTitle(informationAddDTO.getTitle());
        List<Information> informationList = informationReadMapper.listByExample(informationExample);
        if (!CollectionUtils.isEmpty(informationList)) {
            throw new MallException("资讯标题重复，请重新填写");
        }
        //获取分类名称
        InformationCategory informationCategory = informationCategoryReadMapper.getByPrimaryKey(informationAddDTO.getCateId());

        Information informationInsert = new Information();
        PropertyUtils.copyProperties(informationInsert,informationAddDTO);
        informationInsert.setCateName(informationCategory.getCateName());
        informationInsert.setCreateTime(new Date());
        informationInsert.setCreateAdminId(admin.getAdminId());
        informationInsert.setCreateAdminName(admin.getAdminName());
        int count = informationWriteMapper.insert(informationInsert);
        if (count == 0) {
            throw new MallException("添加资讯表失败，请重试");
        }
        return count;
    }

    /**
     * 根据informationId删除资讯表
     *
     * @param informationId informationId
     * @return
     */
    public Integer deleteInformation(Integer informationId) {
        int count = informationWriteMapper.deleteByPrimaryKey(informationId);
        if (count == 0) {
            log.error("根据informationId：" + informationId + "删除资讯表失败");
            throw new MallException("删除资讯表失败,请重试");
        }
        return count;
    }

    /**
     * 根据informationId更新资讯表
     *
     * @param information
     * @return
     */
    public Integer updateInformation(Information information) {
        if (StringUtils.isEmpty(information.getInformationId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = informationWriteMapper.updateByPrimaryKeySelective(information);
        if (count == 0) {
            log.error("根据informationId：" + information.getInformationId() + "更新资讯表失败");
            throw new MallException("更新资讯表失败,请重试");
        }
        return count;
    }

    /**
     * 根据informationId更新资讯表
     *
     * @param informationUpdateDTO
     * @param admin
     * @return
     */
    public Integer updateInformation(InformationUpdateDTO informationUpdateDTO,Admin admin) throws Exception{
        if (!com.slodon.b2b2c.core.util.StringUtil.isEmpty(informationUpdateDTO.getTitle())) {
            //对标题名称进行查重
            InformationExample informationExample = new InformationExample();
            informationExample.setTitle(informationUpdateDTO.getTitle());
            informationExample.setInformationIdNotEquals(informationUpdateDTO.getInformationId());
            List<Information> informationList = informationReadMapper.listByExample(informationExample);
            if (!CollectionUtils.isEmpty(informationList)) {
                throw new MallException("编辑资讯时标题名称重复，请重新填写");
            }
        }
        Information informationUpdate = new Information();
        PropertyUtils.copyProperties(informationUpdate,informationUpdateDTO);
        //根据分类id获取分类名称
        if (!StringUtil.isNullOrZero(informationUpdateDTO.getCateId())) {
            Information information = informationReadMapper.getByPrimaryKey(informationUpdateDTO.getCateId());
            informationUpdate.setCateName(information.getCateName());
        }
        informationUpdate.setUpdateTime(new Date());
        informationUpdate.setUpdateAdminId(admin.getAdminId());
        informationUpdate.setUpdateAdminName(admin.getAdminName());
        int count = informationWriteMapper.updateByPrimaryKeySelective(informationUpdate);
        if (count == 0) {
            log.error("根据informationId：" + informationUpdateDTO.getInformationId() + "更新资讯表失败");
            throw new MallException("更新资讯表失败,请重试");
        }
        return count;
    }

    /**
     * 根据informationId获取资讯表详情
     *
     * @param informationId informationId
     * @return
     */
    public Information getInformationByInformationId(Integer informationId) {
        return informationReadMapper.getByPrimaryKey(informationId);
    }

    /**
     * 根据条件获取资讯表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Information> getInformationList(InformationExample example, PagerInfo pager) {
        List<Information> informationList;
        if (pager != null) {
            pager.setRowsCount(informationReadMapper.countByExample(example));
            informationList = informationReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            informationList = informationReadMapper.listByExample(example);
        }
        return informationList;
    }
}