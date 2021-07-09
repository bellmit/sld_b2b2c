package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsPictureReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsPictureWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralGoodsPictureExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsPicture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品对应图片表model
 */
@Component
@Slf4j
public class IntegralGoodsPictureModel {
    @Resource
    private IntegralGoodsPictureReadMapper integralGoodsPictureReadMapper;

    @Resource
    private IntegralGoodsPictureWriteMapper integralGoodsPictureWriteMapper;

    /**
     * 新增商品对应图片表
     *
     * @param integralGoodsPicture
     * @return
     */
    public Integer saveIntegralGoodsPicture(IntegralGoodsPicture integralGoodsPicture) {
        int count = integralGoodsPictureWriteMapper.insert(integralGoodsPicture);
        if (count == 0) {
            throw new MallException("添加商品对应图片表失败，请重试");
        }
        return count;
    }

    /**
     * 根据pictureId删除商品对应图片表
     *
     * @param pictureId pictureId
     * @return
     */
    public Integer deleteIntegralGoodsPicture(Integer pictureId) {
        if (StringUtils.isEmpty(pictureId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralGoodsPictureWriteMapper.deleteByPrimaryKey(pictureId);
        if (count == 0) {
            log.error("根据pictureId：" + pictureId + "删除商品对应图片表失败");
            throw new MallException("删除商品对应图片表失败,请重试");
        }
        return count;
    }

    /**
     * 根据pictureId更新商品对应图片表
     *
     * @param integralGoodsPicture
     * @return
     */
    public Integer updateIntegralGoodsPicture(IntegralGoodsPicture integralGoodsPicture) {
        if (StringUtils.isEmpty(integralGoodsPicture.getPictureId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralGoodsPictureWriteMapper.updateByPrimaryKeySelective(integralGoodsPicture);
        if (count == 0) {
            log.error("根据pictureId：" + integralGoodsPicture.getPictureId() + "更新商品对应图片表失败");
            throw new MallException("更新商品对应图片表失败,请重试");
        }
        return count;
    }

    /**
     * 根据pictureId获取商品对应图片表详情
     *
     * @param pictureId pictureId
     * @return
     */
    public IntegralGoodsPicture getIntegralGoodsPictureByPictureId(Integer pictureId) {
        return integralGoodsPictureReadMapper.getByPrimaryKey(pictureId);
    }

    /**
     * 根据条件获取商品对应图片表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralGoodsPicture> getIntegralGoodsPictureList(IntegralGoodsPictureExample example, PagerInfo pager) {
        List<IntegralGoodsPicture> integralGoodsPictureList;
        if (pager != null) {
            pager.setRowsCount(integralGoodsPictureReadMapper.countByExample(example));
            integralGoodsPictureList = integralGoodsPictureReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralGoodsPictureList = integralGoodsPictureReadMapper.listByExample(example);
        }
        return integralGoodsPictureList;
    }
}