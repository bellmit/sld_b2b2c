package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.constant.MemberInvoiceConst;
import com.slodon.b2b2c.dao.write.member.MemberInvoiceWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.member.MemberInvoiceReadMapper;
import com.slodon.b2b2c.member.dto.MemberInvoiceAddDTO;
import com.slodon.b2b2c.member.dto.MemberInvoiceUpdateDTO;
import com.slodon.b2b2c.member.example.MemberInvoiceExample;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 会员发票信息表model
 */
@Component
@Slf4j
public class MemberInvoiceModel {

    @Resource
    private MemberInvoiceReadMapper memberInvoiceReadMapper;
    @Resource
    private MemberInvoiceWriteMapper memberInvoiceWriteMapper;

    /**
     * 新增会员发票信息表
     *
     * @param memberInvoice
     * @return
     */
    public Integer saveMemberInvoice(MemberInvoice memberInvoice) {
        int count = memberInvoiceWriteMapper.insert(memberInvoice);
        if (count == 0) {
            throw new MallException("添加会员发票信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据invoiceId删除会员发票信息表
     *
     * @param invoiceId invoiceId
     * @return
     */
    public Integer deleteMemberInvoice(Integer invoiceId) {
        if (StringUtils.isEmpty(invoiceId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberInvoiceWriteMapper.deleteByPrimaryKey(invoiceId);
        if (count == 0) {
            log.error("根据invoiceId：" + invoiceId + "删除会员发票信息表失败");
            throw new MallException("删除会员发票信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据invoiceId更新会员发票信息表
     *
     * @param memberInvoice
     * @return
     */
    public Integer updateMemberInvoice(MemberInvoice memberInvoice) {
        if (StringUtils.isEmpty(memberInvoice.getInvoiceId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberInvoiceWriteMapper.updateByPrimaryKeySelective(memberInvoice);
        if (count == 0) {
            log.error("根据invoiceId：" + memberInvoice.getInvoiceId() + "更新会员发票信息表失败");
            throw new MallException("更新会员发票信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据invoiceId获取会员发票信息表详情
     *
     * @param invoiceId invoiceId
     * @return
     */
    public MemberInvoice getMemberInvoiceByInvoiceId(Integer invoiceId) {
        return memberInvoiceReadMapper.getByPrimaryKey(invoiceId);
    }

    /**
     * 根据条件获取会员发票信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberInvoice> getMemberInvoiceList(MemberInvoiceExample example, PagerInfo pager) {
        List<MemberInvoice> memberInvoiceList;
        if (pager != null) {
            pager.setRowsCount(memberInvoiceReadMapper.countByExample(example));
            memberInvoiceList = memberInvoiceReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberInvoiceList = memberInvoiceReadMapper.listByExample(example);
        }
        return memberInvoiceList;
    }


    /**
     * 新增会员发票信息表
     *
     * @param memberInvoiceAddDTO
     * @param memberId
     * @return
     */
    @Transactional
    public Integer saveMemberInvoice(MemberInvoiceAddDTO memberInvoiceAddDTO, Integer memberId) throws Exception {
        //修改默认值
        if (memberInvoiceAddDTO.getIsDefault() == MemberInvoiceConst.IS_DEFAULT_1) {
            //默认发票只有一个，如果新增的发票是默认发票，将其他发票设置为非默认
            MemberInvoiceExample example = new MemberInvoiceExample();
            example.setMemberId(memberId);
            MemberInvoice record = new MemberInvoice();
            record.setIsDefault(MemberInvoiceConst.IS_DEFAULT_0);
            memberInvoiceWriteMapper.updateByExampleSelective(record, example);
        }
        //组装
        MemberInvoice insertOne = new MemberInvoice();
        PropertyUtils.copyProperties(insertOne, memberInvoiceAddDTO);
        insertOne.setMemberId(memberId);
        insertOne.setCreateTime(new Date());
        //新增发票信息
        int count = memberInvoiceWriteMapper.insert(insertOne);
        AssertUtil.notNullOrZero(count, "添加会员发票信息表失败，请重试");
        return insertOne.getInvoiceId();
    }

    /**
     * 根据invoiceId更新会员发票信息表
     *
     * @param memberInvoiceUpdateDTO
     * @param memberId
     * @return
     */
    @Transactional
    public Integer updateMemberInvoice(MemberInvoiceUpdateDTO memberInvoiceUpdateDTO, Integer memberId) throws Exception {

        MemberInvoice updateOne = new MemberInvoice();
        PropertyUtils.copyProperties(updateOne, memberInvoiceUpdateDTO);

        //修改默认值
        changeDefaultInvoice(updateOne.getInvoiceId(), updateOne.getIsDefault() == MemberInvoiceConst.IS_DEFAULT_1, memberId);
        int count = memberInvoiceWriteMapper.updateByPrimaryKeySelective(updateOne);
        AssertUtil.notNullOrZero(count, "更新会员发票信息表失败,请重试");
        return count;
    }

    /**
     * 根据invoiceId更新默认发票
     *
     * @param invoiceId
     * @param isDefault
     * @param memberId
     * @return
     */
    @Transactional
    public Integer changeDefaultInvoice(Integer invoiceId, Boolean isDefault, Integer memberId) {
        MemberInvoice updateOne = new MemberInvoice();
        updateOne.setInvoiceId(invoiceId);
        if (isDefault) {
            //默认发票只有一个，如果新增的发票是默认发票，将其他发票设置为非默认
            MemberInvoiceExample example = new MemberInvoiceExample();
            example.setMemberId(memberId);
            example.setInvoiceIdNotEquals(invoiceId);
            MemberInvoice record = new MemberInvoice();
            record.setIsDefault(MemberInvoiceConst.IS_DEFAULT_0);
            memberInvoiceWriteMapper.updateByExampleSelective(record, example);

            updateOne.setIsDefault(MemberConst.IS_DEFAULT_1);
        } else {
            //false 直接更改
            updateOne.setIsDefault(MemberConst.IS_DEFAULT_0);
        }
        //更改默认值
        int count = memberInvoiceWriteMapper.updateByPrimaryKeySelective(updateOne);
        AssertUtil.notNullOrZero(count, "更新默认发票失败,请重试");
        return count;
    }

    /**
     * 批量删除会员发票信息表
     *
     * @param invoiceIds
     * @param memberId
     * @return
     */
    public Integer batchDeleteMemberInvoice(String invoiceIds, Integer memberId) {

        MemberInvoiceExample example = new MemberInvoiceExample();
        example.setMemberId(memberId);
        example.setInvoiceIdIn(invoiceIds);
        int count = memberInvoiceWriteMapper.deleteByExample(example);
        AssertUtil.notNullOrZero(count, "删除会员发票信息表失败,请重试");
        return count;
    }
}