package com.slodon.b2b2c.mq;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.constant.CommonConst;
import com.slodon.b2b2c.core.constant.MemberWxSignConst;
import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.constant.StarterConfigConst;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.member.example.MemberWxsignExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberWxsign;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.member.MemberWxsignModel;
import com.slodon.b2b2c.model.msg.*;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.msg.example.MemberSettingExample;
import com.slodon.b2b2c.msg.example.StoreSettingExample;
import com.slodon.b2b2c.msg.pojo.*;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * mq执行发送消息
 */
@Slf4j
@Component
public class MqSendMsgConsumer {

    @Resource
    private MemberTplModel memberTplModel;
    @Resource
    private MemberSettingModel memberSettingModel;
    @Resource
    private MsgSendModel msgSendModel;
    @Resource
    private MemberReceiveModel memberReceiveModel;
    @Resource
    private StoreTplModel storeTplModel;
    @Resource
    private StoreSettingModel storeSettingModel;
    @Resource
    private StoreReceiveModel storeReceiveModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberWxsignModel memberWxsignModel;
    @Resource
    private VendorModel vendorModel;
    @Resource
    private RestTemplate restTemplate;

    /**
     * 发送会员消息
     *
     * @param messageSendVO
     */
    @RabbitListener(queues = StarterConfigConst.MQ_QUEUE_NAME_MEMBER_MSG, containerFactory = StarterConfigConst.MQ_FACTORY_NAME_SINGLE_PASS_ERR)
    public void memberMsgConsumer(MessageSendVO messageSendVO) {
        try {
            //日期格式转换
            String now = TimeUtil.getDateTimeString(new Date());
            //网站名称
            String siteName = stringRedisTemplate.opsForValue().get("basic_site_name");
            //短信key
            String smsKey = stringRedisTemplate.opsForValue().get("notification_sms_key");
            //短信签名
            String smsSign = stringRedisTemplate.opsForValue().get("notification_sms_signature");

            //获取对应的模版
            MemberTpl memberTpl = memberTplModel.getMemberTplByTplCode(messageSendVO.getTplType());
            if (memberTpl == null) {
                return;
            }
            //获取配置设置表
            MemberSettingExample example = new MemberSettingExample();
            example.setTplCode(messageSendVO.getTplType());
            example.setMemberId(new Long(messageSendVO.getReceiveId()).intValue());
            List<MemberSetting> memberSettingList = memberSettingModel.getMemberSettingList(example, null);
            if (CollectionUtils.isEmpty(memberSettingList)) {
                return;
            }
            MemberSetting memberSetting = memberSettingList.get(0);
            //获取对应的会员
            Member member = memberModel.getMemberByMemberId(new Long(messageSendVO.getReceiveId()).intValue());
            if (null == member) {
                return;
            }

            if (memberSetting.getIsReceive().equals(MsgConst.IS_OPEN_SWITCH_YES)) {
                /**----------------------------------------发邮件-----------------------------------------**/
                if (memberTpl.getEmailSwitch().equals(MsgConst.IS_OPEN_SWITCH_YES) && !StringUtil.isEmpty(member.getMemberEmail())) {
                    JSONObject jsonObjectForEmail = JSONObject.parseObject(memberTpl.getEmailContent());
                    String subject = jsonObjectForEmail.getString("email_subject");
                    String content = jsonObjectForEmail.getString("email_content");
                    //内容替换
                    subject = subject.replace("{$siteName}", siteName);
                    content = content.replace("{$siteName}", siteName).replace("{$" + messageSendVO.getDateName() + "}", now);
                    //除了时间和网站名，其余设置的属性的替换
                    if (!CollectionUtils.isEmpty(messageSendVO.getPropertyList())) {
                        for (MessageSendProperty property : messageSendVO.getPropertyList()) {
                            content = content.replace("{$" + property.getPropertyName() + "}", property.getPropertyValue());
                        }
                    }
                    //发邮件
                    msgSendModel.sendMail(member.getMemberEmail(), subject, content);
                }
                /**----------------------------------------发邮件结束-----------------------------------------**/

                /**----------------------------------------发短信--------------------------------------------**/
                if (memberTpl.getSmsSwitch().equals(MsgConst.IS_OPEN_SWITCH_YES) && !StringUtil.isEmpty(member.getMemberMobile())) {
                    //替换内容
                    String smsContent = memberTpl.getSmsContent().replace("{$siteName}", smsSign).replace("{$" + messageSendVO.getDateName() + "}", now);
                    //除了时间和网站名，其余设置的属性的替换
                    if (!CollectionUtils.isEmpty(messageSendVO.getPropertyList())) {
                        for (MessageSendProperty property : messageSendVO.getPropertyList()) {
                            smsContent = smsContent.replace("{$" + property.getPropertyName() + "}", property.getPropertyValue());
                        }
                    }
                    //发短信
                    msgSendModel.sendSMS(member.getMemberMobile(), smsContent, smsKey);
                }

                /**----------------------------------------发短信结束-------------------------------------------**/

                /**----------------------------------------发站内信开始-----------------------------------------**/
                if (memberTpl.getMsgSwitch() == MsgConst.IS_OPEN_SWITCH_YES) {
                    String msgContent = memberTpl.getMsgContent().replace("{$" + messageSendVO.getDateName() + "}", now);
                    //除了时间和网站名，其余设置的属性的替换
                    if (!CollectionUtils.isEmpty(messageSendVO.getPropertyList())) {
                        for (MessageSendProperty property : messageSendVO.getPropertyList()) {
                            msgContent = msgContent.replace("{$" + property.getPropertyName() + "}", property.getPropertyValue());
                        }
                    }
                    MemberReceive memberReceive = new MemberReceive();
                    memberReceive.setMsgContent(msgContent);
                    memberReceive.setTplCode(memberTpl.getTplCode());
                    memberReceive.setMemberId(member.getMemberId());
                    memberReceive.setMsgState(MsgConst.MSG_STATE_UNREAD);
                    memberReceive.setMsgLinkInfo(messageSendVO.getMsgLinkInfo());
                    memberReceive.setMsgSendTime(new Date());
                    memberReceive.setSource(MsgConst.SOURCE_MEMBER);
                    memberReceive.setPushId(0);
                    memberReceiveModel.saveMemberReceive(memberReceive);
                }
                /**----------------------------------------发站内信结束-----------------------------------------**/

                /**----------------------------------------微信推送开始-----------------------------------------**/
                if (memberTpl.getWxSwitch() == MsgConst.IS_OPEN_SWITCH_YES) {
                    String memberDevOpenId = this.getWxDevOpenId(member);
                    if (!StringUtils.isEmpty(memberDevOpenId)) {
                        JSONObject jsonObjectForMsg = JSONObject.parseObject(memberTpl.getWxContent());
                        String templateId = jsonObjectForMsg.getString("templateId");
                        WXMsgSenderInfo wxMsgSenderInfo = new WXMsgSenderInfo();
                        // 设置模板id
                        wxMsgSenderInfo.setTemplateId(templateId);
                        // 设置接收用户openId
                        wxMsgSenderInfo.setToUser(memberDevOpenId);
                        //点击详情跳转的地址
                        String url = "";
                        //设置模板dada参数
                        if (!CollectionUtils.isEmpty(messageSendVO.getWxPropertyList())) {
                            for (MessageSendProperty property : messageSendVO.getWxPropertyList()) {
                                if ("url".equals(property.getPropertyName())) {
                                    url = property.getPropertyValue();
                                } else if ("remark".equals(property.getPropertyName())) {
                                    wxMsgSenderInfo.getData().put(property.getPropertyName(), wxMsgSenderInfo.initData(property.getPropertyValue(), ""));
                                } else {
                                    wxMsgSenderInfo.getData().put(property.getPropertyName(), wxMsgSenderInfo.initData(property.getPropertyValue(), "#0000EE"));
                                }
                            }
                        }
                        wxMsgSenderInfo.setUrl(url);
                        String accessToken = this.getWxAccessToken();
                        log.info("发送微信模板消息内容: " + JSONObject.toJSON(wxMsgSenderInfo));
                        //调用微信接口，发送模板消息
                        WxMsgResult wxMsgResult = restTemplate.postForObject(String.format(MsgConst.PUSH_MESSAGE_URL, accessToken), wxMsgSenderInfo, WxMsgResult.class);
                        log.info("发送微信模板消息: " + wxMsgResult);
                    }
                }
            }
        } catch (Exception e) {
            log.error("[MqSendMsgConsumer][memberMsgConsumer]发送消息出现异常:", e);
        }
    }

