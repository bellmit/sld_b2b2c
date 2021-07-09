package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.CouponUseLogReadMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponUseLogWriteMapper;
import com.slodon.b2b2c.promotion.example.CouponUseLogExample;
import com.slodon.b2b2c.promotion.pojo.CouponUseLog;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class CouponUseLogModel {
    @Resource
    private CouponUseLogReadMapper couponUseLogReadMapper;

    @Resource
    private CouponUseLogWriteMapper couponUseLogWriteMapper;

    /**
     * 新增优惠券详细日志表
     *
     * @param couponUseLog
     * @return
     */
    public Integer saveCouponUseLog(CouponUseLog couponUseLog) {
        int count = couponUseLogWriteMapper.insert(couponUseLog);
        if (count == 0) {
            throw new MallException("添加优惠券详细日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除优惠券详细日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteCouponUseLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = couponUseLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除优惠券详细日志表失败");
            throw new MallException("删除优惠券详细日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新优惠券详细日志表
     *
     * @param couponUseLog
     * @return
     */
    public Integer updateCouponUseLog(CouponUseLog couponUseLog) {
        if (StringUtils.isEmpty(couponUseLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = couponUseLogWriteMapper.updateByPrimaryKeySelective(couponUseLog);
        if (count == 0) {
            log.error("根据logId：" + couponUseLog.getLogId() + "更新优惠券详细日志表失败");
            throw new MallException("更新优惠券详细日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取优惠券详细日志表详情
     *
     * @param logId logId
     * @return
     */
    public CouponUseLog getCouponUseLogByLogId(Integer logId) {
        return couponUseLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取优惠券详细日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<CouponUseLog> getCouponUseLogList(CouponUseLogExample example, PagerInfo pager) {
        List<CouponUseLog> couponUseLogList;
        if (pager != null) {
            pager.setRowsCount(couponUseLogReadMapper.countByExample(example));
            couponUseLogList = couponUseLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            couponUseLogList = couponUseLogReadMapper.listByExample(example);
        }
        return couponUseLogList;
    }
}