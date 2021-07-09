package com.slodon.b2b2c.model.helpdesk;

import com.slodon.b2b2c.core.constant.HelpdeskConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskContactReadMapper;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskTransferReadMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskContactWriteMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskTransferWriteMapper;
import com.slodon.b2b2c.helpdesk.example.HelpdeskContactExample;
import com.slodon.b2b2c.helpdesk.example.HelpdeskTransferExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskContact;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskTransfer;
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
public class HelpdeskTransferModel {

    @Resource
    private HelpdeskTransferReadMapper helpdeskTransferReadMapper;
    @Resource
    private HelpdeskTransferWriteMapper helpdeskTransferWriteMapper;
    @Resource
    private HelpdeskContactWriteMapper helpdeskContactWriteMapper;
    @Resource
    private HelpdeskContactReadMapper helpdeskContactReadMapper;

    /**
     * 新增客服转接表
     *
     * @param helpdeskTransfer
     * @return
     */
    @Transactional
    public Integer saveHelpdeskTransfer(HelpdeskTransfer helpdeskTransfer) {
        // 保存聊天信息
        Integer count = helpdeskTransferWriteMapper.insert(helpdeskTransfer);
        if (count == 0) {
            throw new MallException("客服转接信息保存失败，请重试！");
        }

        // 原客服数据
        HelpdeskContactExample example = new HelpdeskContactExample();
        example.setMemberId(helpdeskTransfer.getMemberId());
        example.setVendorId(helpdeskTransfer.getSrcVendorId());
        List<HelpdeskContact> helpdeskContacts = helpdeskContactReadMapper.listByExample(example);
        HelpdeskContact helpdeskContact = new HelpdeskContact();
        if (CollectionUtils.isEmpty(helpdeskContacts)) {
            helpdeskContact.setMemberId(helpdeskTransfer.getMemberId());
            helpdeskContact.setVendorId(helpdeskTransfer.getDstVendorId());
            helpdeskContact.setStoreId(helpdeskTransfer.getStoreId());
            helpdeskContact.setState(HelpdeskConst.ACTIVE_STATE_NO);
            helpdeskContact.setCreateTime(new Date());
            count = helpdeskContactWriteMapper.insert(helpdeskContact);
            if (count == 0) {
                throw new MallException("原客服-会员关系信息保存失败，请重试！");
            }
        } else {
            helpdeskContact.setContactId(helpdeskContacts.get(0).getContactId());
            helpdeskContact.setState(HelpdeskConst.ACTIVE_STATE_NO);
            helpdeskContact.setUpdateTime(new Date());
            count = helpdeskContactWriteMapper.updateByPrimaryKeySelective(helpdeskContact);
            if (count == 0) {
                throw new MallException("原客服-会员关系信息更新失败，请重试！");
            }
        }

        // 被转接者数据
        HelpdeskContactExample example2 = new HelpdeskContactExample();
        example2.setMemberId(helpdeskTransfer.getMemberId());
        example2.setVendorId(helpdeskTransfer.getDstVendorId());
        List<HelpdeskContact> contactList = helpdeskContactReadMapper.listByExample(example);
        helpdeskContact = new HelpdeskContact();
        if (CollectionUtils.isEmpty(contactList)) {
            helpdeskContact.setMemberId(helpdeskTransfer.getMemberId());
            helpdeskContact.setVendorId(helpdeskTransfer.getDstVendorId());
            helpdeskContact.setStoreId(helpdeskTransfer.getStoreId());
            helpdeskContact.setState(HelpdeskConst.ACTIVE_STATE_YES);
            helpdeskContact.setCreateTime(new Date());
            count = helpdeskContactWriteMapper.insert(helpdeskContact);
            if (count == 0) {
                throw new MallException("被转接客服-会员关系信息保存失败，请重试！");
            }
        } else {
            helpdeskContact.setContactId(contactList.get(0).getContactId());
            helpdeskContact.setState(HelpdeskConst.ACTIVE_STATE_YES);
            helpdeskContact.setUpdateTime(new Date());
            count = helpdeskContactWriteMapper.updateByPrimaryKeySelective(helpdeskContact);
            if (count == 0) {
                throw new MallException("被转接客服-会员关系信息更新失败，请重试！");
            }
        }
        return count;
    }

    /**
     * 根据transferId删除客服转接表
     *
     * @param transferId transferId
     * @return
     */
    public Integer deleteHelpdeskTransfer(Integer transferId) {
        if (StringUtils.isEmpty(transferId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = helpdeskTransferWriteMapper.deleteByPrimaryKey(transferId);
        if (count == 0) {
            log.error("根据transferId：" + transferId + "删除客服转接表失败");
            throw new MallException("删除客服转接表失败,请重试");
        }
        return count;
    }

    /**
     * 根据transferId更新客服转接表
     *
     * @param helpdeskTransfer
     * @return
     */
    public Integer updateHelpdeskTransfer(HelpdeskTransfer helpdeskTransfer) {
        if (StringUtils.isEmpty(helpdeskTransfer.getTransferId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = helpdeskTransferWriteMapper.updateByPrimaryKeySelective(helpdeskTransfer);
        if (count == 0) {
            log.error("根据transferId：" + helpdeskTransfer.getTransferId() + "更新客服转接表失败");
            throw new MallException("更新客服转接表失败,请重试");
        }
        return count;
    }

    /**
     * 根据transferId获取客服转接表详情
     *
     * @param transferId transferId
     * @return
     */
    public HelpdeskTransfer getHelpdeskTransferByTransferId(Integer transferId) {
        return helpdeskTransferReadMapper.getByPrimaryKey(transferId);
    }

    /**
     * 根据条件获取客服转接表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<HelpdeskTransfer> getHelpdeskTransferList(HelpdeskTransferExample example, PagerInfo pager) {
        List<HelpdeskTransfer> helpdeskTransferList;
        if (pager != null) {
            pager.setRowsCount(helpdeskTransferReadMapper.countByExample(example));
            helpdeskTransferList = helpdeskTransferReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            helpdeskTransferList = helpdeskTransferReadMapper.listByExample(example);
        }
        return helpdeskTransferList;
    }
}