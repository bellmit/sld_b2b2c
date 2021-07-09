package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.dao.write.member.MemberAddressWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.member.dto.MemberAddressAddDTO;
import com.slodon.b2b2c.member.dto.MemberAddressUpdateDTO;
import com.slodon.b2b2c.member.example.MemberAddressExample;
import com.slodon.b2b2c.member.pojo.MemberAddress;
import com.slodon.b2b2c.dao.read.member.MemberAddressReadMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MemberAddressModel {

    @Resource
    private MemberAddressReadMapper memberAddressReadMapper;
    @Resource
    private MemberAddressWriteMapper memberAddressWriteMapper;

    /**
     * 新增收货地址
     *
     * @param memberAddress
     * @return
     */
    public Integer saveMemberAddress(MemberAddress memberAddress) {
        int count = memberAddressWriteMapper.insert(memberAddress);
        if (count == 0) {
            throw new MallException("添加收货地址失败，请重试");
        }
        return count;
    }

    /**
     * 新增收货地址
     *
     * @param memberAddressAddDTO
     * @param memberId
     * @return
     */
    @Transactional
    public Integer saveMemberAddress(MemberAddressAddDTO memberAddressAddDTO, Integer memberId) throws Exception {

        if (memberAddressAddDTO.getIsDefault() == MemberConst.IS_DEFAULT_1) {
            ////新增默认地址,先修改原默认地址为非默认地址,在新增
            MemberAddressExample example = new MemberAddressExample();
            example.setMemberId(memberId);
            example.setIsDefault(MemberConst.IS_DEFAULT_1);

            MemberAddress updateOld = new MemberAddress();
            updateOld.setIsDefault(MemberConst.IS_DEFAULT_0);
            updateOld.setUpdateTime(new Date());
            memberAddressWriteMapper.updateByExampleSelective(updateOld, example);
        }

        //新增
        MemberAddress insertOne = new MemberAddress();
        PropertyUtils.copyProperties(insertOne, memberAddressAddDTO);
        insertOne.setMemberId(memberId);
        insertOne.setCreateTime(new Date());
        int count = memberAddressWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加收货地址失败，请重试");
        }
        return insertOne.getAddressId();
    }

    /**
     * 根据addressId删除收货地址
     *
     * @param addressId addressId
     * @return
     */
    public Integer deleteMemberAddress(Integer addressId) {
        if (StringUtils.isEmpty(addressId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberAddressWriteMapper.deleteByPrimaryKey(addressId);
        if (count == 0) {
            log.error("根据addressId：" + addressId + "删除收货地址失败");
            throw new MallException("删除收货地址失败,请重试");
        }
        return count;
    }


    /**
     * 根据addressId更新收货地址
     *
     * @param memberAddress
     * @return
     */
    public Integer updateMemberAddress(MemberAddress memberAddress) {
        if (StringUtils.isEmpty(memberAddress.getAddressId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberAddressWriteMapper.updateByPrimaryKeySelective(memberAddress);
        if (count == 0) {
            log.error("根据addressId：" + memberAddress.getAddressId() + "更新收货地址失败");
            throw new MallException("更新收货地址失败,请重试");
        }
        return count;
    }

    /**
     * 根据addressId更新收货地址
     *
     * @param memberAddressUpdateDTO
     * @param memberId
     * @return
     */
    @Transactional
    public Integer updateMemberAddress(MemberAddressUpdateDTO memberAddressUpdateDTO, Integer memberId) throws Exception {

        //编辑
        MemberAddress updateOne = new MemberAddress();
        PropertyUtils.copyProperties(updateOne, memberAddressUpdateDTO);
        updateOne.setUpdateTime(new Date());
        if (memberAddressUpdateDTO.getIsDefault() == MemberConst.IS_DEFAULT_1) {
            //设置默认地址
            changeDefaultAddress(updateOne.getAddressId(), MemberConst.IS_DEFAULT_1, memberId);
        }

        int count = memberAddressWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据addressId：" + memberAddressUpdateDTO.getAddressId() + "更新收货地址失败");
            throw new MallException("更新收货地址失败,请重试");
        }
        return count;
    }

    /**
     * 根据addressId获取收货地址详情
     *
     * @param addressId addressId
     * @return
     */
    public MemberAddress getMemberAddressByAddressId(Integer addressId) {
        return memberAddressReadMapper.getByPrimaryKey(addressId);
    }

    /**
     * 根据条件获取收货地址列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberAddress> getMemberAddressList(MemberAddressExample example, PagerInfo pager) {
        List<MemberAddress> memberAddressList;
        if (pager != null) {
            pager.setRowsCount(memberAddressReadMapper.countByExample(example));
            memberAddressList = memberAddressReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberAddressList = memberAddressReadMapper.listByExample(example);
        }
        return memberAddressList;
    }

    /**
     * 根据addressId设置默认收货地址
     *
     * @param addressId
     * @return
     */
    @Transactional
    public Integer changeDefaultAddress(Integer addressId, Integer isDefault, Integer memberId) {
        MemberAddress updateOne = new MemberAddress();
        updateOne.setAddressId(addressId);
        updateOne.setIsDefault(isDefault);
        int count = memberAddressWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据addressId：" + addressId + "设置默认收货地址失败");
            throw new MallException("设置默认收货地址失败,请重试");
        }
        if (isDefault == MemberConst.IS_DEFAULT_1) {
            //更新该用户其他地址为非默认
            MemberAddressExample example = new MemberAddressExample();
            example.setMemberId(memberId);
            example.setAddressIdNotEquals(addressId);
            MemberAddress updateAddress = new MemberAddress();
            updateAddress.setIsDefault(MemberConst.IS_DEFAULT_0);
            updateAddress.setUpdateTime(new Date());
            memberAddressWriteMapper.updateByExampleSelective(updateAddress, example);
        }
        return count;
    }

    /**
     * 批量删除收货地址
     *
     * @param addressIds
     * @param memberId
     * @return
     */
    @Transactional
    public Integer batchDeleteMemberAddress(String addressIds, Integer memberId) {
        MemberAddressExample example = new MemberAddressExample();
        example.setMemberId(memberId);
        example.setAddressIdIn(addressIds);
        int count = memberAddressWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据addressIds：" + addressIds + "删除收货地址失败");
            throw new MallException("删除收货地址失败,请重试");
        }
        return count;
    }
}