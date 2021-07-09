package com.slodon.b2b2c.model.helpdesk;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskContactReadMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskContactWriteMapper;
import com.slodon.b2b2c.helpdesk.example.HelpdeskContactExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskContact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class HelpdeskContactModel {

    @Resource
    private HelpdeskContactReadMapper helpdeskContactReadMapper;
    @Resource
    private HelpdeskContactWriteMapper helpdeskContactWriteMapper;

    /**
     * 新增客服-会员关系表
     *
     * @param helpdeskContact
     * @return
     */
    public Integer saveHelpdeskContact(HelpdeskContact helpdeskContact) {
        int count = helpdeskContactWriteMapper.insert(helpdeskContact);
        if (count == 0) {
            throw new MallException("添加客服-会员关系表失败，请重试");
        }
        return count;
    }

    /**
     * 根据contactId删除客服-会员关系表
     *
     * @param contactId contactId
     * @return
     */
    public Integer deleteHelpdeskContact(Integer contactId) {
        if (StringUtils.isEmpty(contactId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = helpdeskContactWriteMapper.deleteByPrimaryKey(contactId);
        if (count == 0) {
            log.error("根据contactId：" + contactId + "删除客服-会员关系表失败");
            throw new MallException("删除客服-会员关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据contactId更新客服-会员关系表
     *
     * @param helpdeskContact
     * @return
     */
    public Integer updateHelpdeskContact(HelpdeskContact helpdeskContact) {
        if (StringUtils.isEmpty(helpdeskContact.getContactId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = helpdeskContactWriteMapper.updateByPrimaryKeySelective(helpdeskContact);
        if (count == 0) {
            log.error("根据contactId：" + helpdeskContact.getContactId() + "更新客服-会员关系表失败");
            throw new MallException("更新客服-会员关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据contactId获取客服-会员关系表详情
     *
     * @param contactId contactId
     * @return
     */
    public HelpdeskContact getHelpdeskContactByContactId(Integer contactId) {
        return helpdeskContactReadMapper.getByPrimaryKey(contactId);
    }

    /**
     * 根据条件获取客服-会员关系表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<HelpdeskContact> getHelpdeskContactList(HelpdeskContactExample example, PagerInfo pager) {
        List<HelpdeskContact> helpdeskContactList;
        if (pager != null) {
            pager.setRowsCount(helpdeskContactReadMapper.countByExample(example));
            helpdeskContactList = helpdeskContactReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            helpdeskContactList = helpdeskContactReadMapper.listByExample(example);
        }
        return helpdeskContactList;
    }
}