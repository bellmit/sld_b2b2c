package com.slodon.b2b2c.controller.job;

import com.slodon.b2b2c.business.example.OrderAfterServiceExample;
import com.slodon.b2b2c.business.pojo.OrderAfterService;
import com.slodon.b2b2c.business.pojo.OrderReturn;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.MemberTplConst;
import com.slodon.b2b2c.core.constant.StoreTplConst;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.business.ComplainModel;
import com.slodon.b2b2c.model.business.OrderAfterServiceModel;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderReplacementModel;
import com.slodon.b2b2c.model.goods.ESGoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.CouponMemberModel;
import com.slodon.b2b2c.model.promotion.LadderGroupModel;
import com.slodon.b2b2c.model.promotion.SeckillStageProductModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.system.BillModel;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProductBindMember;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.*;

@Slf4j
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   //2.开启定时任务
public class ScheduleTaskController {

    //3.添加定时任务
//    @Scheduled(cron = "0/5 * * * * ?")
//    private void configureTasks() {
//        System.out.println("执行静态定时任务时间: " + LocalDateTime.now());
//    }

    @Resource
    private BillModel billModel;
    @Resource
    private OrderModel orderModel;
    @Resource
    private ESGoodsModel esGoodsModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private OrderReplacementModel orderReplacementModel;
    @Resource
    private ComplainModel complainModel;
    @Resource
    private CouponMemberModel couponMemberModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private OrderAfterServiceModel orderAfterServiceModel;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private SeckillStageProductModel seckillStageProductModel;
    @Resource
    private LadderGroupModel ladderGroupModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 商家结算账单生成定时任务
     * 查询所有商家，每个商家每个结算周期生成一条结算账单
     * 计算周期内商家所有的订单金额合计
     * 计算所有订单下订单货品明细的佣金
     * 计算周期内发生的退货退款（当前周期结算的订单如果在下个结算周期才退款，退款结算在下个周期计算）
     * 每个商家一个事务，某个商家结算时发生错误不影响其他结算
     * 每天0点半执行一次
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void billJobSchedule() {
        log.info("billJobSchedule() start");
        try {
            billModel.billJobSchedule();
        } catch (Exception e) {
            log.error("billJobSchedule()", e);
        }
        log.info("billJobSchedule() end");
    }

    /**
     * 系统自动完成订单
     * 对已发货状态的订单发货时间超过10个自然日(取setting表配置)的订单进行自动完成处理
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobSystemFinishOrder() {
        log.info("jobSystemFinishOrder() start");
        try {
            boolean jobResult = orderModel.jobSystemFinishOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemFinishOrder] 系统自动完成订单时失败");
        } catch (Exception e) {
            log.error("jobSystemFinishOrder()", e);
        }
        log.info("jobSystemFinishOrder() end");
    }

    /**
     * 系统定时更新es索引
     * 每隔十分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobSearchES() {
        log.info("jobSearchES() start");
        try {
            boolean jobResult = esGoodsModel.jobCreateIndexesES(DomainUrlUtil.SLD_ES_URL, DomainUrlUtil.SLD_ES_PORT, "job");
            AssertUtil.isTrue(!jobResult, "[jobSearchES] 系统定时任务定时生成ES索引失败");
        } catch (Exception e) {
            log.error("jobSearchES()", e);
        }
        log.info("jobSearchES() end");
    }

    /**
     * 系统定时更新店铺商品总销量、店铺商品总浏览量
     * 每隔十分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobSetStoreSalesAndLookVolume() {
        log.info("jobSetStoreSalesAndLookVolume() start");
        try {
            boolean jobResult = storeModel.jobSetStoreSalesAndLookVolume();
            AssertUtil.isTrue(!jobResult, "[jobSetStoreSalesAndLookVolume] 定时更新店铺商品总销量、总浏览量失败");
        } catch (Exception e) {
            log.error("jobSetStoreSalesAndLookVolume()", e);
        }
        log.info("jobSetStoreSalesAndLookVolume() end");
    }

    /**
     * 定时任务设定商家的评分，用户评论各项求平均值设置为商家各项的综合评分
     * 每天早上七点执行
     */
    @Scheduled(cron = "0 0 7 * * ?")
    public void jobSetStoreScore() {
        log.info("jobSetStoreScore() start");
        try {
            boolean jobResult = storeModel.jobSetStoreScore();
            AssertUtil.isTrue(!jobResult, "[jobSetStoreScore] 定时任务设定商家的评分时失败");
        } catch (Exception e) {
            log.error("jobSetStoreScore()", e);
        }
        log.info("jobSetStoreScore() end");
    }

