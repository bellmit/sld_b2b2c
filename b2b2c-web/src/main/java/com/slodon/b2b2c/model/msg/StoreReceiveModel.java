package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.StoreReceiveReadMapper;
import com.slodon.b2b2c.dao.write.msg.StoreReceiveWriteMapper;
import com.slodon.b2b2c.msg.example.StoreReceiveExample;
import com.slodon.b2b2c.msg.pojo.StoreReceive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StoreReceiveModel {

    @Resource
    private StoreReceiveReadMapper storeReceiveReadMapper;
    @Resource
    private StoreReceiveWriteMapper storeReceiveWriteMapper;

    /**
     * 新增店铺接收消息表
     *
     * @param storeReceive
     * @return
     */
    public Integer saveStoreReceive(StoreReceive storeReceive) {
        int count = storeReceiveWriteMapper.insert(storeReceive);
        if (count == 0) {
            throw new MallException("添加店铺接收消息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据receiveId删除店铺接收消息表
     *
     * @param receiveId receiveId
     * @return
     */
    public Integer deleteStoreReceive(Integer receiveId) {
        if (StringUtils.isEmpty(receiveId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeReceiveWriteMapper.deleteByPrimaryKey(receiveId);
        if (count == 0) {
            log.error("根据receiveId：" + receiveId + "删除店铺接收消息表失败");
            throw new MallException("删除店铺接收消息表失败,请重试");
        }
        return count;
    }

    /**
     * 批量删除店铺接收消息
     *
     * @param receiveIds receiveIds
     * @return
     */
    @Transactional
    public Integer batchDeleteStoreReceive(String receiveIds,Long storeId) {
        StoreReceiveExample example = new StoreReceiveExample();
        example.setStoreId(storeId);
        example.setReceiveIdIn(receiveIds);
        int count = storeReceiveWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据receiveIds：" + receiveIds + "批量删除店铺接收消息失败");
            throw new MallException("删除店铺接收消息失败,请重试");
        }
        return count;
    }

    /**
     * 批量标为已读店铺接收消息
     *
     * @param receiveIds receiveIds
     * @return
     */
    public Integer batchReadStoreReceive(String receiveIds,Long storeId) {
        StoreReceive storeReceive = new StoreReceive();
        storeReceive.setMsgOperateTime(new Date());
        storeReceive.setMsgState(MsgConst.MSG_STATE_HAVE_READ);
        StoreReceiveExample example = new StoreReceiveExample();
        example.setStoreId(storeId);
        example.setReceiveIdIn(receiveIds);
        int count = storeReceiveWriteMapper.updateByExampleSelective(storeReceive, example);
        if (count == 0) {
            log.error("根据receiveIds：" + receiveIds + "批量标为已读店铺接收消息失败");
            throw new MallException("批量标为已读店铺接收消息失败,请重试");
        }
        return count;
    }

    /**
     * 根据receiveId更新店铺接收消息表
     *
     * @param storeReceive
     * @return
     */
    public Integer updateStoreReceive(StoreReceive storeReceive) {
        if (StringUtils.isEmpty(storeReceive.getReceiveId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeReceiveWriteMapper.updateByPrimaryKeySelective(storeReceive);
        if (count == 0) {
            log.error("根据receiveId：" + storeReceive.getReceiveId() + "更新店铺接收消息表失败");
            throw new MallException("更新店铺接收消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据receiveId获取店铺接收消息表详情
     *
     * @param receiveId receiveId
     * @return
     */
    public StoreReceive getStoreReceiveByReceiveId(Integer receiveId) {
        return storeReceiveReadMapper.getByPrimaryKey(receiveId);
    }

    /**
     * 根据条件获取店铺接收消息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreReceive> getStoreReceiveList(StoreReceiveExample example, PagerInfo pager) {
        List<StoreReceive> storeReceiveList;
        if (pager != null) {
            pager.setRowsCount(storeReceiveReadMapper.countByExample(example));
            storeReceiveList = storeReceiveReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeReceiveList = storeReceiveReadMapper.listByExample(example);
        }
        return storeReceiveList;
    }
}