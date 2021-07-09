package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.RegionCityReadMapper;
import com.slodon.b2b2c.dao.read.system.RegionDistrictReadMapper;
import com.slodon.b2b2c.dao.read.system.RegionProvinceReadMapper;
import com.slodon.b2b2c.dao.write.system.RegionProvinceWriteMapper;
import com.slodon.b2b2c.system.example.RegionCityExample;
import com.slodon.b2b2c.system.example.RegionDistrictExample;
import com.slodon.b2b2c.system.example.RegionProvinceExample;
import com.slodon.b2b2c.system.pojo.RegionCity;
import com.slodon.b2b2c.system.pojo.RegionDistrict;
import com.slodon.b2b2c.system.pojo.RegionProvince;
import com.slodon.b2b2c.vo.system.RegionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RegionProvinceModel {

    @Resource
    private RegionProvinceReadMapper regionProvinceReadMapper;
    @Resource
    private RegionProvinceWriteMapper regionProvinceWriteMapper;
    @Resource
    private RegionCityReadMapper regionCityReadMapper;
    @Resource
    private RegionDistrictReadMapper regionDistrictReadMapper;

    /**
     * 新增省份信息表
     *
     * @param regionProvince
     * @return
     */
    public Integer saveRegionProvince(RegionProvince regionProvince) {
        int count = regionProvinceWriteMapper.insert(regionProvince);
        if (count == 0) {
            throw new MallException("添加省份信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据id删除省份信息表
     *
     * @param id id
     * @return
     */
    public Integer deleteRegionProvince(Integer id) {
        if (StringUtils.isEmpty(id)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = regionProvinceWriteMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            log.error("根据id：" + id + "删除省份信息表失败");
            throw new MallException("删除省份信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id更新省份信息表
     *
     * @param regionProvince
     * @return
     */
    public Integer updateRegionProvince(RegionProvince regionProvince) {
        if (StringUtils.isEmpty(regionProvince.getId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = regionProvinceWriteMapper.updateByPrimaryKeySelective(regionProvince);
        if (count == 0) {
            log.error("根据id：" + regionProvince.getId() + "更新省份信息表失败");
            throw new MallException("更新省份信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id获取省份信息表详情
     *
     * @param id id
     * @return
     */
    public RegionProvince getRegionProvinceById(Integer id) {
        return regionProvinceReadMapper.getByPrimaryKey(id);
    }

    /**
     * 根据条件获取省份信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<RegionProvince> getRegionProvinceList(RegionProvinceExample example, PagerInfo pager) {
        List<RegionProvince> regionProvinceList;
        if (pager != null) {
            pager.setRowsCount(regionProvinceReadMapper.countByExample(example));
            regionProvinceList = regionProvinceReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            regionProvinceList = regionProvinceReadMapper.listByExample(example);
        }
        return regionProvinceList;
    }

    /**
     * 获取所有地区(包括省市区)
     *
     * @return
     */
    public List<RegionProvince> getAllRegion(String remarkCode) {
        RegionProvinceExample example = new RegionProvinceExample();
        example.setRemarkCode(remarkCode);
        example.setOrderBy("native_code asc");
        List<RegionProvince> provinceList = regionProvinceReadMapper.listByExample(example);
        for (RegionProvince pro : provinceList) {
            // 查询市级列表
            RegionCityExample cityExample = new RegionCityExample();
            cityExample.setProvinceCode(pro.getRemarkCode());
            cityExample.setOrderBy("native_code asc");
            List<RegionCity> cityList = regionCityReadMapper.listByExample(cityExample);
            if (cityList.isEmpty()) continue;
            pro.setCityList(cityList);
            // 查询区级列表
            for (RegionCity city : cityList) {
                RegionDistrictExample districtExample = new RegionDistrictExample();
                districtExample.setCityCode(city.getRemarkCode());
                districtExample.setOrderBy("native_code asc");
                List<RegionDistrict> districtList = regionDistrictReadMapper.listByExample(districtExample);
                if (districtList.isEmpty()) continue;
                city.setDistrictList(districtList);
            }
        }
        return provinceList;
    }

    /**
     * 根据条件获取省份信息表
     *
     * @return
     */
    public List<RegionVO> getProvinceList(String remarkCode) {
        List<RegionVO> list = new ArrayList<>();
        RegionProvinceExample example = new RegionProvinceExample();
        example.setRemarkCode(remarkCode);
        example.setOrderBy("native_code asc");
        List<RegionProvince> provinceList = regionProvinceReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(provinceList)) {
            for (RegionProvince province : provinceList) {
                RegionVO vo = new RegionVO();
                vo.setParentCode(province.getCountryCode());
                vo.setRegionCode(province.getRemarkCode());
                vo.setRegionName(province.getProvinceName());
                vo.setRegionLevel(province.getRegionLevel());
                vo.setChildren(new ArrayList<>());
                list.add(vo);
            }
        }
        return list;
    }

    /**
     * 获取省级和市级地区
     *
     * @return
     */
    public List<RegionVO> getProvinceAndCity(String remarkCode) {
        List<RegionVO> list = new ArrayList<>();
        RegionProvinceExample example = new RegionProvinceExample();
        example.setRemarkCode(remarkCode);
        example.setOrderBy("native_code asc");
        List<RegionProvince> provinceList = regionProvinceReadMapper.listByExample(example);
        for (RegionProvince province : provinceList) {
            // 查询市级列表
            RegionCityExample cityExample = new RegionCityExample();
            cityExample.setProvinceCode(province.getRemarkCode());
            cityExample.setOrderBy("native_code asc");
            List<RegionCity> cityList = regionCityReadMapper.listByExample(cityExample);
            province.setCityList(cityList);
        }
        if (!CollectionUtils.isEmpty(provinceList)) {
            for (RegionProvince province : provinceList) {
                RegionVO vo = new RegionVO();
                vo.setParentCode(province.getCountryCode());
                vo.setRegionCode(province.getRemarkCode());
                vo.setRegionName(province.getProvinceName());
                vo.setRegionLevel(province.getRegionLevel());
                // 查询市级列表
                RegionCityExample cityExample = new RegionCityExample();
                cityExample.setProvinceCode(province.getRemarkCode());
                cityExample.setOrderBy("native_code asc");
                List<RegionCity> cityList = regionCityReadMapper.listByExample(cityExample);
                if (!CollectionUtils.isEmpty(cityList)) {
                    List<RegionVO> cityVoList = new ArrayList<>();
                    for (RegionCity city : cityList) {
                        RegionVO cityVo = new RegionVO();
                        cityVo.setParentCode(city.getProvinceCode());
                        cityVo.setRegionCode(city.getRemarkCode());
                        cityVo.setRegionName(city.getCityName());
                        cityVo.setRegionLevel(city.getRegionLevel());
                        cityVo.setChildren(new ArrayList<>());
                        cityVoList.add(cityVo);
                    }
                    vo.setChildren(cityVoList);
                } else {
                    vo.setChildren(new ArrayList<>());
                }
                list.add(vo);
            }
        }
        return list;
    }
}