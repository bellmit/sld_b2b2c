package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.constant.StoreCateConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.StoreInnerLabelReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreInnerLabelWriteMapper;
import com.slodon.b2b2c.dao.write.seller.StoreLabelBindGoodsWriteMapper;
import com.slodon.b2b2c.seller.example.StoreInnerLabelExample;
import com.slodon.b2b2c.seller.example.StoreLabelBindGoodsExample;
import com.slodon.b2b2c.seller.pojo.StoreInnerLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StoreInnerLabelModel {

    @Resource
    private StoreInnerLabelReadMapper storeInnerLabelReadMapper;
    @Resource
    private StoreInnerLabelWriteMapper storeInnerLabelWriteMapper;

    @Resource
    private StoreLabelBindGoodsWriteMapper storeLabelBindGoodsWriteMapper;

    /**
     * 新增店铺内商品标签
     *
     * @param storeInnerLabel
     * @return
     */
    public Integer saveStoreInnerLabel(StoreInnerLabel storeInnerLabel) {

        if (storeInnerLabel.getParentInnerLabelId() != 0) {
            StoreInnerLabel storeInnerLabelDb = storeInnerLabelReadMapper.getByPrimaryKey(storeInnerLabel.getParentInnerLabelId());
            if (storeInnerLabelDb.getParentInnerLabelId() != 0) {
                throw new MallException("不能存三级分类");
            }
        }
        //判断分类名称是否重复
        StoreInnerLabelExample storeInnerLabelExample = new StoreInnerLabelExample();
        storeInnerLabelExample.setInnerLabelName(storeInnerLabel.getInnerLabelName());
        storeInnerLabelExample.setStoreId(storeInnerLabel.getStoreId());
        List<StoreInnerLabel> storeInnerLabels = storeInnerLabelReadMapper.listByExample(storeInnerLabelExample);
        if (!CollectionUtils.isEmpty(storeInnerLabels)) {
            throw new MallException("店铺分类名称重复，请重新填写");
        }
        storeInnerLabel.setCreateTime(new Date());
        int count = storeInnerLabelWriteMapper.insert(storeInnerLabel);
        if (count == 0) {
            throw new MallException("添加店铺内商品标签失败，请重试");
        }
        return count;
    }

    /**
     * 根据innerLabelId删除店铺内商品标签
     *
     * @param innerLabelId innerLabelId
     * @return
     */
    @Transactional
    public Integer deleteStoreInnerLabel(Integer innerLabelId) {

        //删除店铺内部分类绑定商品表
        StoreLabelBindGoodsExample example = new StoreLabelBindGoodsExample();
        example.setInnerLabelId(innerLabelId);
        storeLabelBindGoodsWriteMapper.deleteByExample(example);
        int count = storeInnerLabelWriteMapper.deleteByPrimaryKey(innerLabelId);
        if (count == 0) {
            log.error("根据innerLabelId：" + innerLabelId + "删除店铺内商品标签失败");
            throw new MallException("删除店铺内商品标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据innerLabelId更新店铺内商品标签
     *
     * @param storeInnerLabel
     * @return
     */
    @Transactional
    public Integer editStoreCategory(StoreInnerLabel storeInnerLabel) {
        //判断分类名称是否重复
        StoreInnerLabelExample storeInnerLabelExample = new StoreInnerLabelExample();
        storeInnerLabelExample.setInnerLabelName(storeInnerLabel.getInnerLabelName());
        storeInnerLabelExample.setInnerLabelIdNotEquals(storeInnerLabel.getInnerLabelId());
        List<StoreInnerLabel> storeInnerLabels = storeInnerLabelReadMapper.listByExample(storeInnerLabelExample);
        if (!CollectionUtils.isEmpty(storeInnerLabels)) {
            throw new MallException("店铺分类名称重复，请重新填写");
        }
        //关闭时，把子分类也关闭
        if (storeInnerLabel.getIsShow() == StoreCateConst.STORE_LABEL_NOT_SHOW) {
            StoreInnerLabel storeInnerLabelChild = new StoreInnerLabel();
            storeInnerLabelChild.setIsShow(StoreCateConst.STORE_LABEL_NOT_SHOW);
            storeInnerLabelChild.setUpdateTime(new Date());
            storeInnerLabelChild.setUpdateVendorId(storeInnerLabel.getUpdateVendorId());
            storeInnerLabelChild.setUpdateVendorName(storeInnerLabel.getUpdateVendorName());
            StoreInnerLabelExample storeInnerLabelChildExample = new StoreInnerLabelExample();
            storeInnerLabelChildExample.setParentInnerLabelId(storeInnerLabel.getInnerLabelId());
            storeInnerLabelWriteMapper.updateByExampleSelective(storeInnerLabelChild, storeInnerLabelChildExample);
        }
        storeInnerLabel.setUpdateTime(new Date());
        int count = storeInnerLabelWriteMapper.updateByPrimaryKeySelective(storeInnerLabel);
        if (count == 0) {
            log.error("根据innerLabelId：" + storeInnerLabel.getInnerLabelId() + "更新店铺内商品标签失败");
            throw new MallException("更新店铺内商品标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据innerLabelId更新店铺内商品标签
     *
     * @param storeInnerLabel
     * @return
     */
    @Transactional
    public Integer updateStoreInnerLabel(StoreInnerLabel storeInnerLabel) {
        //关闭时，把子分类也关闭
        if (storeInnerLabel.getIsShow() == StoreCateConst.STORE_LABEL_NOT_SHOW) {
            StoreInnerLabel storeInnerLabelChild = new StoreInnerLabel();
            storeInnerLabelChild.setIsShow(StoreCateConst.STORE_LABEL_NOT_SHOW);
            storeInnerLabelChild.setUpdateTime(new Date());
            storeInnerLabelChild.setUpdateVendorId(storeInnerLabel.getUpdateVendorId());
            storeInnerLabelChild.setUpdateVendorName(storeInnerLabel.getUpdateVendorName());
            StoreInnerLabelExample storeInnerLabelExample = new StoreInnerLabelExample();
            storeInnerLabelExample.setParentInnerLabelId(storeInnerLabel.getInnerLabelId());
            storeInnerLabelWriteMapper.updateByExampleSelective(storeInnerLabelChild, storeInnerLabelExample);
        }
        int count = storeInnerLabelWriteMapper.updateByPrimaryKeySelective(storeInnerLabel);
        if (count == 0) {
            log.error("根据innerLabelId：" + storeInnerLabel.getInnerLabelId() + "更新店铺内商品标签失败");
            throw new MallException("更新店铺内商品标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据innerLabelId获取店铺内商品标签详情
     *
     * @param innerLabelId innerLabelId
     * @return
     */
    public StoreInnerLabel getStoreInnerLabelByInnerLabelId(Integer innerLabelId) {
        return storeInnerLabelReadMapper.getByPrimaryKey(innerLabelId);
    }

    /**
     * 根据条件获取店铺内商品标签列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreInnerLabel> getStoreInnerLabelList(StoreInnerLabelExample example, PagerInfo pager) {
        List<StoreInnerLabel> storeInnerLabelList;
        if (pager != null) {
            pager.setRowsCount(storeInnerLabelReadMapper.countByExample(example));
            storeInnerLabelList = storeInnerLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeInnerLabelList = storeInnerLabelReadMapper.listByExample(example);
        }
        return storeInnerLabelList;
    }
}