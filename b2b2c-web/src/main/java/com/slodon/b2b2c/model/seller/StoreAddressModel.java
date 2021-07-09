package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.StoreAddressReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreAddressWriteMapper;
import com.slodon.b2b2c.seller.example.StoreAddressExample;
import com.slodon.b2b2c.seller.pojo.StoreAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StoreAddressModel {

    @Resource
    private StoreAddressReadMapper storeAddressReadMapper;
    @Resource
    private StoreAddressWriteMapper storeAddressWriteMapper;

    /**
     * 新增店铺地址表
     *
     * @param storeAddress
     * @return
     */
    public Integer saveStoreAddress(StoreAddress storeAddress) {

        StoreAddressExample example = new StoreAddressExample();
        example.setStoreId(storeAddress.getStoreId());

        int count = storeAddressWriteMapper.insert(storeAddress);
        //如果保存失败直接返回，保存成功检查并处理默认地址
        if (count == 0) {
            throw new MallException("添加店铺地址表失败，请重试");
        }else {
            if (storeAddress.getIsDefault() == StoreConst.ADDRESS_IS_DEFAULT){
                //默认地址只有一个，如果新增的收货地址是默认地址，将其他地址设置为非默认
                example.setAddressIdNotEquals(storeAddress.getAddressId());
                StoreAddress address = new StoreAddress();
                address.setIsDefault(StoreConst.ADDRESS_NOT_DEFAULT);
                storeAddressWriteMapper.updateByExampleSelective(address,example);
            }
        }
        return storeAddress.getAddressId();
    }

    /**
     * 根据addressId删除店铺地址表
     *
     * @param addressId addressId
     * @return
     */
    public Integer deleteStoreAddress(Integer addressId) {
        if (StringUtils.isEmpty(addressId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeAddressWriteMapper.deleteByPrimaryKey(addressId);
        if (count == 0) {
            log.error("根据addressId：" + addressId + "删除店铺地址表失败");
            throw new MallException("删除店铺地址表失败,请重试");
        }
        return count;
    }

    /**
     * 根据addressId更新店铺地址表
     *
     * @param storeAddress
     * @return
     */
    public Integer updateStoreAddress(StoreAddress storeAddress) {

        int count = storeAddressWriteMapper.updateByPrimaryKeySelective(storeAddress);
        if (count == 0) {
            log.error("根据addressId：" + storeAddress.getAddressId() + "更新店铺地址表失败");
            throw new MallException("更新店铺地址表失败,请重试");
        }

        if (storeAddress.getIsDefault() == StoreConst.ADDRESS_IS_DEFAULT) {
            // 更新该店铺其他地址为非默认
            StoreAddressExample example = new StoreAddressExample();
            example.setStoreId(storeAddress.getStoreId());
            example.setAddressIdNotEquals(storeAddress.getAddressId());
            StoreAddress updateAddress = new StoreAddress();
            updateAddress.setIsDefault(StoreConst.ADDRESS_NOT_DEFAULT);
            updateAddress.setUpdateTime(new Date());
            storeAddressWriteMapper.updateByExampleSelective(updateAddress, example);
        }
        return count;
    }

    /**
     * 根据addressId获取店铺地址表详情
     *
     * @param addressId addressId
     * @return
     */
    public StoreAddress getStoreAddressByAddressId(Integer addressId) {
        return storeAddressReadMapper.getByPrimaryKey(addressId);
    }

    /**
     * 根据条件获取店铺地址表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreAddress> getStoreAddressList(StoreAddressExample example, PagerInfo pager) {
        List<StoreAddress> storeAddressList;
        if (pager != null) {
            pager.setRowsCount(storeAddressReadMapper.countByExample(example));
            storeAddressList = storeAddressReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeAddressList = storeAddressReadMapper.listByExample(example);
        }
        return storeAddressList;
    }
}