    /**
     * 获取公众号accessToken
     *
     * @return
     */
    private String getWxAccessToken(){
        String accessToken = stringRedisTemplate.opsForValue().get(CommonConst.WX_ACCESS_TOKEN);
        //如果为空，设置accessToken
        if (StringUtil.isEmpty(accessToken)) {
            //获取配置信息
            String appsecret = stringRedisTemplate.opsForValue().get("login_wx_dev_appsecret");
            String appid = stringRedisTemplate.opsForValue().get("login_wx_dev_appid");
            //获取accessToken
            String accessTokenResp = HttpUtil.get(MsgConst.WXPAY_ACCESS_TOKEN.concat("&appid=") + appid + "&secret=" + appsecret);
            JSONObject jsonObject = JSONObject.parseObject(accessTokenResp);
            accessToken = jsonObject.getString("access_token");
            stringRedisTemplate.opsForValue().set(CommonConst.WX_ACCESS_TOKEN, accessToken, 2 * 60 * 60, TimeUnit.SECONDS);
        }
        return accessToken;
    }

    /**
     * 获取会员授权公众号的openid
     * @param member
     * @return
     */
    private String getWxDevOpenId(Member member){
        if (StringUtils.isEmpty(member.getWxUnionid())){
            //用户微信未授权
            return null;
        }
        String access_token = this.getWxAccessToken();//获取token
        String getOpenIdsUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + access_token;//获取公众号下所有的openid
        String openIdsResp = HttpUtil.get(getOpenIdsUrl);
        List<String> openIds = JSON.parseObject(openIdsResp).getJSONObject("data").getObject("openid", List.class);
        if (CollectionUtils.isEmpty(openIds)){
            //公众号下没有关注的用户
            return null;
        }
        //构造用户列表
        List<ModelMap> userList = new ArrayList<>();
        openIds.forEach(openid -> {
            ModelMap openIdMap = new ModelMap();
            openIdMap.put("openid",openid);
            openIdMap.put("lang","zh_CN");
            userList.add(openIdMap);
        });
        ModelMap userMap = new ModelMap();
        userMap.put("user_list",userList);

        //获取用户对应的uninoid
        String getUserInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=" + access_token;//获取用户信息的url
        String userInfoResp = HttpUtil.post(getUserInfoUrl,JSON.toJSONString(userMap));
        JSONArray userInfoList = JSON.parseObject(userInfoResp).getJSONArray("user_info_list");
        if (CollectionUtils.isEmpty(userInfoList)) {
            //获取用户信息失败
            return null;
        }

        //遍历用户信息，根据会员unionid获取公众号的openid
        for (Object o : userInfoList) {
            JSONObject userInfo = (JSONObject) o;
            if ("1".equals(userInfo.getString("subscribe")) && member.getWxUnionid().equals(userInfo.getString("unionid"))) {
                return userInfo.getString("openid");
            }
        }
        return null;
    }

