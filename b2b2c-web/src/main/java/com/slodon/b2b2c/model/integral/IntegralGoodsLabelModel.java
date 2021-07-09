package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.constant.IntegralConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsBindLabelReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsLabelReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsLabelWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralGoodsBindLabelExample;
import com.slodon.b2b2c.integral.example.IntegralGoodsLabelExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsBindLabel;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 积分商城商品标签model
 */
@Component
@Slf4j
public class IntegralGoodsLabelModel {

    @Resource
    private IntegralGoodsLabelReadMapper integralGoodsLabelReadMapper;
    @Resource
    private IntegralGoodsLabelWriteMapper integralGoodsLabelWriteMapper;
    @Resource
    private IntegralGoodsBindLabelReadMapper integralGoodsBindLabelReadMapper;

    /**
     * 新增积分商城商品标签
     *
     * @param integralGoodsLabel
     * @return
     */
    public Integer saveIntegralGoodsLabel(IntegralGoodsLabel integralGoodsLabel) {
        //标签名称不可重复
        IntegralGoodsLabelExample example = new IntegralGoodsLabelExample();
        example.setLabelName(integralGoodsLabel.getLabelName());
        example.setParentLabelId(integralGoodsLabel.getParentLabelId());
        List<IntegralGoodsLabel> list = integralGoodsLabelReadMapper.listByExample(example);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "标签名称不可重复");

        //查询上级标签
        IntegralGoodsLabel integralGoodsLabelDb = this.getIntegralGoodsLabelByLabelId(integralGoodsLabel.getParentLabelId());
        if (integralGoodsLabelDb == null) {
            integralGoodsLabel.setGrade(IntegralConst.GRADE_1);
        } else {
            AssertUtil.isTrue(integralGoodsLabelDb.getGrade() + 1 > 2, "最多支持二级标签，请重试");
            AssertUtil.notEmpty(integralGoodsLabel.getImage(), "二级标签图片不能为空，请重试");
            integralGoodsLabel.setGrade(IntegralConst.GRADE_2);
        }
        integralGoodsLabel.setState(IntegralConst.STATE_1);
        int count = integralGoodsLabelWriteMapper.insert(integralGoodsLabel);
        if (count == 0) {
            throw new MallException("添加积分商城商品标签失败，请重试");
        }
        return count;
    }

    /**
     * 根据labelId删除积分商城商品标签
     *
     * @param labelId labelId
     * @return
     */
    public Integer deleteIntegralGoodsLabel(Integer labelId) {
        if (StringUtils.isEmpty(labelId)) {
            throw new MallException("请选择要删除的数据");
        }
        //查询标签信息
        IntegralGoodsLabel integralGoodsLabel = integralGoodsLabelReadMapper.getByPrimaryKey(labelId);
        AssertUtil.notNull(integralGoodsLabel, "标签不存在");
        //查询标签是否被使用
        IntegralGoodsBindLabelExample example = new IntegralGoodsBindLabelExample();
        if (integralGoodsLabel.getGrade() == IntegralConst.GRADE_1) {
            example.setLabelId1(labelId);
        } else {
            example.setLabelId2(labelId);
        }
        List<IntegralGoodsBindLabel> list = integralGoodsBindLabelReadMapper.listByExample(example);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "该标签正在使用，不能删除");

        int count = integralGoodsLabelWriteMapper.deleteByPrimaryKey(labelId);
        if (count == 0) {
            log.error("根据labelId：" + labelId + "删除积分商城商品标签失败");
            throw new MallException("删除积分商城商品标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId更新积分商城商品标签
     *
     * @param integralGoodsLabel
     * @return
     */
    public Integer updateIntegralGoodsLabel(IntegralGoodsLabel integralGoodsLabel) {
        if (StringUtils.isEmpty(integralGoodsLabel.getLabelId())) {
            throw new MallException("请选择要修改的数据");
        }
        if (!StringUtil.isEmpty(integralGoodsLabel.getLabelName())) {
            AssertUtil.notNull(integralGoodsLabel.getParentLabelId(), "请选择上级标签");
            //标签名称不可重复
            IntegralGoodsLabelExample example = new IntegralGoodsLabelExample();
            example.setLabelName(integralGoodsLabel.getLabelName());
            example.setParentLabelId(integralGoodsLabel.getParentLabelId());
            example.setLabelIdNotEquals(integralGoodsLabel.getLabelId());
            List<IntegralGoodsLabel> list = integralGoodsLabelReadMapper.listByExample(example);
            AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "标签名称不可重复");
        }
        int count = integralGoodsLabelWriteMapper.updateByPrimaryKeySelective(integralGoodsLabel);
        if (count == 0) {
            log.error("根据labelId：" + integralGoodsLabel.getLabelId() + "更新积分商城商品标签失败");
            throw new MallException("更新积分商城商品标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId获取积分商城商品标签详情
     *
     * @param labelId labelId
     * @return
     */
    public IntegralGoodsLabel getIntegralGoodsLabelByLabelId(Integer labelId) {
        return integralGoodsLabelReadMapper.getByPrimaryKey(labelId);
    }

    /**
     * 根据条件获取积分商城商品标签列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralGoodsLabel> getIntegralGoodsLabelList(IntegralGoodsLabelExample example, PagerInfo pager) {
        List<IntegralGoodsLabel> integralGoodsLabelList;
        if (pager != null) {
            pager.setRowsCount(integralGoodsLabelReadMapper.countByExample(example));
            integralGoodsLabelList = integralGoodsLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralGoodsLabelList = integralGoodsLabelReadMapper.listByExample(example);
        }
        return integralGoodsLabelList;
    }

    /**
     * 根据条件获取积分商城商品标签数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getIntegralGoodsLabelCount(IntegralGoodsLabelExample example) {
        return integralGoodsLabelReadMapper.countByExample(example);
    }
}