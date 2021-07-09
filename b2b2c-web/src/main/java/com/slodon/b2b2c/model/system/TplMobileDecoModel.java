package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.constant.TplPcConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.system.TplMobileDecoReadMapper;
import com.slodon.b2b2c.dao.write.system.TplMobileDecoWriteMapper;
import com.slodon.b2b2c.system.example.TplMobileDecoExample;
import com.slodon.b2b2c.system.pojo.TplMobileDeco;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
@Slf4j
public class TplMobileDecoModel {

    @Resource
    private TplMobileDecoReadMapper tplMobileDecoReadMapper;
    @Resource
    private TplMobileDecoWriteMapper tplMobileDecoWriteMapper;

    /**
     * 新增mobile装修页表（用户装修实例）
     *
     * @param tplMobileDeco
     * @return
     */
    public Integer saveTplMobileDeco(TplMobileDeco tplMobileDeco) {
        //查重
        TplMobileDecoExample example = new TplMobileDecoExample();
        example.setName(tplMobileDeco.getName());
        example.setType(tplMobileDeco.getType());
        example.setStoreId(tplMobileDeco.getStoreId());
        List<TplMobileDeco> list = tplMobileDecoReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("模版名称已存在");
        }
        int count = tplMobileDecoWriteMapper.insert(tplMobileDeco);
        if (count == 0) {
            throw new MallException("添加mobile装修页表（用户装修实例）失败，请重试");
        }
        return count;
    }

    /**
     * 根据decoId删除mobile装修页表（用户装修实例）
     *
     * @param decoId decoId
     * @return
     */
    public Integer deleteTplMobileDeco(Integer decoId) {
        if (StringUtils.isEmpty(decoId)) {
            throw new MallException("请选择要删除的数据");
        }
        TplMobileDeco tplMobileDeco = tplMobileDecoReadMapper.getByPrimaryKey(decoId);
        AssertUtil.notNull(tplMobileDeco, "未获取到模板信息");
        if (tplMobileDeco.getAndroid() == TplPcConst.IS_ENABLE_YES || tplMobileDeco.getIos() == TplPcConst.IS_ENABLE_YES
                || tplMobileDeco.getH5() == TplPcConst.IS_ENABLE_YES || tplMobileDeco.getWeixinXcx() == TplPcConst.IS_ENABLE_YES
                || tplMobileDeco.getAlipayXcx() == TplPcConst.IS_ENABLE_YES) {
            throw new MallException("模板正在使用，不能删除");
        }
        int count = tplMobileDecoWriteMapper.deleteByPrimaryKey(decoId);
        if (count == 0) {
            log.error("根据decoId：" + decoId + "删除mobile装修页表（用户装修实例）失败");
            throw new MallException("删除mobile装修页表（用户装修实例）失败,请重试");
        }
        return count;
    }

    /**
     * 根据decoId更新mobile装修页表（用户装修实例）
     *
     * @param tplMobileDeco
     * @return
     */
    public Integer updateTplMobileDeco(TplMobileDeco tplMobileDeco) {
        if (StringUtils.isEmpty(tplMobileDeco.getDecoId())) {
            throw new MallException("请选择要修改的数据");
        }
        if (!StringUtil.isEmpty(tplMobileDeco.getName())) {
            //查重
            TplMobileDecoExample example = new TplMobileDecoExample();
            example.setDecoIdNotEquals(tplMobileDeco.getDecoId());
            example.setName(tplMobileDeco.getName());
            example.setType(tplMobileDeco.getType());
            List<TplMobileDeco> list = tplMobileDecoReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(list)) {
                throw new MallException("模版名称已存在");
            }
        }
        int count = tplMobileDecoWriteMapper.updateByPrimaryKeySelective(tplMobileDeco);
        if (count == 0) {
            log.error("根据decoId：" + tplMobileDeco.getDecoId() + "更新mobile装修页表（用户装修实例）失败");
            throw new MallException("更新mobile装修页表（用户装修实例）失败,请重试");
        }
        return count;
    }

    /**
     * 根据decoId获取mobile装修页表（用户装修实例）详情
     *
     * @param decoId decoId
     * @return
     */
    public TplMobileDeco getTplMobileDecoByDecoId(Integer decoId) {
        return tplMobileDecoReadMapper.getByPrimaryKey(decoId);
    }

    /**
     * 根据条件获取mobile装修页表（用户装修实例）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<TplMobileDeco> getTplMobileDecoList(TplMobileDecoExample example, PagerInfo pager) {
        List<TplMobileDeco> tplMobileDecoList;
        if (pager != null) {
            pager.setRowsCount(tplMobileDecoReadMapper.countByExample(example));
            tplMobileDecoList = tplMobileDecoReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            tplMobileDecoList = tplMobileDecoReadMapper.listByExample(example);
        }
        return tplMobileDecoList;
    }

    /**
     * 启用mobile端装修页
     */
    public Integer enable(Integer decoId, Long storeId, String os,String type) throws InvocationTargetException, IllegalAccessException {
        //判断是否已经有启用的同类型的装修页
        TplMobileDecoExample tplMobileDecoExample = new TplMobileDecoExample();
        tplMobileDecoExample.setDecoIdNotEquals(decoId);
        if (!StringUtil.isEmpty(os)) {
            switch (os) {
                case "android":
                    tplMobileDecoExample.setAndroid(TplPcConst.IS_ENABLE_YES);
                    break;
                case "ios":
                    tplMobileDecoExample.setIos(TplPcConst.IS_ENABLE_YES);
                    break;
                case "h5":
                    tplMobileDecoExample.setH5(TplPcConst.IS_ENABLE_YES);
                    break;
                case "weixinXcx":
                    tplMobileDecoExample.setWeixinXcx(TplPcConst.IS_ENABLE_YES);
                    break;
                default:
                    break;
            }
        }
        List<TplMobileDeco> list;
        if (storeId == 0L) {
            tplMobileDecoExample.setType(type);
            tplMobileDecoExample.setStoreId(0L);
            list = tplMobileDecoReadMapper.listByExample(tplMobileDecoExample);
        } else {
//            tplMobileDecoExample.setType("seller");
            tplMobileDecoExample.setType(type);
            tplMobileDecoExample.setStoreId(storeId);
            list = tplMobileDecoReadMapper.listByExample(tplMobileDecoExample);
        }
        if (!CollectionUtils.isEmpty(list)) {
            TplMobileDeco disableDeco = new TplMobileDeco();
            BeanUtils.setProperty(disableDeco, os, TplPcConst.IS_ENABLE_NO);
            tplMobileDecoWriteMapper.updateByExampleSelective(disableDeco, tplMobileDecoExample);
        }
        //修改启用状态
        TplMobileDeco tplMobileDeco = new TplMobileDeco();
        tplMobileDeco.setDecoId(decoId);
        BeanUtils.setProperty(tplMobileDeco, os, TplPcConst.IS_ENABLE_YES);
        return tplMobileDecoWriteMapper.updateByPrimaryKeySelective(tplMobileDeco);
    }

    /**
     * 停用mobile端装修页
     */
    public Integer disable(Integer decoId, Long storeId, String os,String type) throws InvocationTargetException, IllegalAccessException {
//        TplMobileDecoExample example = new TplMobileDecoExample();
//        if (storeId == 0L) {
//            example.setStoreId(0L);
//            example.setType("home");
//        } else {
//            example.setStoreId(storeId);
//            example.setType("seller");
//        }
//        example.setDecoIdNotEquals(decoId);
//        BeanUtils.setProperty(example, os, TplPcConst.IS_ENABLE_YES);
//        List<TplMobileDeco> list = tplMobileDecoReadMapper.listByExample(example);
//        AssertUtil.isTrue(CollectionUtils.isEmpty(list), "当前模板为默认模板请先设置其他模板为默认模板");

        TplMobileDeco tplMobileDeco = new TplMobileDeco();
        tplMobileDeco.setDecoId(decoId);
        BeanUtils.setProperty(tplMobileDeco, os, TplPcConst.IS_ENABLE_NO);
        return tplMobileDecoWriteMapper.updateByPrimaryKeySelective(tplMobileDeco);
    }
}