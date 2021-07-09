package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.StorePushReadMapper;
import com.slodon.b2b2c.dao.write.msg.StorePushWriteMapper;
import com.slodon.b2b2c.msg.example.StorePushExample;
import com.slodon.b2b2c.msg.pojo.StorePush;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class StorePushModel {
    @Resource
    private StorePushReadMapper storePushReadMapper;

    @Resource
    private StorePushWriteMapper storePushWriteMapper;

    /**
     * 新增店铺推送消息表
     *
     * @param storePush
     * @return
     */
    public Integer saveStorePush(StorePush storePush) {
        int count = storePushWriteMapper.insert(storePush);
        if (count == 0) {
            throw new MallException("添加店铺推送消息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据pushId删除店铺推送消息表
     *
     * @param pushId pushId
     * @return
     */
    public Integer deleteStorePush(Integer pushId) {
        if (StringUtils.isEmpty(pushId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storePushWriteMapper.deleteByPrimaryKey(pushId);
        if (count == 0) {
            log.error("根据pushId：" + pushId + "删除店铺推送消息表失败");
            throw new MallException("删除店铺推送消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据pushId更新店铺推送消息表
     *
     * @param storePush
     * @return
     */
    public Integer updateStorePush(StorePush storePush) {
        if (StringUtils.isEmpty(storePush.getPushId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storePushWriteMapper.updateByPrimaryKeySelective(storePush);
        if (count == 0) {
            log.error("根据pushId：" + storePush.getPushId() + "更新店铺推送消息表失败");
            throw new MallException("更新店铺推送消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据pushId获取店铺推送消息表详情
     *
     * @param pushId pushId
     * @return
     */
    public StorePush getStorePushByPushId(Integer pushId) {
        return storePushReadMapper.getByPrimaryKey(pushId);
    }

    /**
     * 根据条件获取店铺推送消息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StorePush> getStorePushList(StorePushExample example, PagerInfo pager) {
        List<StorePush> storePushList;
        if (pager != null) {
            pager.setRowsCount(storePushReadMapper.countByExample(example));
            storePushList = storePushReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storePushList = storePushReadMapper.listByExample(example);
        }
        return storePushList;
    }
}