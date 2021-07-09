package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.DiscountReadMapper;
import com.slodon.b2b2c.dao.write.promotion.DiscountWriteMapper;
import com.slodon.b2b2c.promotion.example.DiscountExample;
import com.slodon.b2b2c.promotion.pojo.Discount;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DiscountModel {
    @Resource
    private DiscountReadMapper discountReadMapper;

    @Resource
    private DiscountWriteMapper discountWriteMapper;

    /**
     * 新增折扣活动基础信息表
     *
     * @param discount
     * @return
     */
    public Integer saveDiscount(Discount discount) {
        int count = discountWriteMapper.insert(discount);
        if (count == 0) {
            throw new MallException("添加折扣活动基础信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据discountId删除折扣活动基础信息表
     *
     * @param discountId discountId
     * @return
     */
    public Integer deleteDiscount(Integer discountId) {
        if (StringUtils.isEmpty(discountId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = discountWriteMapper.deleteByPrimaryKey(discountId);
        if (count == 0) {
            log.error("根据discountId：" + discountId + "删除折扣活动基础信息表失败");
            throw new MallException("删除折扣活动基础信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据discountId更新折扣活动基础信息表
     *
     * @param discount
     * @return
     */
    public Integer updateDiscount(Discount discount) {
        if (StringUtils.isEmpty(discount.getDiscountId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = discountWriteMapper.updateByPrimaryKeySelective(discount);
        if (count == 0) {
            log.error("根据discountId：" + discount.getDiscountId() + "更新折扣活动基础信息表失败");
            throw new MallException("更新折扣活动基础信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据discountId获取折扣活动基础信息表详情
     *
     * @param discountId discountId
     * @return
     */
    public Discount getDiscountByDiscountId(Integer discountId) {
        return discountReadMapper.getByPrimaryKey(discountId);
    }

    /**
     * 根据条件获取折扣活动基础信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Discount> getDiscountList(DiscountExample example, PagerInfo pager) {
        List<Discount> discountList;
        if (pager != null) {
            pager.setRowsCount(discountReadMapper.countByExample(example));
            discountList = discountReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            discountList = discountReadMapper.listByExample(example);
        }
        return discountList;
    }
}