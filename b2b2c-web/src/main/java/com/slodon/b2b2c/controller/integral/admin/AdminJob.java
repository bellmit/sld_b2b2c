package com.slodon.b2b2c.controller.integral.admin;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.model.integral.IntegralBillModel;
import com.slodon.b2b2c.model.integral.IntegralESGoodsModel;
import com.slodon.b2b2c.model.integral.IntegralOrderModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   //2.开启定时任务
public class AdminJob {

    @Resource
    private IntegralESGoodsModel integralESGoodsModel;
    @Resource
    private IntegralBillModel integralBillModel;
    @Resource
    private IntegralOrderModel integralOrderModel;

    /**
     * 系统定时更新积分商城es索引
     * 每隔十分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobSearchIntegralES() {
        log.info("jobSearchIntegralES() start");
        try {
            boolean jobResult = integralESGoodsModel.jobCreateIndexesES(DomainUrlUtil.SLD_ES_URL, DomainUrlUtil.SLD_ES_PORT, "job");
            AssertUtil.isTrue(!jobResult, "[jobSearchES] 系统定时任务定时生成积分商城ES索引失败");
        } catch (Exception e) {
            log.error("jobSearchIntegralES()", e);
        }
        log.info("jobSearchIntegralES() end");
    }

    /**
     * 商家结算账单生成定时任务
     * 查询所有商家，每个商家每个结算周期生成一条结算账单
     * 计算周期内商家所有的订单金额合计
     * 每个商家一个事务，某个商家结算时发生错误不影响其他结算
     * 每天0点半执行一次
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void integralBillJobSchedule() {
        log.info("integralBillJobSchedule() start");
        try {
            integralBillModel.billJobSchedule();
        } catch (Exception e) {
            log.error("integralBillJobSchedule()", e);
        }
        log.info("integralBillJobSchedule() end");
    }

    /**
     * 系统自动取消24小时没有付款的积分订单
     * 每天早上七点半执行
     */
    @Scheduled(cron = "0 30 7 * * ?")
    public void jobSystemCancelIntegralOrder() {
        log.info("jobSystemCancelIntegralOrder() start");
        try {
            boolean jobResult = integralOrderModel.jobSystemCancelIntegralOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemCancelIntegralOrder] 定时任务系统自动取消24小时没有付款的积分订单时失败");
        } catch (Exception e) {
            log.error("jobSystemCancelIntegralOrder()", e);
        }
        log.info("jobSystemCancelIntegralOrder() end");
    }

    /**
     * 系统自动完成积分订单
     * 对已发货状态的订单发货时间超过10个自然日(取setting表配置)的订单进行自动完成处理
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobSystemFinishIntegralOrder() {
        log.info("jobSystemFinishIntegralOrder() start");
        try {
            boolean jobResult = integralOrderModel.jobSystemFinishIntegralOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemFinishIntegralOrder] 系统自动完成积分订单时失败");
        } catch (Exception e) {
            log.error("jobSystemFinishIntegralOrder()", e);
        }
        log.info("jobSystemFinishIntegralOrder() end");
    }
}
