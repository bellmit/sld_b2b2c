package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.CouponGoodsCategoryReadMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponGoodsCategoryWriteMapper;
import com.slodon.b2b2c.promotion.example.CouponGoodsCategoryExample;
import com.slodon.b2b2c.promotion.pojo.CouponGoodsCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class CouponGoodsCategoryModel {

    @Resource
    private CouponGoodsCategoryReadMapper couponGoodsCategoryReadMapper;
    @Resource
    private CouponGoodsCategoryWriteMapper couponGoodsCategoryWriteMapper;

    /**
     * 新增优惠券和商品分类的绑定表
     *
     * @param couponGoodsCategory
     * @return
     */
    public Integer saveCouponGoodsCategory(CouponGoodsCategory couponGoodsCategory) {
        int count = couponGoodsCategoryWriteMapper.insert(couponGoodsCategory);
        if (count == 0) {
            throw new MallException("添加优惠券和商品分类的绑定表失败，请重试");
        }
        return count;
    }

    /**
     * 根据couponCategoryId删除优惠券和商品分类的绑定表
     *
     * @param couponCategoryId couponCategoryId
     * @return
     */
    public Integer deleteCouponGoodsCategory(Integer couponCategoryId) {
        if (StringUtils.isEmpty(couponCategoryId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = couponGoodsCategoryWriteMapper.deleteByPrimaryKey(couponCategoryId);
        if (count == 0) {
            log.error("根据couponCategoryId：" + couponCategoryId + "删除优惠券和商品分类的绑定表失败");
            throw new MallException("删除优惠券和商品分类的绑定表失败,请重试");
        }
        return count;
    }

    /**
     * 根据couponCategoryId更新优惠券和商品分类的绑定表
     *
     * @param couponGoodsCategory
     * @return
     */
    public Integer updateCouponGoodsCategory(CouponGoodsCategory couponGoodsCategory) {
        if (StringUtils.isEmpty(couponGoodsCategory.getCouponCategoryId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = couponGoodsCategoryWriteMapper.updateByPrimaryKeySelective(couponGoodsCategory);
        if (count == 0) {
            log.error("根据couponCategoryId：" + couponGoodsCategory.getCouponCategoryId() + "更新优惠券和商品分类的绑定表失败");
            throw new MallException("更新优惠券和商品分类的绑定表失败,请重试");
        }
        return count;
    }

    /**
     * 根据couponCategoryId获取优惠券和商品分类的绑定表详情
     *
     * @param couponCategoryId couponCategoryId
     * @return
     */
    public CouponGoodsCategory getCouponGoodsCategoryByCouponCategoryId(Integer couponCategoryId) {
        return couponGoodsCategoryReadMapper.getByPrimaryKey(couponCategoryId);
    }

    /**
     * 根据条件获取优惠券和商品分类的绑定表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<CouponGoodsCategory> getCouponGoodsCategoryList(CouponGoodsCategoryExample example, PagerInfo pager) {
        List<CouponGoodsCategory> couponGoodsCategoryList;
        if (pager != null) {
            pager.setRowsCount(couponGoodsCategoryReadMapper.countByExample(example));
            couponGoodsCategoryList = couponGoodsCategoryReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            couponGoodsCategoryList = couponGoodsCategoryReadMapper.listByExample(example);
        }
        return couponGoodsCategoryList;
    }

    /**
     * 根据指定字段查询（group by特殊情况处理）
     *
     * @param fields
     * @param example
     * @return
     */
    public List<CouponGoodsCategory> getCouponGoodsCategoryListByField(String fields, CouponGoodsCategoryExample example) {
        return couponGoodsCategoryReadMapper.listFieldsByExample(fields, example);
    }
}