    /**
     * 系统自动取消24小时没有付款订单
     * 每天早上七点半执行
     */
    @Scheduled(cron = "0 30 7 * * ?")
    public void jobSystemCancelOrder() {
        log.info("jobSystemCancelOrder() start");
        try {
            boolean jobResult = orderModel.jobSystemCancelOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemCancelOrder] 定时任务系统自动取消24小时没有付款订单时失败");
        } catch (Exception e) {
            log.error("jobSystemCancelOrder()", e);
        }
        log.info("jobSystemCancelOrder() end");
    }

    /**
     * 系统自动取消没有付款的秒杀订单
     * 每隔五分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void jobSystemCancelSeckillOrder() {
        log.info("jobSystemCancelSeckillOrder() start");
        try {
            boolean jobResult = orderModel.jobSystemCancelSeckillOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemCancelSeckillOrder] 定时任务系统自动取消没有付款秒杀订单时失败");
        } catch (Exception e) {
            log.error("jobSystemCancelSeckillOrder()", e);
        }
        log.info("jobSystemCancelSeckillOrder() end");
    }

    /**
     * 系统自动取消没有付款的阶梯团订单
     * 每隔五分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void jobSystemCancelLadderGroupOrder() {
        log.info("jobSystemCancelLadderGroupOrder() start");
        try {
            boolean jobResult = orderModel.jobSystemCancelLadderGroupOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemCancelLadderGroupOrder] 定时任务系统自动取消没有付款阶梯团订单时失败");
        } catch (Exception e) {
            log.error("jobSystemCancelLadderGroupOrder()", e);
        }
        log.info("jobSystemCancelLadderGroupOrder() end");
    }

    /**
     * 系统自动取消没有付款的阶梯团尾款订单
     * 每隔五分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void jobSystemCancelLadderGroupBalanceOrder() {
        log.info("jobSystemCancelLadderGroupBalanceOrder() start");
        try {
            boolean jobResult = orderModel.jobSystemCancelLadderGroupBalanceOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemCancelLadderGroupBalanceOrder] 定时任务系统自动取消没有付尾款阶梯团尾款订单时失败");
        } catch (Exception e) {
            log.error("jobSystemCancelLadderGroupBalanceOrder()", e);
        }
        log.info("jobSystemCancelLadderGroupBalanceOrder() end");
    }

    /**
     * 系统自动取消没有付款的预售订单
     * 每隔五分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void jobSystemCancelPreSellOrder() {
        log.info("jobSystemCancelPreSellOrder() start");
        try {
            boolean jobResult = orderModel.jobSystemCancelPreSellOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemCancelPreSellOrder] 定时任务系统自动取消没有付款预售订单时失败");
        } catch (Exception e) {
            log.error("jobSystemCancelPreSellOrder()", e);
        }
        log.info("jobSystemCancelPreSellOrder() end");
    }

    /**
     * 系统自动取消没有付款的预售尾款订单
     * 每隔五分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void jobSystemCancelPreSellBalanceOrder() {
        log.info("jobSystemCancelPreSellBalanceOrder() start");
        try {
            boolean jobResult = orderModel.jobSystemCancelPreSellBalanceOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemCancelPreSellBalanceOrder] 定时任务系统自动取消没有付尾款预售订单时失败");
        } catch (Exception e) {
            log.error("jobSystemCancelPreSellBalanceOrder()", e);
        }
        log.info("jobSystemCancelPreSellBalanceOrder() end");
    }

    /**
     * 定时任务设定商家各项统计数据
     * 每隔十分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobStoreStatistics() {
        log.info("jobStoreStatistics() start");
        try {
            boolean jobResult = storeModel.jobStoreStatistics();
            AssertUtil.isTrue(!jobResult, "[jobStoreStatistics] 定时任务设定商家各项统计数据时失败");
        } catch (Exception e) {
            log.error("jobStoreStatistics()", e);
        }
        log.info("jobStoreStatistics() end");
    }

    /**
     * 系统自动完成换货单
     * 对已发货状态的换货单发货时间超过15个自然日的换货单进行自动完成处理
     */
