package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsSpecReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsSpecWriteMapper;
import com.slodon.b2b2c.goods.dto.GoodsSpecAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsSpecUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsSpecExample;
import com.slodon.b2b2c.goods.pojo.GoodsSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class GoodsSpecModel {

    @Resource
    private GoodsSpecReadMapper goodsSpecReadMapper;
    @Resource
    private GoodsSpecWriteMapper goodsSpecWriteMapper;
    @Resource
    private GoodsSpecValueModel goodsSpecValueModel;

    /**
     * 新增规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）
     *
     * @param goodsSpec
     * @return
     */
    public Integer saveGoodsSpec(GoodsSpec goodsSpec) {
        int count = goodsSpecWriteMapper.insert(goodsSpec);
        if (count == 0) {
            throw new MallException("添加规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）失败，请重试");
        }
        return count;
    }

    /**
     * 新增规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）
     *
     * @param storeId
     * @param creatId
     * @param creatName
     * @param goodsSpecAddDTO
     * @return
     */
    @Transactional
    public Integer saveGoodsSpec(Long storeId, Integer creatId, String creatName, GoodsSpecAddDTO goodsSpecAddDTO) {

        //判断属性名称是否重复
        GoodsSpecExample goodsSpecExample = new GoodsSpecExample();
        goodsSpecExample.setStoreId(storeId);
        goodsSpecExample.setSpecName(goodsSpecAddDTO.getSpecName());
        List<GoodsSpec> goodsSpecs = goodsSpecReadMapper.listByExample(goodsSpecExample);
        if (!CollectionUtils.isEmpty(goodsSpecs)) {
            throw new MallException("规则名称已存在，请重新填写");
        }
        //插入属性表
        GoodsSpec goodsSpecInsert = new GoodsSpec();
        goodsSpecInsert.setSpecName(goodsSpecAddDTO.getSpecName());
        goodsSpecInsert.setSpecType(GoodsConst.SPEC_TYPE_1);
        goodsSpecInsert.setStoreId(storeId); //0为系统创建
        goodsSpecInsert.setCreateId(creatId);
        goodsSpecInsert.setCreateName(creatName);
        goodsSpecInsert.setCreateTime(new Date());
        goodsSpecInsert.setUpdateId(creatId);
        goodsSpecInsert.setUpdateName(creatName);
        goodsSpecInsert.setUpdateTime(new Date());
        goodsSpecInsert.setState(goodsSpecAddDTO.getState());
        goodsSpecInsert.setSort(goodsSpecAddDTO.getSort());

        int count;
        count = goodsSpecWriteMapper.insert(goodsSpecInsert);
        if (count == 0) {
            throw new MallException("添加规格表失败，请重试");
        }
        return goodsSpecInsert.getSpecId();
    }

    /**
     * 新增规格表和规格值表（系统维护，平台可以看到店铺创建的规格，但不做修改）
     *
     * @param storeId
     * @param creatId
     * @param creatName
     * @param goodsSpecAddDTO
     * @return
     */
    @Transactional
    public Integer saveGoodsSpecAndValue(Long storeId, Integer creatId, String creatName, GoodsSpecAddDTO goodsSpecAddDTO) {
        Integer specId = saveGoodsSpec(storeId, creatId, creatName, goodsSpecAddDTO);
        if (!StringUtil.isEmpty(goodsSpecAddDTO.getSpecValues())) {
            goodsSpecValueModel.saveGoodsSpecValue(storeId, creatId, specId, goodsSpecAddDTO.getSpecValues());
        }
        return specId;
    }

    /**
     * 根据specId删除规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）
     *
     * @param specId specId
     * @return
     */
    @Transactional
    public Integer deleteGoodsSpec(Integer specId) {
        int count = goodsSpecWriteMapper.deleteByPrimaryKey(specId);
        if (count == 0) {
            log.error("根据specId：" + specId + "删除规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）失败");
            throw new MallException("删除规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）失败,请重试");
        }
        return count;
    }


    /**
     * 根据specId删除规格表 和规格值表（系统维护，平台可以看到店铺创建的规格，但不做修改）
     *
     * @param specId specId
     * @return
     */
    @Transactional
    public JsonResult deleteSpecAndValue(Integer specId) {
        goodsSpecValueModel.deleteGoodsSpecValueBySpecID(specId);
        deleteGoodsSpec(specId);
        return SldResponse.success("删除规格成功");
    }

    /**
     * 根据specId更新规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）
     *
     * @param goodsSpec
     * @return
     */
    public Integer updateGoodsSpec(GoodsSpec goodsSpec) {
        if (StringUtils.isEmpty(goodsSpec.getSpecId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsSpecWriteMapper.updateByPrimaryKeySelective(goodsSpec);
        if (count == 0) {
            log.error("根据specId：" + goodsSpec.getSpecId() + "更新规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）失败");
            throw new MallException("更新规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）失败,请重试");
        }
        return count;
    }

    /**
     * 根据specId更新规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）
     *
     * @param goodsSpecUpdateDTO
     * @return
     */
    @Transactional
    public Integer updateGoodsSpec(Integer updateId, String updateName, GoodsSpecUpdateDTO goodsSpecUpdateDTO) {
        //判断规格名称是否重复
        GoodsSpecExample goodsSpecExample = new GoodsSpecExample();
        goodsSpecExample.setSpecName(goodsSpecUpdateDTO.getSpecName());
        List<GoodsSpec> goodsSpecs = goodsSpecReadMapper.listByExample(goodsSpecExample);
        if (!CollectionUtils.isEmpty(goodsSpecs)) {
            if (goodsSpecs.size() > 0) {
                if (goodsSpecs.get(0).getSpecId() != goodsSpecUpdateDTO.getSpecId()) {
                    throw new MallException("规格名称已存在，请重新填写");
                }
            }
        }
        //修改属性表
        GoodsSpec goodsSpecUpdate = new GoodsSpec();
        goodsSpecUpdate.setSpecId(goodsSpecUpdateDTO.getSpecId());
        goodsSpecUpdate.setSpecName(goodsSpecUpdateDTO.getSpecName());
        goodsSpecUpdate.setSort(goodsSpecUpdateDTO.getSort());
        goodsSpecUpdate.setState(goodsSpecUpdateDTO.getState());
        goodsSpecUpdate.setUpdateId(updateId);
        goodsSpecUpdate.setUpdateName(updateName);

        int count;
        count = goodsSpecWriteMapper.updateByPrimaryKeySelective(goodsSpecUpdate);
        if (count == 0) {
            log.error("根据属性ID：" + goodsSpecUpdate.getSpecId() + "更新属性表失败");
            throw new MallException("更新属性表失败,请重试");
        }
        return count;
    }

    /**
     * 编辑规格表和规格值表（系统维护，平台可以看到店铺创建的规格，但不做修改）
     *
     * @param storeId
     * @param updateId
     * @param updateName
     * @param goodsSpecUpdateDTO
     * @return
     */
    @Transactional
    public void updateGoodsSpecAndValue(Long storeId, Integer updateId, String updateName, GoodsSpecUpdateDTO goodsSpecUpdateDTO) {
        updateGoodsSpec(updateId, updateName, goodsSpecUpdateDTO);
        goodsSpecValueModel.deleteGoodsSpecValueBySpecID(goodsSpecUpdateDTO.getSpecId());
        goodsSpecValueModel.saveGoodsSpecValue(storeId, updateId, goodsSpecUpdateDTO.getSpecId(), goodsSpecUpdateDTO.getSpecValues());
    }

    /**
     * 根据specId获取规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）详情
     *
     * @param specId specId
     * @return
     */
    public GoodsSpec getGoodsSpecBySpecId(Integer specId) {
        return goodsSpecReadMapper.getByPrimaryKey(specId);
    }

    /**
     * 根据条件获取规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsSpec> getGoodsSpecList(GoodsSpecExample example, PagerInfo pager) {
        List<GoodsSpec> goodsSpecList;
        if (pager != null) {
            pager.setRowsCount(goodsSpecReadMapper.countByExample(example));
            goodsSpecList = goodsSpecReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsSpecList = goodsSpecReadMapper.listByExample(example);
        }
        return goodsSpecList;
    }
}