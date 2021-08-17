package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.RegionDistrictReadMapper;
import com.slodon.b2b2c.dao.write.system.RegionDistrictWriteMapper;
import com.slodon.b2b2c.system.example.RegionDistrictExample;
import com.slodon.b2b2c.system.pojo.RegionDistrict;
import com.slodon.b2b2c.vo.system.PostInfoVO;
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
public class RegionDistrictModel {
    @Resource
    private RegionDistrictReadMapper regionDistrictReadMapper;

    @Resource
    private RegionDistrictWriteMapper regionDistrictWriteMapper;

    /**
     * 新增区县信息表
     *
     * @param regionDistrict
     * @return
     */
    public Integer saveRegionDistrict(RegionDistrict regionDistrict) {
        int count = regionDistrictWriteMapper.insert(regionDistrict);
        if (count == 0) {
            throw new MallException("添加区县信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据id删除区县信息表
     *
     * @param id id
     * @return
     */
    public Integer deleteRegionDistrict(Integer id) {
        if (StringUtils.isEmpty(id)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = regionDistrictWriteMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            log.error("根据id：" + id + "删除区县信息表失败");
            throw new MallException("删除区县信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id更新区县信息表
     *
     * @param regionDistrict
     * @return
     */
    public Integer updateRegionDistrict(RegionDistrict regionDistrict) {
        if (StringUtils.isEmpty(regionDistrict.getId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = regionDistrictWriteMapper.updateByPrimaryKeySelective(regionDistrict);
        if (count == 0) {
            log.error("根据id：" + regionDistrict.getId() + "更新区县信息表失败");
            throw new MallException("更新区县信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id获取区县信息表详情
     *
     * @param id id
     * @return
     */
    public RegionDistrict getRegionDistrictById(Integer id) {
        return regionDistrictReadMapper.getByPrimaryKey(id);
    }

    /**
     * 根据条件获取区县信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<RegionDistrict> getRegionDistrictList(RegionDistrictExample example, PagerInfo pager) {
        List<RegionDistrict> regionDistrictList;
        if (pager != null) {
            pager.setRowsCount(regionDistrictReadMapper.countByExample(example));
            regionDistrictList = regionDistrictReadMapper.listPageByExample(example, pager.getStart(),
                    pager.getPageSize());
        } else {
            regionDistrictList = regionDistrictReadMapper.listByExample(example);
        }
        return regionDistrictList;
    }

    /**
     * 根据条件获取区信息表
     *
     * @return
     */
    public List<RegionVO> getDistrictList(String remarkCode, String cityCode) {
        List<RegionVO> list = new ArrayList<>();
        RegionDistrictExample example = new RegionDistrictExample();
        example.setRemarkCode(remarkCode);
        if (!StringUtils.isEmpty(cityCode)) {
            example.setCityCode(cityCode);
        }
        example.setOrderBy("native_code asc");
        List<RegionDistrict> districtList = regionDistrictReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(districtList)) {
            for (RegionDistrict district : districtList) {
                RegionVO vo = new RegionVO();
                vo.setParentCode(district.getCityCode());
                vo.setRegionCode(district.getRemarkCode());
                vo.setRegionName(district.getDistrictName());
                vo.setRegionLevel(district.getRegionLevel());
                vo.setChildren(new ArrayList<>());
                list.add(vo);
            }
        }
        return list;
    }

//    /**
//     * 根据邮政编码获取区信息表
//     *
//     * @return
//     */
//    public List<PostInfoVO> getDistrictByPostcode(String postCode) {
//        List<PostInfoVO> list = new ArrayList<>();
//        RegionDistrictExample example = new RegionDistrictExample();
//        example.setNativeCode(postCode);
//        example.setOrderBy("native_code asc");
//        List<RegionDistrict> districtList = regionDistrictReadMapper.listByExample(example);
//        if (!CollectionUtils.isEmpty(districtList)) {
//            for (RegionDistrict district : districtList) {
//                PostInfoVO vo = new PostInfoVO();
//                vo.setPostCode(district.getNativeCode());
//                vo.setProviceCode(district.getProvinceCode());
//                vo.setProviceName(district.getProvinceName());
//                vo.setCityCode(district.getCityCode());
//                vo.setCityName(district.getCityName());
//                vo.setAddressLine(district.getDistrictName());
//                list.add(vo);
//            }
//        }
//        return list;
//    }


    /**
     * 根据条件获取区县信息表列表
     *
     */
    public List<PostInfoVO> getPostList(String remarkCode) {
        List<PostInfoVO> postInfo = new ArrayList<>();
        RegionDistrictExample example = new RegionDistrictExample();
        example.setRemarkCode(remarkCode);
        example.setOrderBy("native_code asc");
        List<RegionDistrict> postList = regionDistrictReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(postList)) {
            for (RegionDistrict post : postList) {
                PostInfoVO vo = new PostInfoVO();
                vo.setPostCode(post.getNativeCode());
                vo.setProviceCode(post.getProvinceCode());
                vo.setProviceName(post.getProvinceName());
                vo.setCityCode(post.getCityCode());
                vo.setCityName(post.getCityName());
                vo.setAddressLine(post.getDistrictName());
                postInfo.add(vo);
            }
        }
        return postInfo;
    }
}