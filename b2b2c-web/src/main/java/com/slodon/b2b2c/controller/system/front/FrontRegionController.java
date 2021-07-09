package com.slodon.b2b2c.controller.system.front;

import com.alibaba.fastjson.JSONArray;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.model.system.RegionProvinceModel;
import com.slodon.b2b2c.system.pojo.RegionCity;
import com.slodon.b2b2c.system.pojo.RegionDistrict;
import com.slodon.b2b2c.system.pojo.RegionProvince;
import com.slodon.b2b2c.vo.system.RegionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-区域列表")
@RestController
@RequestMapping("v3/system/front/region")
public class FrontRegionController extends BaseController {

    @Resource
    private RegionProvinceModel regionProvinceModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("获取地区列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remarkCode", value = "地区编码", paramType = "query"),
            @ApiImplicitParam(name = "regionLevel", value = "子地区级别[1省级，2市级，3区级]", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<List<RegionVO>> getRegionList(String remarkCode, Integer regionLevel) {
        List<RegionVO> list = new ArrayList<>();
        if (null == remarkCode) {
            if (null == regionLevel || regionLevel == 3) {
                //获取所有地址，先查redis缓存是否有数据
                String allRegion = stringRedisTemplate.opsForValue().get(RedisConst.REGION);
                if (StringUtils.isEmpty(allRegion)) {
                    //redis缓存数据无效，从数据库查，并放入缓存
                    List<RegionProvince> allRegionList = regionProvinceModel.getAllRegion(null);
                    List<RegionVO> resultList = this.getRegionList(allRegionList);
                    stringRedisTemplate.opsForValue().set(RedisConst.REGION, JSONArray.toJSONString(resultList));
                    return SldResponse.success(resultList);
                } else {
                    return SldResponse.success(JSONArray.parseArray(allRegion, RegionVO.class));
                }
            } else if (regionLevel == 2) {
                list = regionProvinceModel.getProvinceAndCity(null);
            } else if (regionLevel == 1) {
                list = regionProvinceModel.getProvinceList(null);
            } else {
                return SldResponse.success(null);
            }
            return SldResponse.success(list);
        } else {
            if (StringUtils.isEmpty(regionLevel) || regionLevel < 0 || regionLevel > 3)
                return SldResponse.badArgumentValue();
            if (regionLevel == 1) {
                list = regionProvinceModel.getProvinceList(remarkCode);
            } else if (regionLevel == 2) {
                list = regionProvinceModel.getProvinceAndCity(remarkCode);
            } else if (regionLevel == 3) {
                List<RegionProvince> allRegionList = regionProvinceModel.getAllRegion(remarkCode);
                list = this.getRegionList(allRegionList);
            }
            return SldResponse.success(list);
        }
    }

    /**
     * 组装省级地区
     *
     * @param list
     * @return
     */
    public List<RegionVO> getRegionList(List<RegionProvince> list) {
        List<RegionVO> voList = new ArrayList<>();
        for (RegionProvince province : list) {
            RegionVO vo = new RegionVO();
            vo.setParentCode(province.getCountryCode());
            vo.setRegionCode(province.getRemarkCode());
            vo.setRegionName(province.getProvinceName());
            vo.setRegionLevel(province.getRegionLevel());
            if (!CollectionUtils.isEmpty(province.getCityList())) {
                vo.setChildren(getRegionCityList(province.getCityList()));
            } else {
                vo.setChildren(new ArrayList<>());
            }
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 组装市级地区
     *
     * @param list
     * @return
     */
    public List<RegionVO> getRegionCityList(List<RegionCity> list) {
        List<RegionVO> voList = new ArrayList<>();
        for (RegionCity city : list) {
            RegionVO vo = new RegionVO();
            vo.setParentCode(city.getProvinceCode());
            vo.setRegionCode(city.getRemarkCode());
            vo.setRegionName(city.getCityName());
            vo.setRegionLevel(city.getRegionLevel());
            if (!CollectionUtils.isEmpty(city.getDistrictList())) {
                vo.setChildren(getRegionAreaList(city.getDistrictList()));
            } else {
                vo.setChildren(new ArrayList<>());
            }
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 组装区级地区
     *
     * @param list
     * @return
     */
    public List<RegionVO> getRegionAreaList(List<RegionDistrict> list) {
        List<RegionVO> voList = new ArrayList<>();
        for (RegionDistrict district : list) {
            RegionVO vo = new RegionVO();
            vo.setParentCode(district.getCityCode());
            vo.setRegionCode(district.getRemarkCode());
            vo.setRegionName(district.getDistrictName());
            vo.setRegionLevel(district.getRegionLevel());
            vo.setChildren(new ArrayList<>());
            voList.add(vo);
        }
        return voList;
    }
}
