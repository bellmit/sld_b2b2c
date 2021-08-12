package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.RegionCityReadMapper;
import com.slodon.b2b2c.dao.write.system.RegionCityWriteMapper;
import com.slodon.b2b2c.system.example.RegionCityExample;
import com.slodon.b2b2c.system.pojo.RegionCity;
import com.slodon.b2b2c.vo.system.RegionVO;
<<<<<<< HEAD
=======

>>>>>>> wangyl
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
<<<<<<< HEAD
=======

>>>>>>> wangyl
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RegionCityModel {
    @Resource
    private RegionCityReadMapper regionCityReadMapper;

    @Resource
    private RegionCityWriteMapper regionCityWriteMapper;

    /**
     * 新增市级信息表
     *
     * @param regionCity
     * @return
     */
    public Integer saveRegionCity(RegionCity regionCity) {
        int count = regionCityWriteMapper.insert(regionCity);
        if (count == 0) {
            throw new MallException("添加市级信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据id删除市级信息表
     *
     * @param id id
     * @return
     */
    public Integer deleteRegionCity(Integer id) {
        if (StringUtils.isEmpty(id)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = regionCityWriteMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            log.error("根据id：" + id + "删除市级信息表失败");
            throw new MallException("删除市级信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id更新市级信息表
     *
     * @param regionCity
     * @return
     */
    public Integer updateRegionCity(RegionCity regionCity) {
        if (StringUtils.isEmpty(regionCity.getId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = regionCityWriteMapper.updateByPrimaryKeySelective(regionCity);
        if (count == 0) {
            log.error("根据id：" + regionCity.getId() + "更新市级信息表失败");
            throw new MallException("更新市级信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id获取市级信息表详情
     *
     * @param id id
     * @return
     */
    public RegionCity getRegionCityById(Integer id) {
        return regionCityReadMapper.getByPrimaryKey(id);
    }

    /**
     * 根据条件获取市级信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<RegionCity> getRegionCityList(RegionCityExample example, PagerInfo pager) {
        List<RegionCity> regionCityList;
        if (pager != null) {
            pager.setRowsCount(regionCityReadMapper.countByExample(example));
            regionCityList = regionCityReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            regionCityList = regionCityReadMapper.listByExample(example);
        }
        return regionCityList;
    }

    /**
     * 根据条件获取市信息表
     *
     * @return
     */
    public List<RegionVO> getCityList(String remarkCode, String provinceCode) {
        List<RegionVO> list = new ArrayList<>();
        RegionCityExample example = new RegionCityExample();
        example.setRemarkCode(remarkCode);
        if (!StringUtils.isEmpty(provinceCode)) {
            example.setProvinceCode(provinceCode);
        }
        example.setOrderBy("native_code asc");
        List<RegionCity> cityList = regionCityReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(cityList)) {
            for (RegionCity city : cityList) {
                RegionVO vo = new RegionVO();
                vo.setParentCode(city.getProvinceCode());
                vo.setRegionCode(city.getRemarkCode());
                vo.setRegionName(city.getCityName());
                vo.setRegionLevel(city.getRegionLevel());
                vo.setChildren(new ArrayList<>());
                list.add(vo);
            }
        }
        return list;
    }
<<<<<<< HEAD
=======

>>>>>>> wangyl
}