package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.StoreSettingReadMapper;
import com.slodon.b2b2c.dao.write.msg.StoreSettingWriteMapper;
import com.slodon.b2b2c.msg.example.StoreSettingExample;
import com.slodon.b2b2c.msg.pojo.StoreSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 店铺消息接收设置model
 */
@Component
@Slf4j
public class StoreSettingModel {

    @Resource
    private StoreSettingReadMapper storeSettingReadMapper;
    @Resource
    private StoreSettingWriteMapper storeSettingWriteMapper;

    /**
     * 新增店铺消息接收设置
     *
     * @param storeSetting
     * @return
     */
    public Integer saveStoreSetting(StoreSetting storeSetting) {
        int count = storeSettingWriteMapper.insert(storeSetting);
        if (count == 0) {
            throw new MallException("添加店铺消息接收设置失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplCode删除店铺消息接收设置
     *
     * @param tplCode tplCode
     * @return
     */
    public Integer deleteStoreSetting(String tplCode) {
        if (StringUtils.isEmpty(tplCode)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeSettingWriteMapper.deleteByPrimaryKey(tplCode);
        if (count == 0) {
            log.error("根据tplCode：" + tplCode + "删除店铺消息接收设置失败");
            throw new MallException("删除店铺消息接收设置失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode更新店铺消息接收设置
     *
     * @param storeSetting
     * @return
     */
    public Integer updateStoreSetting(StoreSetting storeSetting) {
        if (StringUtils.isEmpty(storeSetting.getTplCode())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = 0;
        StoreSettingExample example = new StoreSettingExample();
        example.setTplCode(storeSetting.getTplCode());
        example.setStoreId(storeSetting.getStoreId());
        example.setVendorId(storeSetting.getVendorId());
        List<StoreSetting> settingList = storeSettingReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(settingList)) {
            count = storeSettingWriteMapper.insert(storeSetting);
        } else {
            StoreSetting setting = new StoreSetting();
            setting.setIsReceive(storeSetting.getIsReceive());
            count = storeSettingWriteMapper.updateByExampleSelective(setting, example);
        }
        if (count == 0) {
            log.error("根据tplCode：" + storeSetting.getTplCode() + "更新店铺消息接收设置失败");
            throw new MallException("更新店铺消息接收设置失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode获取店铺消息接收设置详情
     *
     * @param tplCode tplCode
     * @return
     */
    public StoreSetting getStoreSettingByTplCode(Integer tplCode) {
        return storeSettingReadMapper.getByPrimaryKey(tplCode);
    }

    /**
     * 根据条件获取店铺消息接收设置列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreSetting> getStoreSettingList(StoreSettingExample example, PagerInfo pager) {
        List<StoreSetting> storeSettingList;
        if (pager != null) {
            pager.setRowsCount(storeSettingReadMapper.countByExample(example));
            storeSettingList = storeSettingReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeSettingList = storeSettingReadMapper.listByExample(example);
        }
        return storeSettingList;
    }
}