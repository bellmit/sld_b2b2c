package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.DiscountMbDecoReadMapper;
import com.slodon.b2b2c.dao.write.promotion.DiscountMbDecoWriteMapper;
import com.slodon.b2b2c.promotion.example.DiscountMbDecoExample;
import com.slodon.b2b2c.promotion.pojo.DiscountMbDeco;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DiscountMbDecoModel {
    @Resource
    private DiscountMbDecoReadMapper discountMbDecoReadMapper;

    @Resource
    private DiscountMbDecoWriteMapper discountMbDecoWriteMapper;

    /**
     * 新增折扣活动mobile装修数据
     *
     * @param discountMbDeco
     * @return
     */
    public Integer saveDiscountMbDeco(DiscountMbDeco discountMbDeco) {
        int count = discountMbDecoWriteMapper.insert(discountMbDeco);
        if (count == 0) {
            throw new MallException("添加折扣活动mobile装修数据失败，请重试");
        }
        return count;
    }

    /**
     * 根据id删除折扣活动mobile装修数据
     *
     * @param id id
     * @return
     */
    public Integer deleteDiscountMbDeco(Integer id) {
        if (StringUtils.isEmpty(id)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = discountMbDecoWriteMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            log.error("根据id：" + id + "删除折扣活动mobile装修数据失败");
            throw new MallException("删除折扣活动mobile装修数据失败,请重试");
        }
        return count;
    }

    /**
     * 根据id更新折扣活动mobile装修数据
     *
     * @param discountMbDeco
     * @return
     */
    public Integer updateDiscountMbDeco(DiscountMbDeco discountMbDeco) {
        if (StringUtils.isEmpty(discountMbDeco.getId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = discountMbDecoWriteMapper.updateByPrimaryKeySelective(discountMbDeco);
        if (count == 0) {
            log.error("根据id：" + discountMbDeco.getId() + "更新折扣活动mobile装修数据失败");
            throw new MallException("更新折扣活动mobile装修数据失败,请重试");
        }
        return count;
    }

    /**
     * 根据id获取折扣活动mobile装修数据详情
     *
     * @param id id
     * @return
     */
    public DiscountMbDeco getDiscountMbDecoById(Integer id) {
        return discountMbDecoReadMapper.getByPrimaryKey(id);
    }

    /**
     * 根据条件获取折扣活动mobile装修数据列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<DiscountMbDeco> getDiscountMbDecoList(DiscountMbDecoExample example, PagerInfo pager) {
        List<DiscountMbDeco> discountMbDecoList;
        if (pager != null) {
            pager.setRowsCount(discountMbDecoReadMapper.countByExample(example));
            discountMbDecoList = discountMbDecoReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            discountMbDecoList = discountMbDecoReadMapper.listByExample(example);
        }
        return discountMbDecoList;
    }
}