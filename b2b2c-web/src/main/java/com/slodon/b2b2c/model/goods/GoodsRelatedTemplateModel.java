package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.goods.GoodsRelatedTemplateReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsRelatedTemplateWriteMapper;
import com.slodon.b2b2c.goods.dto.GoodsRelatedTemplateAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsRelatedTemplateUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsRelatedTemplateExample;
import com.slodon.b2b2c.goods.pojo.GoodsRelatedTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsRelatedTemplateModel {
    @Resource
    private GoodsRelatedTemplateReadMapper goodsRelatedTemplateReadMapper;

    @Resource
    private GoodsRelatedTemplateWriteMapper goodsRelatedTemplateWriteMapper;

    /**
     * 新增商品的店铺关联模版
     *
     * @param goodsRelatedTemplate
     * @return
     */
    public Integer saveGoodsRelatedTemplate(GoodsRelatedTemplate goodsRelatedTemplate) {
        int count = goodsRelatedTemplateWriteMapper.insert(goodsRelatedTemplate);
        if (count == 0) {
            throw new MallException("添加商品的店铺关联模版失败，请重试");
        }
        return count;
    }

    /**
     * 新增商品的店铺关联模版
     *
     * @param goodsRelatedTemplateAddDTO,vendor
     * @return
     */
    @Transactional
    public Integer saveGoodsRelatedTemplate(GoodsRelatedTemplateAddDTO goodsRelatedTemplateAddDTO, Long storeId) throws Exception{

        //新增
        GoodsRelatedTemplate insertOne = new GoodsRelatedTemplate();
        PropertyUtils.copyProperties(insertOne, goodsRelatedTemplateAddDTO);
        insertOne.setStoreId(storeId);
//        insertOne.setTemplatePosition(GoodsConst.TEMPLATE_CONTENT_1);
        insertOne.setCreateVendorId(storeId);

        int count = goodsRelatedTemplateWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加商品的店铺关联模版失败，请重试");
        }
        return count;
    }

    /**
     * 根据templateId删除商品的店铺关联模版
     *
     * @param templateId templateId
     * @return
     */
    public Integer deleteGoodsRelatedTemplate(Integer templateId) {
        if (StringUtils.isEmpty(templateId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsRelatedTemplateWriteMapper.deleteByPrimaryKey(templateId);
        if (count == 0) {
            log.error("根据templateId：" + templateId + "删除商品的店铺关联模版失败");
            throw new MallException("删除商品的店铺关联模版失败,请重试");
        }
        return count;
    }

    /**
     * 根据templateIds删除商品的店铺关联模版
     *
     * @param templateIds
     * @return
     */
    @Transactional
    public Integer deleteGoodsRelatedTemplate(String templateIds,Long storeId) {
        //组装要删除的条件
        GoodsRelatedTemplateExample example = new GoodsRelatedTemplateExample();
        example.setStoreId(storeId);
        example.setTemplateIdIn(templateIds);
        int count = goodsRelatedTemplateWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据templateIds：" + templateIds + "删除商品的店铺关联模版失败");
            throw new MallException("删除商品的店铺关联模版失败,请重试");
        }
        return count;
    }

    /**
     * 根据templateId更新商品的店铺关联模版
     *
     * @param goodsRelatedTemplate
     * @return
     */
    public Integer updateGoodsRelatedTemplate(GoodsRelatedTemplate goodsRelatedTemplate) {
        if (StringUtils.isEmpty(goodsRelatedTemplate.getTemplateId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsRelatedTemplateWriteMapper.updateByPrimaryKeySelective(goodsRelatedTemplate);
        if (count == 0) {
            log.error("根据templateId：" + goodsRelatedTemplate.getTemplateId() + "更新商品的店铺关联模版失败");
            throw new MallException("更新商品的店铺关联模版失败,请重试");
        }
        return count;
    }

    /**
     * 根据goodsRelatedTemplateUpdateDTO更新商品的店铺关联模版
     *
     * @param goodsRelatedTemplateUpdateDTO
     * @return
     */
    @Transactional
    public Integer updateGoodsRelatedTemplate(GoodsRelatedTemplateUpdateDTO goodsRelatedTemplateUpdateDTO,Long storeId) throws Exception{

        //查重
        GoodsRelatedTemplateExample example = new GoodsRelatedTemplateExample();
        example.setStoreId(storeId);
        example.setTemplateName(goodsRelatedTemplateUpdateDTO.getTemplateName());
        example.setTemplateIdNotEquals(goodsRelatedTemplateUpdateDTO.getTemplateId());
        List<GoodsRelatedTemplate> list = goodsRelatedTemplateReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("关联模板名称重复,请重新填写");
        }

        //修改
        GoodsRelatedTemplate updateOne = new GoodsRelatedTemplate();
        PropertyUtils.copyProperties(updateOne, goodsRelatedTemplateUpdateDTO);

        GoodsRelatedTemplateExample goodsRelatedTemplateExample = new GoodsRelatedTemplateExample();
        goodsRelatedTemplateExample.setStoreId(storeId);
        goodsRelatedTemplateExample.setTemplateIdIn(goodsRelatedTemplateUpdateDTO.getTemplateId()+"");

        int count = goodsRelatedTemplateWriteMapper.updateByExampleSelective(updateOne,goodsRelatedTemplateExample);
        if (count == 0) {
            log.error("根据templateId：" + goodsRelatedTemplateUpdateDTO.getTemplateId() + "更新商品的店铺关联模版失败");
            throw new MallException("更新商品的店铺关联模版失败,请重试");
        }
        return count;
    }

    /**
     * 根据templateId获取商品的店铺关联模版详情
     *
     * @param templateId templateId
     * @return
     */
    public GoodsRelatedTemplate getGoodsRelatedTemplateByTemplateId(Integer templateId) {
        return goodsRelatedTemplateReadMapper.getByPrimaryKey(templateId);
    }

    /**
     * 根据条件获取商品的店铺关联模版列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsRelatedTemplate> getGoodsRelatedTemplateList(GoodsRelatedTemplateExample example, PagerInfo pager) {
        List<GoodsRelatedTemplate> goodsRelatedTemplateList;
        if (pager != null) {
            pager.setRowsCount(goodsRelatedTemplateReadMapper.countByExample(example));
            goodsRelatedTemplateList = goodsRelatedTemplateReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsRelatedTemplateList = goodsRelatedTemplateReadMapper.listByExample(example);
        }
        return goodsRelatedTemplateList;
    }


}