    /**
     * 发送商户消息
     *
     * @param messageSendVO
     */
    @RabbitListener(queues = StarterConfigConst.MQ_QUEUE_NAME_SELLER_MSG, containerFactory = StarterConfigConst.MQ_FACTORY_NAME_SINGLE_PASS_ERR)
    public void sellerMsgConsumer(MessageSendVO messageSendVO) {
        try {
            //日期格式转换
            String now = TimeUtil.getDateTimeString(new Date());
            //网站名称
            String siteName = stringRedisTemplate.opsForValue().get("basic_site_name");
            //短信key
            String smsKey = stringRedisTemplate.opsForValue().get("notification_sms_key");
            //短信签名
            String smsSign = stringRedisTemplate.opsForValue().get("notification_sms_signature");

            //获取对应的模版
            StoreTpl storeTpl = storeTplModel.getStoreTplByTplCode(messageSendVO.getTplType());
            if (storeTpl == null) {
                return;
            }
            //获取配置设置表
            StoreSettingExample storeSettingExample = new StoreSettingExample();
            storeSettingExample.setStoreId(messageSendVO.getReceiveId());
            storeSettingExample.setTplCode(messageSendVO.getTplType());
            List<StoreSetting> storeSettings = storeSettingModel.getStoreSettingList(storeSettingExample, null);
            if (CollectionUtils.isEmpty(storeSettings)) {
                return;
            }

            //有多少商户员工有权限就给多少商户员工发消息
            for (StoreSetting storeSetting : storeSettings) {
                //获取对应的商户员工
                Vendor vendor = vendorModel.getVendorByVendorId(storeSetting.getVendorId());
                if (null == vendor) {
                    continue;
                }
                if (storeSetting.getIsReceive().equals(MsgConst.IS_OPEN_SWITCH_YES)) {
                    /**----------------------------------------发邮件-----------------------------------------**/
                    if (storeTpl.getEmailSwitch().equals(MsgConst.IS_OPEN_SWITCH_YES) && !StringUtil.isEmpty(vendor.getVendorEmail())) {
                        JSONObject jsonObject = JSONObject.parseObject(storeTpl.getEmailContent());
                        String subject = jsonObject.getString("email_subject");
                        String content = jsonObject.getString("email_content");
                        //内容替换
                        subject = subject.replace("{$siteName}", siteName);
                        content = content.replace("{$siteName}", siteName).replace("{$" + messageSendVO.getDateName() + "}", now);
                        //除了时间和网站名，其余设置的属性的替换
                        if (!CollectionUtils.isEmpty(messageSendVO.getPropertyList())) {
                            for (MessageSendProperty property : messageSendVO.getPropertyList()) {
                                content = content.replace("{$" + property.getPropertyName() + "}", property.getPropertyValue());
                            }
                        }
                        //发邮件
                        msgSendModel.sendMail(vendor.getVendorEmail(), subject, content);
                    }
                    /**----------------------------------------发邮件结束-----------------------------------------**/

                    /**----------------------------------------发短信--------------------------------------------**/
                    if (storeTpl.getSmsSwitch().equals(MsgConst.IS_OPEN_SWITCH_YES) && !StringUtil.isEmpty(vendor.getVendorMobile())) {
                        //替换内容
                        String smsContent = storeTpl.getSmsContent().replace("{$siteName}", smsSign).replace("{$" + messageSendVO.getDateName() + "}", now);
                        //除了时间和网站名，其余设置的属性的替换
                        if (!CollectionUtils.isEmpty(messageSendVO.getPropertyList())) {
                            for (MessageSendProperty property : messageSendVO.getPropertyList()) {
                                smsContent = smsContent.replace("{$" + property.getPropertyName() + "}", property.getPropertyValue());
                            }
                        }
                        //发短信
                        msgSendModel.sendSMS(vendor.getVendorMobile(), smsContent, smsKey);
                    }
                    /**----------------------------------------发短信结束-------------------------------------------**/

                    /**----------------------------------------发站内信开始-----------------------------------------**/
                    if (storeTpl.getMsgSwitch().equals(MsgConst.IS_OPEN_SWITCH_YES)) {
                        String msgContent = storeTpl.getMsgContent().replace("{$" + messageSendVO.getDateName() + "}", now);
                        //除了时间和网站名，其余设置的属性的替换
                        if (!CollectionUtils.isEmpty(messageSendVO.getPropertyList())) {
                            for (MessageSendProperty property : messageSendVO.getPropertyList()) {
                                msgContent = msgContent.replace("{$" + property.getPropertyName() + "}", property.getPropertyValue());
                            }
                        }
                        StoreReceive storeReceive = new StoreReceive();
                        storeReceive.setMsgContent(msgContent);
                        storeReceive.setTplCode(storeTpl.getTplCode());
                        storeReceive.setVendorId(vendor.getVendorId());
                        storeReceive.setStoreId(vendor.getStoreId());
                        storeReceive.setMsgState(MsgConst.MSG_STATE_UNREAD);
                        storeReceive.setMsgLinkInfo(messageSendVO.getMsgLinkInfo());
                        storeReceive.setMsgSendTime(new Date());
                        storeReceive.setSource(MsgConst.SOURCE_STORE);
                        storeReceive.setPushId(0);
                        storeReceiveModel.saveStoreReceive(storeReceive);
                    }
                    /**----------------------------------------发站内信结束-----------------------------------------**/
                }
            }
        } catch (Exception e) {
            log.error("[MqSendMsgConsumer][sellerMsgConsumer]发送消息出现异常:", e);
        }
    }
}
