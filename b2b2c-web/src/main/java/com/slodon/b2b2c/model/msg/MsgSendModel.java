package com.slodon.b2b2c.model.msg;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.beust.jcommander.ParameterException;
import com.slodon.b2b2c.core.constant.SMSConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.sms.YunPianJsonResult;
import com.slodon.b2b2c.core.sms.YunPianSmsSender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消息发送model层
 */
@Slf4j
@Component
public class MsgSendModel {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送验证码短信
     *
     * @param map
     * @return
     */
    @SneakyThrows
    public YunPianJsonResult sendVerifyCode(Map<String, Object> map) {
        //创建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(SMSConst.DEFAULT_RUN_THREAD_NUM);
        if (map.get("mobile") == null) {
            throw new MallException("请指定要发送的手机号码");
        }
        YunPianJsonResult result = (YunPianJsonResult) pool.submit(new SMSCall(map, SMSConst.VERIFY_SMS)).get();
        if (result.getCode() != 0) {
            log.error("短信发送错误[code]:" + result.getCode() + "[msg]:" + result.getMsg());
            switch (result.getCode()) {
                case 22:
                    throw new MallException("每小时获取验证码次数已达上限，请稍后再试");
                case 33:
                    throw new MallException("操作频繁，请稍后再试");
                case 17:
                    throw new MallException("今日获取验证码次数过多,请明日再试");
                default:
                    throw new MallException("短信发送异常，请稍后重试");
            }
        }
        //关闭线程池
        pool.shutdown();
        return result;
    }


    /**
     * 发送通知短信
     *
     *      * 短信发送形式：0-验证码短信；1-通知类短信
     *      *
     *      * 验证码短信：主要用于登录、修改密码等发送的6位随机数，由用户发起调用
     *      * 通知类短信：主要用户发送订单发货、状态变更等等的系统主动通知短信，由平台发起调用
     *
     * @param mobile  手机号
     * @param content 短信内容
     * @param apiKey  短信key
     */
    public void sendSMS(String mobile, String content, String apiKey) {
        if (mobile == null) {
            throw new MallException("请指定要发送的手机号码");
        }
        if (content == null) {
            throw new MallException("请指定短信参数");
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("mobile", mobile);
        map.put("content", content);
        map.put("apiKey", apiKey);
        map.put("app", stringRedisTemplate.opsForValue().get("basic_site_name"));
        new Thread(new SMSThread(map, SMSConst.NOTIFICATION_SMS)).start();
    }

    /**
     * 发送邮件
     *
     * @param toAddress 邮件接收地址
     * @param subject   主题
     * @param content   内容
     */
    public void sendMail(String toAddress, String subject, String content) {
        MailAccount account = new MailAccount();
        account.setHost(stringRedisTemplate.opsForValue().get("notification_email_smtp_host"));
        account.setPort(Integer.parseInt(stringRedisTemplate.opsForValue().get("notification_email_smtp_port")));
        account.setFrom(stringRedisTemplate.opsForValue().get("notification_email_sender_address"));
        account.setUser(stringRedisTemplate.opsForValue().get("notification_email_sender_name"));
        account.setPass(stringRedisTemplate.opsForValue().get("notification_email_sender_password"));
        account.setAuth(true);
        account.setStarttlsEnable(true);
        account.setSslEnable(true);
        account.setDebug(true);
        MailUtil.send(account, toAddress, subject, content, false);
    }

    class SMSCall implements Callable<Object> {
        private Map<String, Object> param;
        private int type;

        SMSCall(Map<String, Object> param, int type) {
            this.param = param;
            this.type = type;
        }

        @Override
        public Object call() {
            YunPianSmsSender yunPianSmsSender = null;
            if (type == SMSConst.VERIFY_SMS) {
                yunPianSmsSender = new YunPianSmsSender(param, SMSConst.VERIFY_SMS);
            }else if (type == SMSConst.NOTIFICATION_SMS) {
                yunPianSmsSender = new YunPianSmsSender(param,SMSConst.NOTIFICATION_SMS);
            }
            if (yunPianSmsSender == null) {
                throw new ParameterException("参数错误");
            }
            return yunPianSmsSender.sendSMS();
        }
    }

    class SMSThread implements Runnable {
        private Map<String, Object> param;
        private int type;

        SMSThread(Map<String, Object> map, int type) {
            this.param = map;
            this.type = type;
        }

        @Override
        public void run() {
            YunPianSmsSender yunPianSmsSender = null;
            if (type == SMSConst.VERIFY_SMS) {
                yunPianSmsSender = new YunPianSmsSender(param, SMSConst.VERIFY_SMS);
            } else if (type == SMSConst.NOTIFICATION_SMS) {
                yunPianSmsSender = new YunPianSmsSender(param, SMSConst.NOTIFICATION_SMS);
            }

            if (yunPianSmsSender == null) {
                throw new ParameterException("参数错误");
            }
            yunPianSmsSender.sendSMS();
        }
    }

}
