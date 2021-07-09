package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsPictureReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsPictureWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsPictureExample;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.pojo.GoodsPicture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsPictureModel {
    @Resource
    private GoodsPictureReadMapper goodsPictureReadMapper;

    @Resource
    private GoodsPictureWriteMapper goodsPictureWriteMapper;

    /**
     * 新增商品对应图片表
     *
     * @param goodsPicture
     * @return
     */
    public Integer saveGoodsPicture(GoodsPicture goodsPicture) {
        int count = goodsPictureWriteMapper.insert(goodsPicture);
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
    public Integer deleteGoodsPicture(Integer pictureId) {
        if (StringUtils.isEmpty(pictureId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsPictureWriteMapper.deleteByPrimaryKey(pictureId);
        if (count == 0) {
            log.error("根据pictureId：" + pictureId + "删除商品对应图片表失败");
            throw new MallException("删除商品对应图片表失败,请重试");
        }
        return count;
    }

    /**
     * 根据goodsId删除商品对应图片表
     *
     * @param goodsId
     * @return
     */
    public void deleteGoodsPictureGoodsId(Long goodsId) {
        if (StringUtils.isEmpty(goodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        GoodsPictureExample goodsPictureExample=new GoodsPictureExample();
        goodsPictureExample.setGoodsId(goodsId);
        int count =goodsPictureWriteMapper.deleteByExample(goodsPictureExample);
        if (count == 0) {
            log.error("根据goodsId：" + goodsId + "删除商品对应图片表失败");
            throw new MallException("删除商品对应图片表失败,请重试");
        }
    }

    /**
     * 根据pictureId更新商品对应图片表
     *
     * @param goodsPicture
     * @return
     */
    public Integer updateGoodsPicture(GoodsPicture goodsPicture) {
        if (StringUtils.isEmpty(goodsPicture.getPictureId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsPictureWriteMapper.updateByPrimaryKeySelective(goodsPicture);
        if (count == 0) {
            log.error("根据pictureId：" + goodsPicture.getPictureId() + "更新商品对应图片表失败");
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
    public GoodsPicture getGoodsPictureByPictureId(Integer pictureId) {
        return goodsPictureReadMapper.getByPrimaryKey(pictureId);
    }

    /**
     * 根据条件获取商品对应图片表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsPicture> getGoodsPictureList(GoodsPictureExample example, PagerInfo pager) {
        List<GoodsPicture> goodsPictureList;
        if (pager != null) {
            pager.setRowsCount(goodsPictureReadMapper.countByExample(example));
            goodsPictureList = goodsPictureReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsPictureList = goodsPictureReadMapper.listByExample(example);
        }
        return goodsPictureList;
    }


}