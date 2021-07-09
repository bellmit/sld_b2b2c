package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.constant.TplPcConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.system.TplPcMallDataReadMapper;
import com.slodon.b2b2c.dao.read.system.TplPcMallDecoReadMapper;
import com.slodon.b2b2c.dao.write.system.TplPcMallDecoWriteMapper;
import com.slodon.b2b2c.system.example.TplPcMallDataExample;
import com.slodon.b2b2c.system.example.TplPcMallDecoExample;
import com.slodon.b2b2c.system.pojo.TplPcMallData;
import com.slodon.b2b2c.system.pojo.TplPcMallDeco;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TplPcMallDecoModel {

    @Resource
    private TplPcMallDecoReadMapper tplPcMallDecoReadMapper;
    @Resource
    private TplPcMallDecoWriteMapper tplPcMallDecoWriteMapper;
    @Resource
    private TplPcMallDataReadMapper tplPcMallDataReadMapper;

    /**
     * 新增PC商城装修页组装信息表（基于实例化数据表组合）
     *
     * @param tplPcMallDeco
     * @return
     */
    public Integer saveTplPcMallDeco(TplPcMallDeco tplPcMallDeco) {
        //名称查重
        TplPcMallDecoExample example = new TplPcMallDecoExample();
        example.setDecoName(tplPcMallDeco.getDecoName());
        example.setDecoType(tplPcMallDeco.getDecoType());
        List<TplPcMallDeco> tplPcMallDecos = tplPcMallDecoReadMapper.listByExample(example);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(tplPcMallDecos), "名称重复，请重试");
        AssertUtil.isTrue(tplPcMallDeco.getDecoName().length() > 50, "复制后名称过长，请修改");
        int count = tplPcMallDecoWriteMapper.insert(tplPcMallDeco);
        if (count == 0) {
            throw new MallException("添加PC商城装修页组装信息表（基于实例化数据表组合）失败，请重试");
        }
        return count;
    }

    /**
     * 根据decoId删除PC商城装修页组装信息表（基于实例化数据表组合）
     *
     * @param decoId decoId
     * @return
     */
    public Integer deleteTplPcMallDeco(Integer decoId) {
        if (StringUtils.isEmpty(decoId)) {
            throw new MallException("请选择要删除的数据");
        }
        TplPcMallDeco tplPcMallDeco = tplPcMallDecoReadMapper.getByPrimaryKey(decoId);
        if (null == tplPcMallDeco) {
            throw new MallException("无此模板实例化数据,无法删除");
        }
        if (TplPcConst.IS_ENABLE_YES == tplPcMallDeco.getIsEnable()) {
            throw new MallException("此模板实例化数据正在使用中,无法删除");
        }
        int count = tplPcMallDecoWriteMapper.deleteByPrimaryKey(decoId);
        if (count == 0) {
            log.error("根据decoId：" + decoId + "删除PC商城装修页组装信息表（基于实例化数据表组合）失败");
            throw new MallException("删除PC商城装修页组装信息表（基于实例化数据表组合）失败,请重试");
        }
        return count;
    }

    /**
     * 根据decoId更新PC商城装修页组装信息表（基于实例化数据表组合）
     *
     * @param tplPcMallDeco
     * @return
     */
    public void updateTplPcMallDecoByExample(TplPcMallDeco tplPcMallDeco, TplPcMallDecoExample example) {
        tplPcMallDecoWriteMapper.updateByExampleSelective(tplPcMallDeco, example);
    }

    /**
     * 根据decoId更新PC商城装修页组装信息表（基于实例化数据表组合）
     *
     * @param tplPcMallDeco
     * @return
     */
    public Integer updateTplPcMallDeco(TplPcMallDeco tplPcMallDeco) {
        if (StringUtils.isEmpty(tplPcMallDeco.getDecoId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = tplPcMallDecoWriteMapper.updateByPrimaryKeySelective(tplPcMallDeco);
        if (count == 0) {
            log.error("根据decoId：" + tplPcMallDeco.getDecoId() + "更新PC商城装修页组装信息表（基于实例化数据表组合）失败");
            throw new MallException("更新PC商城装修页组装信息表（基于实例化数据表组合）失败,请重试");
        }
        return count;
    }

    /**
     * 根据decoId获取PC商城装修页组装信息表（基于实例化数据表组合）详情
     *
     * @param decoId decoId
     * @return
     */
    public TplPcMallDeco getTplPcMallDecoByDecoId(Integer decoId) {
        return tplPcMallDecoReadMapper.getByPrimaryKey(decoId);
    }

    /**
     * 根据条件获取PC商城装修页组装信息表（基于实例化数据表组合）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<TplPcMallDeco> getTplPcMallDecoList(TplPcMallDecoExample example, PagerInfo pager) {
        List<TplPcMallDeco> tplPcMallDecoList;
        if (pager != null) {
            pager.setRowsCount(tplPcMallDecoReadMapper.countByExample(example));
            tplPcMallDecoList = tplPcMallDecoReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            tplPcMallDecoList = tplPcMallDecoReadMapper.listByExample(example);
        }
        return tplPcMallDecoList;
    }

    /**
     * 获取装修页详细信息
     *
     * @param decoId
     */
    public TplPcMallDeco getTplPcMallDecoDetail(Integer decoId) {
        TplPcMallDeco detail = tplPcMallDecoReadMapper.getByPrimaryKey(decoId);
        if (null != detail) {
            //装修页主导航条数据
            if (detail.getMasterNavigationBarId() != 0) {
                detail.setMasterNavigationBarData(tplPcMallDataReadMapper.getByPrimaryKey(detail.getMasterNavigationBarId()));
            }
            //添加装修页主轮播图数据
            if (detail.getMasterBannerId() != 0) {
                TplPcMallDataExample tplPcMallDataExample = new TplPcMallDataExample();
                tplPcMallDataExample.setIsEnable(TplPcConst.IS_ENABLE_YES);
                tplPcMallDataExample.setDataId(detail.getMasterBannerId());
                List<TplPcMallData> pcMallDataList = tplPcMallDataReadMapper.listByExample(tplPcMallDataExample);
                if (!CollectionUtils.isEmpty(pcMallDataList)) {
                    detail.setMasterBannerData(pcMallDataList.get(0));
                }
            }
            //添加有序实例化模板
            if (StringUtils.isEmpty(detail.getRankedTplDataIds())) {
                detail.setRankedTplDataList(null);
            } else {
                String[] split1 = detail.getRankedTplDataIds().split(",");
                if (null != split1) {
                    ArrayList<TplPcMallData> tplPcMallDatas = new ArrayList<>();
                    for (String id : split1) {
                        if (StringUtils.isEmpty(id) || Integer.parseInt(id) < 1) {
                            continue;
                        }
                        TplPcMallDataExample tplPcMallDataExample = new TplPcMallDataExample();
                        tplPcMallDataExample.setIsEnable(TplPcConst.IS_ENABLE_YES);
                        tplPcMallDataExample.setDataId(Integer.parseInt(id));
                        List<TplPcMallData> pcMallDataList = tplPcMallDataReadMapper.listByExample(tplPcMallDataExample);
                        if (!CollectionUtils.isEmpty(pcMallDataList)) {
                            pcMallDataList.forEach(tplPcMallData -> {
                                tplPcMallDatas.add(tplPcMallData);
                            });
                        }
                    }
                    detail.setRankedTplDataList(tplPcMallDatas);
                }
            }
        }
        return detail;
    }
}