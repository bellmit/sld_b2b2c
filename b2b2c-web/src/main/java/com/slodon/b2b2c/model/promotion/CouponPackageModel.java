package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.CouponPackageReadMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponPackageWriteMapper;
import com.slodon.b2b2c.promotion.example.CouponPackageExample;
import com.slodon.b2b2c.promotion.pojo.CouponPackage;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class CouponPackageModel {
    @Resource
    private CouponPackageReadMapper couponPackageReadMapper;

    @Resource
    private CouponPackageWriteMapper couponPackageWriteMapper;

    /**
     * 新增优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）
     *
     * @param couponPackage
     * @return
     */
    public Integer saveCouponPackage(CouponPackage couponPackage) {
        int count = couponPackageWriteMapper.insert(couponPackage);
        if (count == 0) {
            throw new MallException("添加优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）失败，请重试");
        }
        return count;
    }

    /**
     * 根据packageId删除优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）
     *
     * @param packageId packageId
     * @return
     */
    public Integer deleteCouponPackage(Integer packageId) {
        if (StringUtils.isEmpty(packageId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = couponPackageWriteMapper.deleteByPrimaryKey(packageId);
        if (count == 0) {
            log.error("根据packageId：" + packageId + "删除优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）失败");
            throw new MallException("删除优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）失败,请重试");
        }
        return count;
    }

    /**
     * 根据packageId更新优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）
     *
     * @param couponPackage
     * @return
     */
    public Integer updateCouponPackage(CouponPackage couponPackage) {
        if (StringUtils.isEmpty(couponPackage.getPackageId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = couponPackageWriteMapper.updateByPrimaryKeySelective(couponPackage);
        if (count == 0) {
            log.error("根据packageId：" + couponPackage.getPackageId() + "更新优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）失败");
            throw new MallException("更新优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）失败,请重试");
        }
        return count;
    }

    /**
     * 根据packageId获取优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）详情
     *
     * @param packageId packageId
     * @return
     */
    public CouponPackage getCouponPackageByPackageId(Integer packageId) {
        return couponPackageReadMapper.getByPrimaryKey(packageId);
    }

    /**
     * 根据条件获取优惠券表礼包（注册礼包、生日礼包、活动礼包等，用户自行创建）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<CouponPackage> getCouponPackageList(CouponPackageExample example, PagerInfo pager) {
        List<CouponPackage> couponPackageList;
        if (pager != null) {
            pager.setRowsCount(couponPackageReadMapper.countByExample(example));
            couponPackageList = couponPackageReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            couponPackageList = couponPackageReadMapper.listByExample(example);
        }
        return couponPackageList;
    }
}