package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsBrandReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsBrandWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsCategoryBindBrandWriteMapper;
import com.slodon.b2b2c.goods.dto.ApplyBrandAddDTO;
import com.slodon.b2b2c.goods.dto.ApplyBrandUpdateDTO;
import com.slodon.b2b2c.goods.dto.GoodsBrandAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsBrandUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsBrandExample;
import com.slodon.b2b2c.goods.example.GoodsCategoryBindBrandExample;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.GoodsBrandFrontVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class GoodsBrandModel {

    @Resource
    private GoodsBrandReadMapper goodsBrandReadMapper;
    @Resource
    private GoodsBrandWriteMapper goodsBrandWriteMapper;
    @Resource
    private GoodsCategoryBindBrandWriteMapper goodsCategoryBindBrandWriteMapper;
    @Resource
    private StoreModel storeModel;

    /**
     * 新增商品品牌
     *
     * @param goodsBrand
     * @return
     */
    public Integer saveGoodsBrand(GoodsBrand goodsBrand) {
        int count = goodsBrandWriteMapper.insert(goodsBrand);
        if (count == 0) {
            throw new MallException("添加商品品牌失败，请重试");
        }
        return count;
    }

    /**
     * 新增品牌
     *
     * @param goodSBrandAddDTO
     * @param adminId
     * @return
     */
    @Transactional
    public Integer saveGoodsBrand(Integer adminId, GoodsBrandAddDTO goodSBrandAddDTO) {

        //判断品牌名称是否重复
        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setBrandName(goodSBrandAddDTO.getBrandName());
        List<GoodsBrand> goodsBrands = goodsBrandReadMapper.listByExample(goodsBrandExample);
        if (!CollectionUtils.isEmpty(goodsBrands)) {
            throw new MallException("品牌名称已存在，请重新填写");
        }

        //插入品牌表
        GoodsBrand goodsBrandInsert = new GoodsBrand();
        goodsBrandInsert.setBrandName(goodSBrandAddDTO.getBrandName());
        goodsBrandInsert.setBrandDesc(goodSBrandAddDTO.getBrandDesc());
        goodsBrandInsert.setImage(goodSBrandAddDTO.getImage());
        goodsBrandInsert.setBrandInitial(StringUtil.GetFirstChar(goodSBrandAddDTO.getBrandName()));
        goodsBrandInsert.setCreateTime(new Date());
        goodsBrandInsert.setIsRecommend(GoodsConst.IS_BRAND_RECOMMEND_YES);
        goodsBrandInsert.setSort(GoodsConst.BRAND_SORT);
        goodsBrandInsert.setCreateAdminId(adminId);
        goodsBrandInsert.setApplyVendorId(0L);
        goodsBrandInsert.setApplyStoreId(0L);
        goodsBrandInsert.setState(GoodsConst.BRAND_STATE_1);
        int count;
        count = goodsBrandWriteMapper.insert(goodsBrandInsert);
        if (count == 0) {
            throw new MallException("添加品牌表失败，请重试");
        }
        return count;
    }

    /**
     * 商户申请品牌
     *
     * @param applyBrandAddDTO
     * @return
     */
    @Transactional
    public int saveSellersBrand(ApplyBrandAddDTO applyBrandAddDTO, Vendor vendor, String goodsCategoryPath) {
        //判断品牌名称是否重复
        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setBrandName(applyBrandAddDTO.getBrandName());
        List<GoodsBrand> goodsBrands = goodsBrandReadMapper.listByExample(goodsBrandExample);
        if (!CollectionUtils.isEmpty(goodsBrands)) {
            throw new MallException("品牌名称已存在，请重新填写");
        }

        //插入品牌表
        GoodsBrand goodsBrandInsert = new GoodsBrand();
        goodsBrandInsert.setBrandName(applyBrandAddDTO.getBrandName());
        goodsBrandInsert.setBrandDesc(applyBrandAddDTO.getBrandDesc());
        goodsBrandInsert.setImage(applyBrandAddDTO.getImage());
        goodsBrandInsert.setBrandInitial(StringUtil.GetFirstChar(applyBrandAddDTO.getBrandName()));
        goodsBrandInsert.setCreateTime(new Date());
        goodsBrandInsert.setUpdateTime(new Date());
        goodsBrandInsert.setIsRecommend(GoodsConst.IS_BRAND_RECOMMEND_YES);
        goodsBrandInsert.setSort(GoodsConst.BRAND_SORT);
        //商户申请的品牌adminId
        goodsBrandInsert.setCreateAdminId(0);
        goodsBrandInsert.setApplyVendorId(vendor.getVendorId());
        goodsBrandInsert.setApplyStoreId(vendor.getStoreId());
        Store store = storeModel.getStoreByStoreId(vendor.getStoreId());
        goodsBrandInsert.setStoreName(store.getStoreName());
        goodsBrandInsert.setState(GoodsConst.BRAND_STATE_2);
        goodsBrandInsert.setGoodsCategoryId3(applyBrandAddDTO.getCategoryId());
        goodsBrandInsert.setGoodsCategoryPath(goodsCategoryPath);
        int count;
        count = goodsBrandWriteMapper.insert(goodsBrandInsert);
        if (count == 0) {
            throw new MallException("添加品牌表失败，请重试");
        }
        return count;
    }

    /**
     * 根据brandId删除商品品牌
     *
     * @param brandId brandId
     * @return
     */
    @Transactional
    public Integer deleteGoodsBrand(Integer brandId) {
        if (StringUtils.isEmpty(brandId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count;
        //删除商品分类与品牌定关系表
        GoodsCategoryBindBrandExample categoryBindBrandExample = new GoodsCategoryBindBrandExample();
        categoryBindBrandExample.setBrandId(brandId);
        count = goodsCategoryBindBrandWriteMapper.deleteByExample(categoryBindBrandExample);
        //删除品牌
        count = goodsBrandWriteMapper.deleteByPrimaryKey(brandId);
        if (count == 0) {
            log.error("根据brandId：" + brandId + "删除商品品牌失败");
            throw new MallException("删除商品品牌失败,请重试");
        }
        return count;
    }

    /**
     * 根据categoryId删除商品品牌
     *
     * @param categoryId categoryId
     * @return
     */
    public Integer delGoodsBrandByCategoryId(Integer categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new MallException("请选择要删除的数据");
        }
        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setGoodsCategoryId3(categoryId);
        return goodsBrandWriteMapper.deleteByExample(goodsBrandExample);
    }

    /**
     * 根据brandId更新商品品牌
     *
     * @param goodsBrand
     * @return
     */
    public Integer updateGoodsBrand(GoodsBrand goodsBrand) {
        if (StringUtils.isEmpty(goodsBrand.getBrandId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsBrandWriteMapper.updateByPrimaryKeySelective(goodsBrand);
        if (count == 0) {
            log.error("根据brandId：" + goodsBrand.getBrandId() + "更新商品品牌失败");
            throw new MallException("更新商品品牌失败,请重试");
        }
        return count;
    }


    /**
     * 根据brandId更新商品品牌
     *
     * @param goodsBrandUpdateDTO
     * @param adminId
     * @return
     */
    @Transactional
    public Integer updateGoodsBrand(Integer adminId, GoodsBrandUpdateDTO goodsBrandUpdateDTO) throws Exception {
        //判断品牌名称是否重复
        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setBrandName(goodsBrandUpdateDTO.getBrandName());
        goodsBrandExample.setBrandIdNotEquals(goodsBrandUpdateDTO.getBrandId());
        List<GoodsBrand> goodsBrands = goodsBrandReadMapper.listByExample(goodsBrandExample);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(goodsBrands), "品牌名称已存在，请重新填写");
        //修改品牌表
        GoodsBrand goodsBrandUpdate = new GoodsBrand();
        PropertyUtils.copyProperties(goodsBrandUpdate, goodsBrandUpdateDTO);
        goodsBrandUpdate.setUpdateTime(new Date());
        goodsBrandUpdate.setUpdateAdminId(adminId);

        int count = goodsBrandWriteMapper.updateByPrimaryKeySelective(goodsBrandUpdate);
        if (count == 0) {
            log.error("根据brandID：" + goodsBrandUpdate.getBrandId() + "更新品牌表失败");
            throw new MallException("更新品牌表失败,请重试");
        }
        return count;
    }

    /**
     * 根据brandId更新商户申请的品牌
     *
     * @param applyBrandUpdateDTO
     * @param vendor
     * @return
     */
    @Transactional
    public Integer updateSellersBrand(ApplyBrandUpdateDTO applyBrandUpdateDTO, Vendor vendor, String goodsCategoryPath) {
        //判断品牌名称是否重复
        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setBrandName(applyBrandUpdateDTO.getBrandName());
        goodsBrandExample.setBrandIdNotEquals(applyBrandUpdateDTO.getBrandId());
        List<GoodsBrand> goodsBrands = goodsBrandReadMapper.listByExample(goodsBrandExample);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(goodsBrands), "品牌名称已存在，请重新填写");

        //查询已有品牌,判断是否是当前店铺的品牌
        GoodsBrand oldGoodsBrand = getGoodsBrandByBrandId(applyBrandUpdateDTO.getBrandId());
        AssertUtil.isTrue(!oldGoodsBrand.getApplyStoreId().equals(vendor.getStoreId()), "没有操作权限");

        //更新品牌表
        GoodsBrand goodsBrandUpdate = new GoodsBrand();
        goodsBrandUpdate.setBrandId(applyBrandUpdateDTO.getBrandId());
        goodsBrandUpdate.setBrandName(applyBrandUpdateDTO.getBrandName());
        goodsBrandUpdate.setBrandDesc(applyBrandUpdateDTO.getBrandDesc());
        goodsBrandUpdate.setImage(applyBrandUpdateDTO.getImage());
        goodsBrandUpdate.setBrandInitial(StringUtil.GetFirstChar(applyBrandUpdateDTO.getBrandName()));
        goodsBrandUpdate.setUpdateTime(new Date());
        goodsBrandUpdate.setIsRecommend(GoodsConst.IS_BRAND_RECOMMEND_YES);
        goodsBrandUpdate.setSort(GoodsConst.BRAND_SORT);
        goodsBrandUpdate.setFailReason("");
        //商户申请的品牌adminId
        goodsBrandUpdate.setState(GoodsConst.BRAND_STATE_2);
        goodsBrandUpdate.setGoodsCategoryId3(applyBrandUpdateDTO.getCategoryId());
        goodsBrandUpdate.setGoodsCategoryPath(goodsCategoryPath);
        int count = goodsBrandWriteMapper.updateByPrimaryKeySelective(goodsBrandUpdate);
        if (count == 0) {
            throw new MallException("更新品牌表失败，请重试");
        }
        return count;

    }

    /**
     * 根据brandId获取商品品牌详情
     *
     * @param brandId brandId
     * @return
     */
    public GoodsBrand getGoodsBrandByBrandId(Integer brandId) {
        return goodsBrandReadMapper.getByPrimaryKey(brandId);
    }

    /**
     * 根据条件获取商品品牌列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsBrand> getGoodsBrandList(GoodsBrandExample example, PagerInfo pager) {
        List<GoodsBrand> goodsBrandList;
        if (pager != null) {
            pager.setRowsCount(goodsBrandReadMapper.countByExample(example));
            goodsBrandList = goodsBrandReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsBrandList = goodsBrandReadMapper.listByExample(example);
        }
        return goodsBrandList;
    }

    /**
     * 根据条件获取商品品牌列表
     *
     * @param pager 分页信息
     * @return
     */
    public List<GoodsBrandFrontVO> getGoodsBrandListBy(PagerInfo pager) {
        //根据品牌首字母分组查询
        GoodsBrandExample brandExample = new GoodsBrandExample();
        //最多26个英文字母
        List<String> list1 = goodsBrandReadMapper.groupListFieldsPageByExample(0, 26);
        pager.setRowsCount(list1.size());
        //分组查询
        List<String> list = goodsBrandReadMapper.groupListFieldsPageByExample(pager.getStart(), pager.getPageSize());

        //响应
        List<GoodsBrandFrontVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (String brandInitial : list) {
                //循环一次为一个品牌首字母
                GoodsBrandFrontVO vo = new GoodsBrandFrontVO();
                vo.setBrandInitial(brandInitial);

                //按照品牌首字母重新查询品牌集合
                GoodsBrandExample example = new GoodsBrandExample();
                example.setBrandInitial(brandInitial);
                List<GoodsBrand> brandList = goodsBrandReadMapper.listByExample(example);

                List<GoodsBrandFrontVO.GoodsBrandInfo> infos = new ArrayList<>();
                brandList.forEach(goodsBrand -> {
                    GoodsBrandFrontVO.GoodsBrandInfo goodsBrandInfo = new GoodsBrandFrontVO.GoodsBrandInfo(goodsBrand);
                    infos.add(goodsBrandInfo);
                });
                vo.setGoodsBrandInfoList(infos);

                //vos集合添加数据
                vos.add(vo);
            }
        }

        return vos;
    }

    /**
     * 删除商品品牌
     *
     * @param brandIds
     * @return
     */
    @Transactional
    public Integer deleteBrand(String brandIds) {
        //删除商品分类与品牌定关系表
        GoodsCategoryBindBrandExample bindBrandExample = new GoodsCategoryBindBrandExample();
        bindBrandExample.setBrandIdIn(brandIds);
        goodsCategoryBindBrandWriteMapper.deleteByExample(bindBrandExample);

        //删除商品品牌
        GoodsBrandExample example = new GoodsBrandExample();
        example.setBrandIdIn(brandIds);
        int count = goodsBrandWriteMapper.deleteByExample(example);
        if (count == 0) {
            throw new MallException("删除商品品牌失败,请重试");
        }
        return count;
    }
}