//    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobSystemFinishReplacementOrder() {
        log.info("jobSystemFinishReplacementOrder() start");
        try {
            boolean jobResult = orderReplacementModel.jobSystemFinishReplacementOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemFinishReplacementOrder] 系统自动完成换货单时失败");
        } catch (Exception e) {
            log.error("jobSystemFinishReplacementOrder()", e);
        }
        log.info("jobSystemFinishReplacementOrder() end");
    }

    /**
     * 每天执行一次，定时器定时处理超过3天的交易投诉交由商家申诉
     */
//    @Scheduled(cron = "0 0 5 * * ?")
    public void jobSubmitStoreComplaint() {
        log.info("jobSubmitStoreComplaint() start");
        try {
            boolean jobResult = complainModel.jobSubmitStoreComplaint();
            AssertUtil.isTrue(!jobResult, "[jobSubmitStoreComplaint] 定时任务默认3天后交由商家申诉时失败");
        } catch (Exception e) {
            log.error("jobSubmitStoreComplaint()", e);
        }
        log.info("jobSubmitStoreComplaint() end");
    }

    /**
     * 每天执行一次，定时器定时处理商家申诉超过7天的交易投诉自动投诉成功
     */
//    @Scheduled(cron = "0 30 5 * * ?")
    public void jobAutoComplaintSuccess() {
        log.info("jobAutoComplaintSuccess() start");
        try {
            boolean jobResult = complainModel.jobAutoComplaintSuccess();
            AssertUtil.isTrue(!jobResult, "[jobAutoComplaintSuccess] 定时任务处理商家申诉超过7天的交易投诉自动投诉成功时失败");
        } catch (Exception e) {
            log.error("jobAutoComplaintSuccess()", e);
        }
        log.info("jobAutoComplaintSuccess() end");
    }

    /**
     * 系统自动取消没有付款的拼团订单
     * 每隔五分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void jobSystemCancelSpellOrder() {
        log.info("jobSystemCancelSpellOrder() start");
        try {
            boolean jobResult = orderModel.jobSystemCancelSpellOrder();
            AssertUtil.isTrue(!jobResult, "[jobSystemCancelSpellOrder] 定时任务系统自动取消没有付款拼团订单时失败");
        } catch (Exception e) {
            log.error("jobSystemCancelSpellOrder()", e);
        }
        log.info("jobSystemCancelSpellOrder() end");
    }

    /**
     * 系统自动发起对拼团失败的退款
     * 对拼团活动成团后在截团期间对未完成拼团的的订单进行自动发起退款处理
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    public void jobSystemLaunchSpellRefund() {
        log.info("jobSystemLaunchSpellRefund() start");
        try {
            boolean jobResult = orderModel.jobSystemLaunchSpellRefund();
            AssertUtil.isTrue(!jobResult, "[jobSystemLaunchSpellRefund] 定时任务自动发起拼团失败的退款时失败");
        } catch (Exception e) {
            log.error("jobSystemLaunchSpellRefund()", e);
        }
        log.info("jobSystemLaunchSpellRefund() end");
    }

    /**
     * 系统定时检查即将过期优惠券，并对会员发送消息提醒
     * 每天早上六点半执行
     */
    @Scheduled(cron = "0 30 6 * * ?")
    public void jobSystemCheckExpiredCouponRemind() {
        log.info("jobSystemCheckExpiredCouponRemind() start");
        try {
            List<CouponMember> couponMemberList = couponMemberModel.jobSystemCheckExpiredCoupon();
            if (!CollectionUtils.isEmpty(couponMemberList)) {
                for (CouponMember couponMember : couponMemberList) {
                    Date expiredDate = couponMember.getEffectiveEnd();
                    //构造消息通知对象
                    List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
                    messageSendPropertyList.add(new MessageSendProperty("expireTime", TimeUtil.getDateTimeString(expiredDate)));
                    //微信消息通知
                    List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
                    messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您有一张优惠券即将过期，请登录查看"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", TimeUtil.getDateTimeString(expiredDate)));
                    String msgLinkInfo = "{\"type\":\"coupon_news\"}";
                    MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, null,
                            couponMember.getMemberId(), MemberTplConst.COUPON_EXPIRED_REMINDER, msgLinkInfo);

                    //发送到mq
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
                }
            }
        } catch (Exception e) {
            log.error("jobSystemCheckExpiredCouponRemind()", e);
        }
        log.info("jobSystemCheckExpiredCouponRemind() end");
    }

    /**
     * 系统定时对库存进行检查
     * 达到预警值的库存会给商家发送信息
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    public void jobSystemSendStockWarnRemind() {
        log.info("jobSystemSendStockWarnRemind() start");
        try {
            List<Product> productList = productModel.jobSystemCheckProductStock();
            if (!CollectionUtils.isEmpty(productList)) {
                for (Product product : productList) {
                    //消息通知对象
                    List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
                    messageSendPropertyList.add(new MessageSendProperty("goodsName", product.getGoodsName()));
                    messageSendPropertyList.add(new MessageSendProperty("goodsSpec", product.getSpecValues()));
                    String msgLinkInfo = "{\"goodsId\":\"" + product.getGoodsId() + "\",\"type\":\"goods_news\"}";
                    MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, product.getStoreId(), StoreTplConst.GOODS_STOCK_WARNING, msgLinkInfo);

                    //发送到mq
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
                }
            }
        } catch (Exception e) {
            log.error("jobSystemSendStockWarnRemind()", e);
        }
        log.info("jobSystemSendStockWarnRemind() end");
    }

    /**
     * 系统自动完成冻结或续签店铺
     * 对即将到期的店铺发送到期通知
     * 每天00:05:00执行一次，如果store表的end_time小于当前时间，查询续签表，1.没有续签成功的申请：冻结店铺;2.有续签成功的，续签，更新店铺等级、end_time
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void jobSystemStoreManage() {
        log.info("jobSystemStoreManage() start");
        try {
            List<Store> storeList = storeModel.jobSystemStoreManage();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (!CollectionUtils.isEmpty(storeList)) {
                for (Store store : storeList) {
                    List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
                    messageSendPropertyList.add(new MessageSendProperty("storeName", store.getStoreName()));
                    messageSendPropertyList.add(new MessageSendProperty("expireTime", sdf.format(store.getStoreExpireTime())));
                    String msgLinkInfo = "{\"type\":\"store_news\"}";
                    MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, store.getStoreId(), StoreTplConst.STORE_END_REMINDER, msgLinkInfo);

                    //发送到mq
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
                }
            }
        } catch (Exception e) {
            log.error("jobSystemStoreManage()", e);
        }
        log.info("jobSystemStoreManage() end");
    }

    /**
     * 系统自动处理售后单
     * 1.商户未审核的退货退款申请
     * 2.商户未审核的换货
     * 3.商户未审核的仅退款申请
     * 4.用户退货发货但是到时间限制商户还未收货
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobSystemDealAfterService() {
        log.info("jobSystemDealAfterService() start");
        try {
            Map<String, List<OrderReturn>> resultMap = orderAfterServiceModel.jobSystemDealAfterService();

            List<OrderReturn> returnsReceiveList = resultMap.get("returnsReceiveList");
            List<OrderReturn> returnsApplyList = resultMap.get("returnsApplyList");
            List<OrderReturn> refundApplyList = resultMap.get("refundApplyList");

            //处理退货自动处理消息通知
            if (!CollectionUtils.isEmpty(returnsReceiveList)) {
                for (OrderReturn orderReturn : returnsReceiveList) {
                    OrderAfterServiceExample afterServiceExample = new OrderAfterServiceExample();
                    afterServiceExample.setAfsSn(orderReturn.getAfsSn());
                    List<OrderAfterService> orderAfterServiceList = orderAfterServiceModel.getOrderAfterServiceList(afterServiceExample, null);
                    if (CollectionUtils.isEmpty(orderAfterServiceList)) {
                        continue;
                    }
                    OrderAfterService orderAfterService = orderAfterServiceList.get(0);
                    //添加消息通知
                    List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
                    messageSendPropertyList.add(new MessageSendProperty("afsSn", orderReturn.getAfsSn()));
                    String msgLinkInfo = "{\"type\":\"return_news\",\"afsSn\":\"" + orderReturn.getAfsSn() + "\"}";
                    MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null,
                            orderReturn.getStoreId(), StoreTplConst.MEMBER_RETURN_AUTO_RECEIVE_REMINDER, msgLinkInfo);

                    //发送到mq
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
                    //发送会员售后提醒到mq
                    //微信消息通知
                    List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
                    messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的售后单有新的变化。"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", orderReturn.getAfsSn()));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", orderReturn.getReturnMoneyAmount().add(orderReturn.getReturnExpressAmount()) + "元"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", orderReturn.getReturnNum() + "件"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", orderAfterService.getApplyReasonContent()));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("remark", "点击【详情】查看售后详情"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("url", DomainUrlUtil.SLD_H5_URL + "/#/pages/order/refundDetail?afsSn=" + orderAfterService.getAfsSn()));
                    MessageSendVO sendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, null, orderReturn.getMemberId(), MemberTplConst.AFTER_SALE_REMINDER, msgLinkInfo);
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, sendVO);
                }
            }
            //处理退货未收货自动处理消息通知
            if (!CollectionUtils.isEmpty(returnsApplyList)) {
                for (OrderReturn orderReturn : returnsApplyList) {
                    OrderAfterServiceExample afterServiceExample = new OrderAfterServiceExample();
                    afterServiceExample.setAfsSn(orderReturn.getAfsSn());
                    List<OrderAfterService> orderAfterServiceList = orderAfterServiceModel.getOrderAfterServiceList(afterServiceExample, null);
                    if (CollectionUtils.isEmpty(orderAfterServiceList)) {
                        continue;
                    }
                    OrderAfterService orderAfterService = orderAfterServiceList.get(0);

                    //添加消息通知
                    List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
                    messageSendPropertyList.add(new MessageSendProperty("afsSn", orderReturn.getAfsSn()));
                    String msgLinkInfo = "{\"type\":\"return_news\",\"afsSn\":\"" + orderReturn.getAfsSn() + "\"}";
                    MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, orderReturn.getStoreId(), StoreTplConst.MEMBER_AUTO_RETURN_REMINDER, msgLinkInfo);

                    //发送到mq
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
                    //发送会员售后提醒到mq
                    //微信消息通知
                    List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
                    messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的售后单有新的变化。"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", orderReturn.getAfsSn()));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", orderReturn.getReturnMoneyAmount().add(orderReturn.getReturnExpressAmount()) + "元"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", orderReturn.getReturnNum() + "件"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", orderAfterService.getApplyReasonContent()));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("remark", "点击【详情】查看售后详情"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("url", DomainUrlUtil.SLD_H5_URL + "/#/pages/order/refundDetail?afsSn=" + orderAfterService.getAfsSn()));
                    MessageSendVO sendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, null, orderReturn.getMemberId(), MemberTplConst.AFTER_SALE_REMINDER, msgLinkInfo);
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, sendVO);
                }
            }
            //处理退款自动处理消息通知
            if (!CollectionUtils.isEmpty(refundApplyList)) {
                for (OrderReturn orderReturn : refundApplyList) {
                    OrderAfterServiceExample afterServiceExample = new OrderAfterServiceExample();
                    afterServiceExample.setAfsSn(orderReturn.getAfsSn());
                    List<OrderAfterService> orderAfterServiceList = orderAfterServiceModel.getOrderAfterServiceList(afterServiceExample, null);
                    if (CollectionUtils.isEmpty(orderAfterServiceList)) {
                        continue;
                    }
                    OrderAfterService orderAfterService = orderAfterServiceList.get(0);

                    //添加消息通知
                    List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
                    messageSendPropertyList.add(new MessageSendProperty("afsSn", orderReturn.getAfsSn()));
                    String msgLinkInfo = "{\"type\":\"refund_news\",\"afsSn\":\"" + orderReturn.getAfsSn() + "\"}";
                    MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, orderReturn.getStoreId(), StoreTplConst.MEMBER_AUTO_REFUND_REMINDER, msgLinkInfo);

                    //发送到mq
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
                    //发送会员售后提醒到mq
                    //微信消息通知
                    List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
                    messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的售后单有新的变化。"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", orderReturn.getAfsSn()));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", orderReturn.getReturnMoneyAmount().add(orderReturn.getReturnExpressAmount()) + "元"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", orderReturn.getReturnNum() + "件"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", orderAfterService.getApplyReasonContent()));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("remark", "点击【详情】查看售后详情"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("url", DomainUrlUtil.SLD_H5_URL + "/#/pages/order/refundDetail?afsSn=" + orderAfterService.getAfsSn()));
                    MessageSendVO sendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, null, orderReturn.getMemberId(), MemberTplConst.AFTER_SALE_REMINDER, msgLinkInfo);
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, sendVO);
                }
            }
        } catch (Exception e) {
            log.error("jobSystemDealAfterService()", e);
        }
        log.info("jobSystemDealAfterService() end");
    }

    /**
     * 定时清理已结束的商品活动
     * 每隔十分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void jobClearGoodsPromotion() {
        log.info("jobClearGoodsPromotion() start");
        try {
            boolean jobResult = goodsPromotionModel.jobClearGoodsPromotion();
            AssertUtil.isTrue(!jobResult, "[jobClearGoodsPromotion] 定时清理已结束的商品活动时失败");
        } catch (Exception e) {
            log.error("jobClearGoodsPromotion()", e);
        }
        log.info("jobClearGoodsPromotion() end");
    }

    /**
     * 秒杀场次开始前一分钟将秒杀货品和限购数量放入redis
     * 每小时的59:00执行一次
     */
    @Scheduled(cron = "0 59 * * * ?")
    public void jobSaveSeckillStageProduct() {
        log.info("jobSaveSeckillStageProduct() start");
        try {
            boolean jobResult = seckillStageProductModel.jobSaveSeckillStageProduct();
            AssertUtil.isTrue(!jobResult, "[jobSaveSeckillStageProduct] 定时保存秒杀场次活动货品到redis失败");
        } catch (Exception e) {
            log.error("jobSaveSeckillStageProduct()", e);
        }
        log.info("jobSaveSeckillStageProduct() end");
    }

    /**
     * 秒杀结束后将redis中的货品库存放入货品表
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void jobRecoverSeckillProduct() {
        log.info("jobRecoverSeckillProduct() start");
        try {
            boolean jobResult = seckillStageProductModel.jobRecoverSeckillProduct();
            AssertUtil.isTrue(!jobResult, "[jobRecoverSeckillProduct] 定时恢复redis中秒杀货品到数据库");
        } catch (Exception e) {
            log.error("jobRecoverSeckillProduct()", e);
        }
        log.info("jobRecoverSeckillProduct() end");
    }


    /**
     * 秒杀商品设置提醒成功后，距离秒杀场次开始时间3分钟时，向会员消息推送；
     */
    @Scheduled(cron = "0 57 * * * ?")
    public void jobSendSeckillStageProductNotice() {
        log.info("jobSendSeckillStageProductNotice() start");
        try {
            List<SeckillStageProductBindMember> productBindMemberList = seckillStageProductModel.jobSendSeckillStageProductNotice();
            if (!CollectionUtils.isEmpty(productBindMemberList)) {
                productBindMemberList.forEach(seckillStageProductBindMember -> {
                    SeckillStageProduct seckillStageProduct = seckillStageProductModel.getSeckillStageProductByStageProductId(seckillStageProductBindMember.getStageProductId());
                    AssertUtil.notNull(seckillStageProduct, "秒杀商品信息为空");
                    //添加消息通知
                    List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
                    messageSendPropertyList.add(new MessageSendProperty("goodsName", seckillStageProduct.getGoodsName()));

                    //微信消息通知
                    List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
                    messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您预约的秒杀活动即将开始。"));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", seckillStageProductBindMember.getMemberName()));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", "商品秒杀-" + seckillStageProduct.getGoodsName()));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", TimeUtil.getDateTimeString(seckillStageProduct.getStartTime())));
                    messageSendPropertyList4Wx.add(new MessageSendProperty("remark", "请及时参与。"));

                    String msgLinkInfo = "{\"type\":\"appointment_news\",\"productId\":\""
                            + seckillStageProduct.getProductId() + "\",\"goodsId\":\"" + seckillStageProduct.getGoodsId() + "\"}";
                    MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, null,
                            seckillStageProductBindMember.getMemberId(), MemberTplConst.SCEKILL_START, msgLinkInfo);

                    //发送到mq
                    rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
                });
            }
        } catch (Exception e) {
            log.error("jobSendSeckillStageProductNotice()", e);
        }
        log.info("jobSendSeckillStageProductNotice() end");
    }

    /**
     * 定时修改阶梯团尾款信息
     * 每隔一分钟执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void jobUpdateLadderGroup() {
        log.info("jobUpdateLadderGroup() start");
        try {
            boolean jobResult = ladderGroupModel.jobUpdateLadderGroup();
            AssertUtil.isTrue(!jobResult, "[jobUpdateLadderGroup] 定时修改阶梯团尾款信息时失败");
        } catch (Exception e) {
            log.error("jobUpdateLadderGroup()", e);
        }
        log.info("jobUpdateLadderGroup() end");
    }
}
