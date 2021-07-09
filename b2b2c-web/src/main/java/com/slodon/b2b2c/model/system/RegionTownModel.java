package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.RegionTownReadMapper;
import com.slodon.b2b2c.dao.write.system.RegionTownWriteMapper;
import com.slodon.b2b2c.system.example.RegionTownExample;
import com.slodon.b2b2c.system.pojo.RegionTown;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class RegionTownModel {
    @Resource
    private RegionTownReadMapper regionTownReadMapper;

    @Resource
    private RegionTownWriteMapper regionTownWriteMapper;

    /**
     * 新增乡镇(街道)信息表
     *
     * @param regionTown
     * @return
     */
    public Integer saveRegionTown(RegionTown regionTown) {
        int count = regionTownWriteMapper.insert(regionTown);
        if (count == 0) {
            throw new MallException("添加乡镇(街道)信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据id删除乡镇(街道)信息表
     *
     * @param id id
     * @return
     */
    public Integer deleteRegionTown(Integer id) {
        if (StringUtils.isEmpty(id)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = regionTownWriteMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            log.error("根据id：" + id + "删除乡镇(街道)信息表失败");
            throw new MallException("删除乡镇(街道)信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id更新乡镇(街道)信息表
     *
     * @param regionTown
     * @return
     */
    public Integer updateRegionTown(RegionTown regionTown) {
        if (StringUtils.isEmpty(regionTown.getId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = regionTownWriteMapper.updateByPrimaryKeySelective(regionTown);
        if (count == 0) {
            log.error("根据id：" + regionTown.getId() + "更新乡镇(街道)信息表失败");
            throw new MallException("更新乡镇(街道)信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id获取乡镇(街道)信息表详情
     *
     * @param id id
     * @return
     */
    public RegionTown getRegionTownById(Integer id) {
        return regionTownReadMapper.getByPrimaryKey(id);
    }

    /**
     * 根据条件获取乡镇(街道)信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<RegionTown> getRegionTownList(RegionTownExample example, PagerInfo pager) {
        List<RegionTown> regionTownList;
        if (pager != null) {
            pager.setRowsCount(regionTownReadMapper.countByExample(example));
            regionTownList = regionTownReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            regionTownList = regionTownReadMapper.listByExample(example);
        }
        return regionTownList;
    }
}