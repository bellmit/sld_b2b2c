package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.seller.StoreBindCategoryReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreBindCategoryWriteMapper;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.pojo.StoreBindCategory;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StoreBindCategoryModel {

    @Resource
    private StoreBindCategoryReadMapper storeBindCategoryReadMapper;
    @Resource
    private StoreBindCategoryWriteMapper storeBindCategoryWriteMapper;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;

    /**
     * 新增店铺可用商品分类（已申请绑定），自营平台默认可用所有分类
     *
     * @param storeBindCategory
     * @return
     */
    public Integer saveStoreBindCategory(StoreBindCategory storeBindCategory) {
        int count = storeBindCategoryWriteMapper.insert(storeBindCategory);
        if (count == 0) {
            throw new MallException("添加店铺可用商品分类（已申请绑定），自营平台默认可用所有分类失败，请重试");
        }
        return count;
    }

    /**
     * 提交经营类目申请
     *
     * @param goodsCateIds 申请分类id字符串,例1级-2级-3级,1级-2级-3级
     * @param vendor       操作员
     * @return
     */
    public Integer commitApply(String goodsCateIds, Vendor vendor) {
        StoreBindCategory storeBindCategory = new StoreBindCategory();
        storeBindCategory.setStoreId(vendor.getStoreId());
        storeBindCategory.setCreateVendorId(vendor.getVendorId());
        storeBindCategory.setCreateTime(new Date());
        storeBindCategory.setState(StoreConst.STORE_CATEGORY_STATE_SEND);

        String[] split = goodsCateIds.split(",");
        Integer count = 0;
        for (String goodsCategoryId : split) {
            String[] split1 = goodsCategoryId.split("-");
            storeBindCategory.setGoodsCategoryId1(Integer.parseInt(split1[0]));
            storeBindCategory.setGoodsCategoryId2(Integer.parseInt(split1[1]));
            storeBindCategory.setGoodsCategoryId3(Integer.parseInt(split1[2]));
            //根据一级分类id查询一级分类名称
            GoodsCategory goodsCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(Integer.parseInt(split1[0]));
            //根据二级分类id查询二级分类名称
            GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(Integer.parseInt(split1[1]));
            //根据三级分类id查询三级分类名称
            GoodsCategory goodsCategory3 = goodsCategoryModel.getGoodsCategoryByCategoryId(Integer.parseInt(split1[2]));

            //判断该分类是否符合条件
            StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
            storeBindCategoryExample.setStoreId(vendor.getStoreId());
            storeBindCategoryExample.setGoodsCategoryId3(Integer.parseInt(split1[2]));
            storeBindCategoryExample.setStateNotEquals(StoreConst.STORE_CATEGORY_STATE_FALSE);
            List<StoreBindCategory> storeBindCategoryList = storeBindCategoryReadMapper.listByExample(storeBindCategoryExample);
            AssertUtil.isTrue(!CollectionUtils.isEmpty(storeBindCategoryList), "分类名称：" + goodsCategory3.getCategoryName() + "的申请已存在");
            //拼接类目组合名称
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(goodsCategory1.getCategoryName());
            stringBuffer.append(">");
            stringBuffer.append(goodsCategory2.getCategoryName());
            stringBuffer.append(">");
            stringBuffer.append(goodsCategory3.getCategoryName());
            storeBindCategory.setGoodsCateName(stringBuffer.toString());
            storeBindCategory.setScaling(goodsCategory3.getScaling());
            count = storeBindCategoryWriteMapper.insert(storeBindCategory);
            if (count == 0) {
                throw new MallException("店铺可用商品分类表添加失败，请重试");
            }
        }
        return count;
    }

    /**
     * 根据bindId删除店铺可用商品分类（已申请绑定），自营平台默认可用所有分类
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteStoreBindCategory(Integer bindId) {
        int count = storeBindCategoryWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除店铺可用商品分类（已申请绑定），自营平台默认可用所有分类失败");
            throw new MallException("删除店铺可用商品分类（已申请绑定），自营平台默认可用所有分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId更新店铺可用商品分类（已申请绑定），自营平台默认可用所有分类
     *
     * @param storeBindCategory
     * @return
     */
    public Integer updateStoreBindCategory(StoreBindCategory storeBindCategory) {
        if (StringUtils.isEmpty(storeBindCategory.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeBindCategoryWriteMapper.updateByPrimaryKeySelective(storeBindCategory);
        if (count == 0) {
            log.error("根据bindId：" + storeBindCategory.getBindId() + "更新店铺可用商品分类（已申请绑定），自营平台默认可用所有分类失败");
            throw new MallException("更新店铺可用商品分类（已申请绑定），自营平台默认可用所有分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId审核绑定商品分类申请
     *
     * @param bindIds      绑定id串，以逗号隔开
     * @param isPass       是否通过[true==通过,false==拒绝]
     * @param refuseReason 审核拒绝理由
     * @param admin        审核管理员
     * @return
     */
    public Integer auditBindCate(String bindIds, Boolean isPass, String refuseReason, Admin admin) {

        String[] split = bindIds.split(",");
        int count = 0;
        for (String bindId : split) {
            //根据bindId查询数据库
            StoreBindCategory storeBindCategory = storeBindCategoryReadMapper.getByPrimaryKey(bindId);
            if (storeBindCategory == null) {
                //数据库里不存在该申请
                throw new MallException("您无权进行此操作");
            }

            //计算审核后的状态
            int newState = 0;
            if (storeBindCategory.getState() == StoreConst.STORE_CATEGORY_STATE_SEND) {
                if (isPass) {
                    newState = StoreConst.STORE_CATEGORY_STATE_PASS;
                } else {
                    newState = StoreConst.STORE_CATEGORY_STATE_FALSE;
                }
                storeBindCategory.setState(newState);
            }
            storeBindCategory.setRefuseReason(refuseReason == null ? "" : refuseReason);
            storeBindCategory.setAuditAdminId(admin.getAdminId());
            storeBindCategory.setAuditTime(new Date());
            count = storeBindCategoryWriteMapper.updateByPrimaryKeySelective(storeBindCategory);
            if (count == 0) {
                throw new MallException("更新店铺可用商品分类失败");
            }
        }
        return count;
    }

    /**
     * 根据bindId获取店铺可用商品分类（已申请绑定），自营平台默认可用所有分类详情
     *
     * @param bindId bindId
     * @return
     */
    public StoreBindCategory getStoreBindCategoryByBindId(Integer bindId) {
        return storeBindCategoryReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取店铺可用商品分类（已申请绑定），自营平台默认可用所有分类列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreBindCategory> getStoreBindCategoryList(StoreBindCategoryExample example, PagerInfo pager) {
        List<StoreBindCategory> storeBindCategoryList;
        if (pager != null) {
            pager.setRowsCount(storeBindCategoryReadMapper.countByExample(example));
            storeBindCategoryList = storeBindCategoryReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeBindCategoryList = storeBindCategoryReadMapper.listByExample(example);
        }
        return storeBindCategoryList;
    }
}