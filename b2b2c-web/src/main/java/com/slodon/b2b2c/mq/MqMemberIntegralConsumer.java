package com.slodon.b2b2c.mq;

import com.slodon.b2b2c.core.constant.StarterConfigConst;
import com.slodon.b2b2c.model.member.MemberIntegralLogModel;
import com.slodon.b2b2c.starter.mq.entity.MemberIntegralVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * mq执行会员积分记录操作
 */
@Slf4j
@Component
public class MqMemberIntegralConsumer {

    @Resource
    private MemberIntegralLogModel memberIntegralLogModel;

    /**
     * 存储会员积分日志
     *
     * @param memberIntegralVO
     */
    @RabbitListener(queues = StarterConfigConst.MQ_QUEUE_NAME_MEMBER_INTEGRAL, containerFactory = StarterConfigConst.MQ_FACTORY_NAME_SINGLE_PASS_ERR)
    public void adminLogConsumer(MemberIntegralVO memberIntegralVO) {
        try {
            memberIntegralLogModel.memberSendIntegral(memberIntegralVO);
        } catch (Exception e) {
            log.error("保存会员积分日志失败：", e);
        }
    }

}
