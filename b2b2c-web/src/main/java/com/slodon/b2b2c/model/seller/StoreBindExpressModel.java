package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.seller.StoreBindExpressReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreBindExpressWriteMapper;
import com.slodon.b2b2c.seller.example.StoreBindExpressExample;
import com.slodon.b2b2c.seller.pojo.StoreBindExpress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 店铺的选择使用的快递公司model
 */
@Component
@Slf4j
public class StoreBindExpressModel {

    @Resource
    private StoreBindExpressReadMapper storeBindExpressReadMapper;
    @Resource
    private StoreBindExpressWriteMapper storeBindExpressWriteMapper;

    /**
     * 新增店铺的选择使用的快递公司
     *
     * @param storeBindExpress
     * @return
     */
    public Integer saveStoreBindExpress(StoreBindExpress storeBindExpress) {

        storeBindExpress.setCreateTime(new Date());
        int count = storeBindExpressWriteMapper.insert(storeBindExpress);
        if (count == 0) {
            throw new MallException("添加店铺的选择使用的快递公司失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除店铺的选择使用的快递公司
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteStoreBindExpress(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeBindExpressWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除店铺的选择使用的快递公司失败");
            throw new MallException("删除店铺的选择使用的快递公司失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId更新店铺的选择使用的快递公司
     *
     * @param storeBindExpress
     * @return
     */
    public Integer updateStoreBindExpress(StoreBindExpress storeBindExpress) {
        if (StringUtils.isEmpty(storeBindExpress.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeBindExpressWriteMapper.updateByPrimaryKeySelective(storeBindExpress);
        if (count == 0) {
            log.error("根据bindId：" + storeBindExpress.getBindId() + "更新店铺的选择使用的快递公司失败");
            throw new MallException("更新店铺的选择使用的快递公司失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId开启/关闭物流公司
     *
     * @param bindId
     * @param expressState
     * @return
     */
    public Integer openOrCloseExpress(Integer bindId, String expressState) {
        //根据bindId查询快递公司
        StoreBindExpress storeBindExpress = storeBindExpressReadMapper.getByPrimaryKey(bindId);
        AssertUtil.notNull(storeBindExpress, "快递公司不存在");

        Integer count;
        if (expressState.equals(StoreConst.EXPRESS_STATE_CLOSE)) {
            //关闭快递公司
            storeBindExpress.setExpressState(StoreConst.EXPRESS_STATE_CLOSE);
            count = storeBindExpressWriteMapper.updateByPrimaryKeySelective(storeBindExpress);
            if (count == 0) {
                throw new MallException("关闭快递公司失败,请重试");
            }
        } else {
            //开启物流公司
            storeBindExpress.setExpressState(StoreConst.EXPRESS_STATE_OPEN);
            count = storeBindExpressWriteMapper.updateByPrimaryKeySelective(storeBindExpress);
            if (count == 0) {
                throw new MallException("开启快递公司失败,请重试");
            }
        }
        return count;
    }

    /**
     * 根据bindId获取店铺的选择使用的快递公司详情
     *
     * @param bindId bindId
     * @return
     */
    public StoreBindExpress getStoreBindExpressByBindId(Integer bindId) {
        return storeBindExpressReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取店铺的选择使用的快递公司列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreBindExpress> getStoreBindExpressList(StoreBindExpressExample example, PagerInfo pager) {
        List<StoreBindExpress> storeBindExpressList;
        if (pager != null) {
            pager.setRowsCount(storeBindExpressReadMapper.countByExample(example));
            storeBindExpressList = storeBindExpressReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeBindExpressList = storeBindExpressReadMapper.listByExample(example);
        }
        return storeBindExpressList;
    }
}