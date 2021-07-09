package com.slodon.b2b2c.model.cms;

import com.slodon.b2b2c.dao.read.cms.InformationIndexReadMapper;
import com.slodon.b2b2c.dao.write.cms.InformationIndexWriteMapper;
import com.slodon.b2b2c.cms.example.InformationIndexExample;
import com.slodon.b2b2c.cms.pojo.InformationIndex;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class InformationIndexModel {
    @Resource
    private InformationIndexReadMapper informationIndexReadMapper;

    @Resource
    private InformationIndexWriteMapper informationIndexWriteMapper;

    /**
     * 新增资讯首页装修数据表
     *
     * @param informationIndex
     * @return
     */
    public Integer saveInformationIndex(InformationIndex informationIndex) {
        int count = informationIndexWriteMapper.insert(informationIndex);
        if (count == 0) {
            throw new MallException("添加资讯首页装修数据表失败，请重试");
        }
        return count;
    }

    /**
     * 根据indexId删除资讯首页装修数据表
     *
     * @param indexId indexId
     * @return
     */
    public Integer deleteInformationIndex(Integer indexId) {
        if (StringUtils.isEmpty(indexId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = informationIndexWriteMapper.deleteByPrimaryKey(indexId);
        if (count == 0) {
            log.error("根据indexId：" + indexId + "删除资讯首页装修数据表失败");
            throw new MallException("删除资讯首页装修数据表失败,请重试");
        }
        return count;
    }

    /**
     * 根据indexId更新资讯首页装修数据表
     *
     * @param informationIndex
     * @return
     */
    public Integer updateInformationIndex(InformationIndex informationIndex) {
        int count = informationIndexWriteMapper.updateByPrimaryKeySelective(informationIndex);
        if (count == 0) {
            log.error("根据indexId：" + informationIndex.getIndexId() + "更新资讯首页装修数据表失败");
            throw new MallException("更新资讯首页装修数据表失败,请重试");
        }
        return count;
    }

    /**
     * 根据indexId获取资讯首页装修数据表详情
     *
     * @param indexId indexId
     * @return
     */
    public InformationIndex getInformationIndexByIndexId(Integer indexId) {
        return informationIndexReadMapper.getByPrimaryKey(indexId);
    }

    /**
     * 根据条件获取资讯首页装修数据表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<InformationIndex> getInformationIndexList(InformationIndexExample example, PagerInfo pager) {
        List<InformationIndex> informationIndexList;
        if (pager != null) {
            pager.setRowsCount(informationIndexReadMapper.countByExample(example));
            informationIndexList = informationIndexReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            informationIndexList = informationIndexReadMapper.listByExample(example);
        }
        return informationIndexList;
    }
}