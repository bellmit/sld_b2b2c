package com.slodon.b2b2c.model.promotion;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.constant.SignConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.promotion.SignActivityReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SignLogReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SignRecordReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SignActivityWriteMapper;
import com.slodon.b2b2c.promotion.example.SignActivityExample;
import com.slodon.b2b2c.promotion.example.SignLogExample;
import com.slodon.b2b2c.promotion.example.SignRecordExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.SignActivity;
import com.slodon.b2b2c.promotion.pojo.SignLog;
import com.slodon.b2b2c.promotion.pojo.SignRecord;
import com.slodon.b2b2c.vo.promotion.MemberSignDetailVO;
import com.slodon.b2b2c.vo.promotion.SignActivityStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class SignActivityModel {

    @Resource
    private SignActivityReadMapper signActivityReadMapper;
    @Resource
    private SignRecordReadMapper signRecordReadMapper;
    @Resource
    private SignLogReadMapper signLogReadMapper;

    @Resource
    private SignActivityWriteMapper signActivityWriteMapper;

    @Resource
    private SignLogModel signLogModel;
    @Resource
    private CouponModel couponModel;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 新增签到活动表
     *
     * @param signActivity
     * @return
     */
    public Integer saveSignActivity(SignActivity signActivity) {
        //判断活动时间是否重叠
        AssertUtil.isTrue(!this.checkActivityTime(signActivity.getSignActivityId(), signActivity.getStartTime(),
                signActivity.getEndTime()), "当前选择的时间段内已有活动，请选择其他时间段");

        if (StringUtil.isNullOrZero(signActivity.getContinueNum())) {
            signActivity.setContinueNum(0);
            signActivity.setBonusIntegral(0);
            signActivity.setBonusVoucher(0);
        }
        signActivity.setState(SignConst.SIGN_STATE_0);
        signActivity.setCreateTime(new Date());
        int count = signActivityWriteMapper.insert(signActivity);
        if (count == 0) {
            throw new MallException("添加签到活动表失败，请重试");
        }
        return count;
    }

    /**
     * 判断活动周期是否与已有的活动重叠
     *
     * @param signActivityId
     * @param startTime
     * @param endTime
     * @return
     */
    public boolean checkActivityTime(Integer signActivityId, Date startTime, Date endTime) {
        SignActivityExample example = new SignActivityExample();
        example.setCheckStartTime(startTime);
        example.setCheckEndTime(endTime);
        example.setSignActivityIdNotEquals(signActivityId);
        example.setState(SignConst.SIGN_STATE_1);   //校验开启状态中的活动时间
        List<SignActivity> list = signActivityReadMapper.listByExample(example);
        return CollectionUtils.isEmpty(list);
    }

    /**
     * 根据signActivityId删除签到活动表
     *
     * @param signActivityId signActivityId
     * @return
     */
    public Integer deleteSignActivity(Integer signActivityId) {
        if (StringUtils.isEmpty(signActivityId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = signActivityWriteMapper.deleteByPrimaryKey(signActivityId);
        if (count == 0) {
            log.error("根据signActivityId：" + signActivityId + "删除签到活动表失败");
            throw new MallException("删除签到活动表失败,请重试");
        }
        return count;
    }

    /**
     * 根据signActivityId更新签到活动表
     *
     * @param signActivity
     * @return
     */
    public Integer updateSignActivity(SignActivity signActivity) {
        if (StringUtils.isEmpty(signActivity.getSignActivityId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = signActivityWriteMapper.updateByPrimaryKeySelective(signActivity);
        if (count == 0) {
            log.error("根据signActivityId：" + signActivity.getSignActivityId() + "更新签到活动表失败");
            throw new MallException("更新签到活动表失败,请重试");
        }
        return count;
    }

    /**
     * 根据signActivityId获取签到活动表详情
     *
     * @param signActivityId signActivityId
     * @return
     */
    public SignActivity getSignActivityBySignActivityId(Integer signActivityId) {
        return signActivityReadMapper.getByPrimaryKey(signActivityId);
    }

    /**
     * 根据条件获取签到活动表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SignActivity> getSignActivityList(SignActivityExample example, PagerInfo pager) {
        List<SignActivity> signActivityList;
        if (pager != null) {
            pager.setRowsCount(signActivityReadMapper.countByExample(example));
            signActivityList = signActivityReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            signActivityList = signActivityReadMapper.listByExample(example);
        }
        return signActivityList;
    }

    /**
     * 封装签到统计
     *
     * @param signActivity
     * @return
     */
    public SignActivityStatisticsVO getStatisticsVO(SignActivity signActivity) {
        SignActivityStatisticsVO vo = new SignActivityStatisticsVO();
        vo.setSignActivityId(signActivity.getSignActivityId());
        vo.setStartTime(TimeUtil.getDateTimeString(signActivity.getStartTime()));

        //判断活动是否进行中
        if (signActivity.getEndTime().after(new Date())) {
            vo.setEndTime("进行中");
        } else {
            vo.setEndTime(TimeUtil.getDateTimeString(signActivity.getEndTime()));
        }

        //签到用户数
        SignRecordExample recordExample = new SignRecordExample();
        recordExample.setActivityId(signActivity.getSignActivityId());
        vo.setMemberNum(signRecordReadMapper.countByExample(recordExample));

        //新用户数
        int newMemberNum = 0;
        List<SignRecord> recordList = signRecordReadMapper.listByExample(recordExample);
        if (!CollectionUtils.isEmpty(recordList)) {
            SignRecordExample example2 = new SignRecordExample();
            for (SignRecord signRecord : recordList) {
                //根据memberId查询签到记录
                example2.setMemberId(signRecord.getMemberId());
                List<SignRecord> records = signRecordReadMapper.listByExample(example2);
                if (records.get(records.size() - 1).getActivityId().equals(signActivity.getSignActivityId())) {
                    //最早一条签到记录的活动id为当前循环的活动id，说明此用户在当前活动第一次签到
                    newMemberNum += 1;
                }
            }
        }
        vo.setNewMemberNum(newMemberNum);

        //总签到次数
        SignLogExample logsExample = new SignLogExample();
        logsExample.setSignActivityId(signActivity.getSignActivityId());
        vo.setTotalSign(signLogReadMapper.countByExample(logsExample));

        //新用户占比
        if (vo.getMemberNum() == 0) {
            vo.setNewMemberRate("-");
        } else {
            //四舍五入，格式为0.00%
            vo.setNewMemberRate(String.format("%.2f", (vo.getNewMemberNum() / (double) vo.getMemberNum()) * 100) + "%");
        }
        return vo;
    }


    /**
     * 封装会员签到详情
     *
     * @param memberId
     * @return
     */
    public MemberSignDetailVO getMemberSign(Integer memberId) throws Exception {
        MemberSignDetailVO vo = new MemberSignDetailVO();
        //正在进行的活动
        SignActivityExample signActivityExample = new SignActivityExample();
        signActivityExample.setStartTimeBefore(new Date());
        signActivityExample.setEndTimeAfter(new Date());
        signActivityExample.setState(SignConst.SIGN_STATE_1);
        List<SignActivity> signActivityList = getSignActivityList(signActivityExample, null);
        AssertUtil.isTrue(CollectionUtils.isEmpty(signActivityList), "签到活动不可用");
        //不为空 正在进行的签到活动
        SignActivity signActivity = signActivityList.get(0);
        BeanUtils.copyProperties(signActivity, vo);
        if (!StringUtil.isNullOrZero(signActivity.getBonusVoucher())) {
            //设置优惠券名称
            Coupon coupon = couponModel.getCouponByCouponId(signActivity.getBonusVoucher());
            AssertUtil.notNull(coupon, "获取优惠券信息为空，请重试");
            vo.setBonusVoucherName(coupon.getCouponName());
        }

        //判断今日是否签到，设置今天获得的签到奖励
        SignLogExample signLogExample = new SignLogExample();
        signLogExample.setMemberId(memberId);
        signLogExample.setSignTimeAfter(TimeUtil.getDayStartFormatYMDHMS(new Date()));
        signLogExample.setSignTimeBefore(TimeUtil.getDayEndFormatYMDHMS(new Date()));
        signLogExample.setSignActivityId(signActivity.getSignActivityId());
        List<SignLog> signLogList = signLogModel.getSignLogList(signLogExample, null);
        if (CollectionUtils.isEmpty(signLogList)) {
            //今日未签到
            vo.setIsSign(SignConst.IS_SIGN_1);
        } else {
            //今天已签到
            SignLog signLog = signLogList.get(0);
            vo.setIsSign(SignConst.IS_SIGN_2);
            vo.setBonusIntegralToday(signLog.getBonusIntegral());
            if (!StringUtil.isNullOrZero(signLog.getBonusVoucher())) {
                //设置今日获取的优惠券名称
                Coupon coupon = couponModel.getCouponByCouponId(signActivity.getBonusVoucher());
                AssertUtil.notNull(coupon, "获取优惠券信息为空，请重试");
                vo.setBonusVoucherNameToday(coupon.getCouponName());
            }
        }

        //获取模板配置信息
        if (!StringUtil.isEmpty(signActivity.getTemplateJson())) {
            JSONObject template = JSONObject.parseObject(signActivity.getTemplateJson());
            JSONObject bgInfo = template.getJSONObject("bgInfo");
//            JSONObject share = template.getJSONObject("share");
            String share = template.getString("pageShareFlag");
            JSONObject bottom = template.getJSONObject("bottom");
            String pageTipFlag = template.getString("pagetipFlag");

            //封装模板配置信息到vo
            String sldImageUrl = stringRedisTemplate.opsForValue().get("sld_image_url");
//            String sldImageUrl = settingModel.getSettingByName("sld_image_url").getValue();
            if (bgInfo != null) {
                //背景设置开启
                vo.getBgInfo().put("pageName", bgInfo.getString("pageName"));
                vo.getBgInfo().put("bgColor", bgInfo.getString("bgColor"));
                vo.getBgInfo().put("bgImg", sldImageUrl + bgInfo.getString("bgImg"));
            } else {
                //背景设置未开启，取默认配置
            }
            if ("true".equals(share)) {
                //分享配置开启
                JSONObject shareData = template.getJSONObject("shareData");
                vo.getShare().put("shareTitle", shareData.getString("shareTitle"));
                vo.getShare().put("shareDesc", shareData.getString("shareDesc"));
                String shareImgPath = sldImageUrl + shareData.getString("shareImgPath");
                if (!StringUtils.isEmpty(shareImgPath)){
                    vo.getShare().put("shareImgPath", sldImageUrl + shareImgPath);
                }
                String shareImgUrl = shareData.getString("shareImgUrl");
                if (!StringUtils.isEmpty(shareImgUrl)){
                    vo.getShare().put("shareImgUrl", shareImgUrl);
                }
            } else {
                //分享配置未开启，取默认配置
            }
            if ("true".equals(pageTipFlag)) {
                //提醒配置开启
                JSONObject tipData = template.getJSONObject("tipData");
                String imgPath = sldImageUrl + tipData.getString("imgPath");
                if (!StringUtils.isEmpty(imgPath)){
                    vo.getNotice().put("imgPath", sldImageUrl + imgPath);
                }
                String imgUrl = tipData.getString("imgUrl");
                if (!StringUtils.isEmpty(imgUrl)){
                    vo.getNotice().put("imgUrl", imgUrl);
                }
            } else {
                //提醒配置未开启，取默认配置
            }
            if (bottom != null) {
                //底部装修配置开启
                vo.getBottom().put("image", sldImageUrl + bottom.getString("image"));
            } else {
                //底部装修配置未开启，取默认配置
            }
        }
        //获取会员签到记录信息
        SignRecordExample signRecordExample = new SignRecordExample();
        signRecordExample.setMemberId(memberId);
        signRecordExample.setActivityId(signActivity.getSignActivityId());
        List<SignRecord> signRecordList = signRecordReadMapper.listByExample(signRecordExample);
        //按照活动周期循环
        //循环周期待定
        int start = TimeUtil.countDate(signActivity.getStartTime());//循环开始
        int end = TimeUtil.countDate(signActivity.getEndTime());//循环限制条件
        //记录某一天的签到信息
        ArrayList<MemberSignDetailVO.memberSignInfo> memberSignInfoList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            MemberSignDetailVO.memberSignInfo memberSignInfo = new MemberSignDetailVO.memberSignInfo();
            memberSignInfo.setDate(TimeUtil.getDateStringByApart(i));
            //签到状态
            int signRecordState;
            if (CollectionUtils.isEmpty(signRecordList)) {
                //该会员在当前活动没有签到记录
//                      //签到状态：1-未签到，2-已签到，3-待签到
                if (i < 0) {
                    signRecordState = SignConst.SIGN_RECORD_STATE_1;
                } else {
                    signRecordState = SignConst.SIGN_RECORD_STATE_3;
                }
            } else {
                SignRecord signRecord = signRecordList.get(0);
                //该会员在当前活动有签到记录
                //会员签到记录信息
                String record = Long.toBinaryString(signRecord.getMask());
                //获取当前循环日期的签到记录，true表示已签到
                boolean isSign = ("" + SignConst.SIGN_RECORD_STATE_1).equals(record.charAt(i - start + 1) + "");
                if (i > 0 || (i == 0 && !isSign)) {
                    //待签到
                    signRecordState = SignConst.SIGN_RECORD_STATE_3;
                } else if (isSign) {
                    //已签到
                    signRecordState = SignConst.SIGN_RECORD_STATE_2;
                } else {
                    signRecordState = SignConst.SIGN_RECORD_STATE_1;
                }
            }
            memberSignInfo.setSignRecordState(signRecordState);
            memberSignInfoList.add(memberSignInfo);
        }
        vo.setMemberSignInfoList(memberSignInfoList);
        return vo;
    }
}