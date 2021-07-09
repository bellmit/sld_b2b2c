/**
 * Copyright@Slodon since 2015, All rights reserved.
 *
 * 注意：
 * 本软件为北京商联达科技有限公司开发研制，未经许可不得使用
 * 购买后可获得源代码使用权（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 * 商业使用请联系: bd@slodon.com 或 拨打全国统一热线 400-881-0877
 * 网址：http://www.slodon.com
 */

package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.CouponGoodsReadMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponGoodsWriteMapper;
import com.slodon.b2b2c.promotion.example.CouponGoodsExample;
import com.slodon.b2b2c.promotion.pojo.CouponGoods;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 */
@Component
@Slf4j
public class CouponGoodsModel {
    @Resource
    private CouponGoodsReadMapper couponGoodsReadMapper;

    @Resource
    private CouponGoodsWriteMapper couponGoodsWriteMapper;

    /**
     * 新增优惠券和产品的绑定表
     *
     * @param couponGoods
     * @return
     */
    public Integer saveCouponGoods(CouponGoods couponGoods) {
        int count = couponGoodsWriteMapper.insert(couponGoods);
        if (count == 0) {
            throw new MallException("添加优惠券和产品的绑定表失败，请重试");
        }
        return count;
    }

    /**
     * 根据couponGoodsId删除优惠券和产品的绑定表
     *
     * @param couponGoodsId couponGoodsId
     * @return
     */
    public Integer deleteCouponGoods(Integer couponGoodsId) {
        if (StringUtils.isEmpty(couponGoodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = couponGoodsWriteMapper.deleteByPrimaryKey(couponGoodsId);
        if (count == 0) {
            log.error("根据couponGoodsId：" + couponGoodsId + "删除优惠券和产品的绑定表失败");
            throw new MallException("删除优惠券和产品的绑定表失败,请重试");
        }
        return count;
    }

    /**
     * 根据couponGoodsId更新优惠券和产品的绑定表
     *
     * @param couponGoods
     * @return
     */
    public Integer updateCouponGoods(CouponGoods couponGoods) {
        if (StringUtils.isEmpty(couponGoods.getCouponGoodsId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = couponGoodsWriteMapper.updateByPrimaryKeySelective(couponGoods);
        if (count == 0) {
            log.error("根据couponGoodsId：" + couponGoods.getCouponGoodsId() + "更新优惠券和产品的绑定表失败");
            throw new MallException("更新优惠券和产品的绑定表失败,请重试");
        }
        return count;
    }

    /**
     * 根据couponGoodsId获取优惠券和产品的绑定表详情
     *
     * @param couponGoodsId couponGoodsId
     * @return
     */
    public CouponGoods getCouponGoodsByCouponGoodsId(Integer couponGoodsId) {
        return couponGoodsReadMapper.getByPrimaryKey(couponGoodsId);
    }

    /**
     * 根据条件获取优惠券和产品的绑定表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<CouponGoods> getCouponGoodsList(CouponGoodsExample example, PagerInfo pager) {
        List<CouponGoods> couponGoodsList;
        if (pager != null) {
            pager.setRowsCount(couponGoodsReadMapper.countByExample(example));
            couponGoodsList = couponGoodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            couponGoodsList = couponGoodsReadMapper.listByExample(example);
        }
        return couponGoodsList;
    }
}