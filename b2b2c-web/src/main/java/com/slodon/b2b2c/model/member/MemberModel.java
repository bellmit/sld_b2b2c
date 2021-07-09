package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.Md5;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.member.MemberReadMapper;
import com.slodon.b2b2c.dao.write.member.MemberWriteMapper;
import com.slodon.b2b2c.member.dto.*;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.promotion.CouponMemberModel;
import com.slodon.b2b2c.promotion.example.CouponMemberExample;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
import com.slodon.b2b2c.vo.member.MemberDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MemberModel {

    @Resource
    private MemberReadMapper memberReadMapper;
    @Resource
    private MemberWriteMapper memberWriteMapper;
    @Resource
    private CouponMemberModel couponMemberModel;
    @Resource
    private OrderModel orderModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增会员
     *
     * @param member
     * @return
     */
    public Integer saveMember(Member member) {
        int count = memberWriteMapper.insert(member);
        if (count == 0) {
            throw new MallException("添加会员失败，请重试");
        }
        return count;
    }

    /**
     * 新增会员
     *
     * @param memberAddDTO
     * @return
     */
    public Integer saveMember(MemberAddDTO memberAddDTO) throws Exception {
        //查重
        MemberExample example = new MemberExample();
        example.setMemberName(memberAddDTO.getMemberName());
        List<Member> list = memberReadMapper.listByExample(example);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "会员名称重复,请重新填写");

        example.setMemberName(null);
        example.setMemberMobile(memberAddDTO.getMemberMobile());
        List<Member> mobileList = memberReadMapper.listByExample(example);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(mobileList), "该手机号已经绑定会员");

        //新增
        Member insertOne = new Member();
        PropertyUtils.copyProperties(insertOne, memberAddDTO);
        insertOne.setLoginPwd(Md5.getMd5String(memberAddDTO.getLoginPwd()));
        insertOne.setPayPwd(Md5.getMd5String(memberAddDTO.getLoginPwd()));
        insertOne.setRegisterTime(new Date());
        insertOne.setLastLoginTime(new Date());
        insertOne.setLastLoginIp("");
        insertOne.setLoginNumber(0);

        int count = memberWriteMapper.insert(insertOne);
        AssertUtil.notNullOrZero(count, "添加会员失败，请重试");
        return count;
    }

    /**
     * 根据memberId删除会员
     *
     * @param memberId memberId
     * @return
     */
    public Integer deleteMember(Integer memberId) {
        int count = memberWriteMapper.deleteByPrimaryKey(memberId);
        if (count == 0) {
            log.error("根据memberId：" + memberId + "删除会员失败");
            throw new MallException("删除会员失败,请重试");
        }
        return count;
    }


    /**
     * 根据memberId更新会员
     *
     * @param member
     * @return
     */
    public Integer updateMember(Member member) {
        if (StringUtils.isEmpty(member.getMemberId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberWriteMapper.updateByPrimaryKeySelective(member);
        if (count == 0) {
            log.error("根据memberId：" + member.getMemberId() + "更新会员失败");
            throw new MallException("更新会员失败,请重试");
        }
        return count;
    }

    /**
     * 根据memberId更新会员
     *
     * @param memberUpdateDTO
     * @return
     */
    public Integer updateMember(MemberUpdateDTO memberUpdateDTO) throws Exception {

        //查重
        MemberExample example = new MemberExample();
        example.setMemberName(memberUpdateDTO.getMemberName());
        example.setMemberIdNotEquals(memberUpdateDTO.getMemberId());
        List<Member> list = memberReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("会员名称重复,请重新填写");
        }

        //修改
        Member updateOne = new Member();
        PropertyUtils.copyProperties(updateOne, memberUpdateDTO);
        updateOne.setUpdateTime(new Date());

        int count = memberWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据memberId：" + memberUpdateDTO.getMemberId() + "更新会员失败");
            throw new MallException("更新会员失败,请重试");
        }
        return count;
    }

    /**
     * 根据会员memberId更新会员状态
     *
     * @param memberId
     * @return
     */
    public Integer updateMemberState(Integer memberId) throws Exception {

        //查询会员
        Member updateOne = memberReadMapper.getByPrimaryKey(memberId);

        //修改
        updateOne.setState(Math.abs(updateOne.getState() - 1));
        updateOne.setUpdateTime(new Date());

        int count = memberWriteMapper.updateByPrimaryKeySelective(updateOne);
        AssertUtil.notNullOrZero(count, "更新会员状态失败,请重试");
        return count;
    }

    /**
     * 根据memberId更新会员
     *
     * @param memberUpdatePwdDTO
     * @return
     */
    public Integer updateMember(MemberUpdatePwdDTO memberUpdatePwdDTO) throws Exception {

        //修改
        Member updateOne = new Member();
        PropertyUtils.copyProperties(updateOne, memberUpdatePwdDTO);
        updateOne.setLoginPwd(Md5.getMd5String(memberUpdatePwdDTO.getLoginPwd()));
        updateOne.setUpdateTime(new Date());

        int count = memberWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据memberId：" + memberUpdatePwdDTO.getMemberId() + "更新会员密码失败");
            throw new MallException("更新会员密码失败,请重试");
        }
        return count;
    }

    /**
     * 根据memberId获取会员详情
     *
     * @param memberId memberId
     * @return
     */
    public Member getMemberByMemberId(Integer memberId) {
        return memberReadMapper.getByPrimaryKey(memberId);
    }

    /**
     * 根据memberId获取会员详情-包含非member字段
     *
     * @param memberId memberId
     * @return
     */
    public MemberDetailVO getMember(Integer memberId) {
        //1.查询会员信息
        Member member = memberReadMapper.getByPrimaryKey(memberId);
        if (StringUtil.isEmpty(member.getMemberAvatar()) && StringUtil.isEmpty(member.getWxAvatarImg())) {
            member.setMemberAvatar(stringRedisTemplate.opsForValue().get("default_image_user_portrait"));
        }
        //2.查询非member字段
        //2.1根据会员id查询未使用的优惠券信息
        CouponMemberExample couponExample = new CouponMemberExample();
        couponExample.setMemberId(memberId);
        couponExample.setUseState(CouponConst.USE_STATE_1);
        couponExample.setEffectiveEndAfter(new Date());
        List<CouponMember> couponList = couponMemberModel.getCouponMemberList(couponExample, null);

        //2.2查询bz_order字段
        OrderExample orderExample = new OrderExample();
        orderExample.setMemberId(memberId);
        orderExample.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        List<Order> orderList = orderModel.getOrderList(orderExample, null);

        //客单价
        BigDecimal pstPrice = new BigDecimal("0.00");
        //累计消费金额
        BigDecimal orderAmount = new BigDecimal("0.00");
        //累计消费订单数
        Integer orderNumber = 0;
        //累计退款金额
        BigDecimal refundAmount = new BigDecimal("0.00");
        //累计退款订单数
        Integer refundNumber = 0;

        if (!CollectionUtils.isEmpty(orderList)) {
            for (Order order : orderList) {
                orderAmount = orderAmount.add(order.getOrderAmount());
                if (order.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
                    refundAmount = refundAmount.add(order.getRefundAmount());
                    refundNumber++;
                }
            }
            orderNumber = orderList.size();
            pstPrice = orderAmount.divide(new BigDecimal(orderNumber), 2, BigDecimal.ROUND_HALF_UP);
        }

        //3.组装前端响应实体MemberDetailVO
        MemberDetailVO vo = new MemberDetailVO(member);
        vo.setCouponNumber(couponList.size());
        vo.setCreateTime(CollectionUtils.isEmpty(orderList) ? null : orderList.get(0).getCreateTime());
        vo.setPstPrice(pstPrice);
        vo.setOrderAmount(orderAmount);
        vo.setOrderNumber(orderNumber);
        vo.setRefundAmount(refundAmount);
        vo.setRefundOrderNumber(refundNumber);
        return new MemberDetailVO(member);
    }

    /**
     * 根据条件获取会员列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Member> getMemberList(MemberExample example, PagerInfo pager) {
        List<Member> memberList;
        if (pager != null) {
            pager.setRowsCount(memberReadMapper.countByExample(example));
            memberList = memberReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberList = memberReadMapper.listByExample(example);
        }
        return memberList;
    }

    /**
     * 修改登录密码
     *
     * @param memberUpdateLoginPwdDTO
     * @param memberId
     * @return
     */
    public Integer editLoginPwd(MemberUpdateLoginPwdDTO memberUpdateLoginPwdDTO, Integer memberId) {
        //修改会员表登录密码
        MemberExample example = new MemberExample();
        example.setMemberId(memberId);

        Member member = new Member();
        //登录密码MD5加密
        member.setLoginPwd(Md5.getMd5String(memberUpdateLoginPwdDTO.getLoginPwd()));
        member.setUpdateTime(new Date());
        int count = memberWriteMapper.updateByExampleSelective(member, example);
        AssertUtil.notNullOrZero(count, "修改登录密码失败,请重试");

        return count;
    }


    /**
     * 设置/重置登录密码
     *
     * @param memberAddLoginPwdDTO
     * @param memberId
     * @return
     */
    public Integer addLoginPwd(MemberAddLoginPwdDTO memberAddLoginPwdDTO, Integer memberId) {
        MemberExample example = new MemberExample();
        example.setMemberId(memberId);

        Member member = new Member();
        //登录密码MD5加密
        member.setLoginPwd(Md5.getMd5String(memberAddLoginPwdDTO.getLoginPwd()));
        member.setUpdateTime(new Date());
        int count = memberWriteMapper.updateByExampleSelective(member, example);
        AssertUtil.notNullOrZero(count, "设置/重置登录密码失败,请重试");

        return count;
    }

    /**
     * 设置/重置支付密码
     *
     * @param memberAddPayPwdDTO
     * @param memberId
     * @return
     */
    public Integer addPayPwd(MemberAddPayPwdDTO memberAddPayPwdDTO, Integer memberId) {
        MemberExample example = new MemberExample();
        example.setMemberId(memberId);

        Member member = new Member();
        //密码MD5加密
        member.setPayPwd(Md5.getMd5String(memberAddPayPwdDTO.getPayPwd()));
        member.setUpdateTime(new Date());
        int count = memberWriteMapper.updateByExampleSelective(member, example);
        AssertUtil.notNullOrZero(count, "设置/重置密码失败,请重试");
        return count;
    }


    /**
     * 设置/重置支付密码
     *
     * @param memberUpdatePayPwdDTO
     * @param memberId
     * @return
     */
    public Integer editPayPwd(MemberUpdatePayPwdDTO memberUpdatePayPwdDTO, Integer memberId) {
        MemberExample example = new MemberExample();
        example.setMemberId(memberId);

        Member member = new Member();
        //密码MD5加密
        member.setPayPwd(Md5.getMd5String(memberUpdatePayPwdDTO.getPayPwd()));
        member.setUpdateTime(new Date());
        int count = memberWriteMapper.updateByExampleSelective(member, example);
        AssertUtil.notNullOrZero(count, "设置/重置密码失败,请重试");

        return count;
    }

    /**
     * 获取条件获取会员数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getMemberCount(MemberExample example) {
        return memberReadMapper.countByExample(example);
    }

    /**
     * 会员数量统计
     *
     * @param example
     * @return
     */
    public List<MemberDayDTO> getMemberDayDto(MemberExample example) {
        return memberReadMapper.getMemberDayDto(example);
    }


    /**
     * 批量删除会员
     *
     * @param memberIds
     * @return
     */
    public Integer batchDeleteMember(String memberIds) {
        //组装要删除的条件
        MemberExample example = new MemberExample();
        example.setMemberIdIn(memberIds);

        int count = memberWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据memberIds：" + memberIds + "删除会员失败");
            throw new MallException("删除会员失败,请重试");
        }
        return count;
    }
}
