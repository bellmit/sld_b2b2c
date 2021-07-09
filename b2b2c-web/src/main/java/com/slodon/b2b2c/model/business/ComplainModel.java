package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.example.ComplainExample;
import com.slodon.b2b2c.business.pojo.Complain;
import com.slodon.b2b2c.business.pojo.OrderAfterService;
import com.slodon.b2b2c.core.constant.ComplainConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.business.ComplainReadMapper;
import com.slodon.b2b2c.dao.write.business.ComplainWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ComplainModel {

    @Resource
    private ComplainReadMapper complainReadMapper;
    @Resource
    private ComplainWriteMapper complainWriteMapper;

    @Resource
    private OrderAfterServiceModel orderAfterServiceModel;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增投诉表
     *
     * @param complain
     * @return
     */
    public Integer saveComplain(Complain complain) {
        int count = complainWriteMapper.insert(complain);
        if (count == 0) {
            throw new MallException("添加投诉表失败，请重试");
        }
        return count;
    }

    /**
     * 根据complainId删除投诉表
     *
     * @param complainId complainId
     * @return
     */
    public Integer deleteComplain(Integer complainId) {
        if (StringUtils.isEmpty(complainId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = complainWriteMapper.deleteByPrimaryKey(complainId);
        if (count == 0) {
            log.error("根据complainId：" + complainId + "删除投诉表失败");
            throw new MallException("删除投诉表失败,请重试");
        }
        return count;
    }

    /**
     * 根据complainId更新投诉表
     *
     * @param complain
     * @return
     */
    public Integer updateComplain(Complain complain) {
        if (StringUtils.isEmpty(complain.getComplainId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = complainWriteMapper.updateByPrimaryKeySelective(complain);
        if (count == 0) {
            log.error("根据complainId：" + complain.getComplainId() + "更新投诉表失败");
            throw new MallException("更新投诉表失败,请重试");
        }
        return count;
    }

    /**
     * 根据complainId获取投诉表详情
     *
     * @param complainId complainId
     * @return
     */
    public Complain getComplainByComplainId(Integer complainId) {
        return complainReadMapper.getByPrimaryKey(complainId);
    }

    /**
     * 根据条件获取投诉表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Complain> getComplainList(ComplainExample example, PagerInfo pager) {
        List<Complain> complainList;
        if (pager != null) {
            pager.setRowsCount(complainReadMapper.countByExample(example));
            complainList = complainReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            complainList = complainReadMapper.listByExample(example);
        }
        return complainList;
    }

    /**
     * 批量投诉审核
     *
     * @param
     * @return
     */
    @Transactional
    public Boolean batchUpdateComplain(Integer adminId, String complainIds, Integer auditType, String adminHandleContent) {
        Complain updateOne = new Complain();
        if (auditType == ComplainConst.AUDIT_TYPE_YES) {
            //审核通过
            updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_2);
            updateOne.setComplainAuditTime(new Date());
            updateOne.setComplainAuditAdminId(adminId);
            ComplainExample example = new ComplainExample();
            example.setComplainIdIn(complainIds);
            example.setComplainState(ComplainConst.COMPLAIN_STATE_1);
            int count = complainWriteMapper.updateByExampleSelective(updateOne, example);
            AssertUtil.notNullOrZero(count, "批量投诉审核通过失败");
        } else if (auditType == ComplainConst.AUDIT_TYPE_NO) {
            //审核拒绝
            updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_7);
            updateOne.setComplainAuditTime(new Date());
            updateOne.setComplainAuditAdminId(adminId);
            updateOne.setAdminHandleContent(adminHandleContent);
            ComplainExample example = new ComplainExample();
            example.setComplainIdIn(complainIds);
            example.setComplainState(ComplainConst.COMPLAIN_STATE_1);
            int count = complainWriteMapper.updateByExampleSelective(updateOne, example);
            AssertUtil.notNullOrZero(count, "批量投诉审核拒绝失败");
        }
        return true;
    }

    /**
     * 新增投诉表
     * 投诉商家
     *
     * @param afsSn
     * @param complainContent
     * @param complainPic
     * @return
     */
    public Integer saveComplain(String afsSn, String complainContent, String complainPic, Integer memberId, String memberName) {
        //根据afsSn获取订单售后服务信息
        OrderAfterService orderAfterServiceDb = orderAfterServiceModel.getAfterServiceByAfsSn(afsSn);
        AssertUtil.isTrue(!orderAfterServiceDb.getMemberId().equals(memberId), "您无权操作");
//        //查询订单货品
//        OrderProduct orderProduct = orderProductModel.getOrderProductByOrderProductId(orderAfterServiceDb.getOrderProductId());
//        AssertUtil.notNull(orderProduct, "获取订单货品信息为空，请重试");
        //新增投诉商家
        Complain insertOne = new Complain();
        insertOne.setAfsSn(afsSn);
        insertOne.setOrderSn(orderAfterServiceDb.getOrderSn());
        insertOne.setComplainMemberId(memberId);
        insertOne.setComplainMemberName(memberName);
        insertOne.setStoreId(orderAfterServiceDb.getStoreId());
        insertOne.setStoreName(orderAfterServiceDb.getStoreName());
        insertOne.setComplainContent(complainContent);
        insertOne.setComplainPic(complainPic);
        insertOne.setComplainTime(new Date());
        insertOne.setComplainState(ComplainConst.COMPLAIN_STATE_1);
        int count = complainWriteMapper.insert(insertOne);
        AssertUtil.notNullOrZero(count, "添加投诉表失败，请重试");
        return insertOne.getComplainId();
    }

    /**
     * 根据complainId更新投诉表
     * 撤销投诉,申诉,放弃申诉,提交仲裁,仲裁等
     *
     * @param complain
     * @param example
     * @return
     */
    public Integer updateByExampleSelective(Complain complain, ComplainExample example) {
        int count = complainWriteMapper.updateByExampleSelective(complain, example);
        AssertUtil.notNullOrZero(count, "更新投诉表失败,请重试");
        return count;
    }

    /**
     * 定时处理超过3天的交易投诉交由商家申诉
     *
     * @return
     */
    public boolean jobSubmitStoreComplaint() {
        int n = Integer.parseInt(stringRedisTemplate.opsForValue().get("default_submit_store_complaint_time"));
        // 获取当前时间3天之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -n);

        Date complainTime = calendar.getTime();

        //获取会员投诉超过3天的投诉单
        ComplainExample example = new ComplainExample();
        example.setComplainTimeBefore(complainTime);
        example.setComplainState(ComplainConst.COMPLAIN_STATE_1);
        List<Complain> complaintList = complainReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(complaintList)) {
            // 单条数据处理异常不影响其他数据执行
            for (Complain complaint : complaintList) {
                Complain complaintNew = new Complain();
                complaintNew.setComplainId(complaint.getComplainId());
                complaintNew.setComplainState(ComplainConst.COMPLAIN_STATE_2);
                complaintNew.setComplainAuditTime(new Date());
                int update = complainWriteMapper.updateByPrimaryKeySelective(complaintNew);
                AssertUtil.isTrue(update == 0, "处理超过3天的交易投诉交由商家申诉时失败。");
            }
        }
        return true;
    }

    /**
     * 定时处理商家申诉超过7天的交易投诉自动投诉成功
     *
     * @return
     */
    public boolean jobAutoComplaintSuccess() {
        int n = Integer.parseInt(stringRedisTemplate.opsForValue().get("default_complain_success_time"));
        // 获取当前时间7天之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -n);

        Date complainCheckTime = calendar.getTime();

        //获取商家申诉超过7天的申诉单
        ComplainExample example = new ComplainExample();
        example.setComplainAuditTimeBefore(complainCheckTime);
        example.setComplainState(ComplainConst.COMPLAIN_STATE_2);
        List<Complain> complaintList = complainReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(complaintList)) {
            // 单条数据处理异常不影响其他数据执行
            for (Complain complaint : complaintList) {
                Complain complaintNew = new Complain();
                complaintNew.setComplainId(complaint.getComplainId());
                complaintNew.setComplainState(ComplainConst.COMPLAIN_STATE_3);
                complaintNew.setAppealContent("系统默认商家申诉超过7天后会员自动投诉成功");
                complaintNew.setAppealTime(new Date());
                int update = complainWriteMapper.updateByPrimaryKeySelective(complaintNew);
                AssertUtil.isTrue(update == 0, "处理商家申诉超过7天的交易投诉自动投诉成功时失败。");
            }
        }
        return true;
    }
}