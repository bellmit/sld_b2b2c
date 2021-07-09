package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.dto.OrderAfterDTO;
import com.slodon.b2b2c.business.example.*;
import com.slodon.b2b2c.business.pojo.*;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.constant.StoreTplConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.business.*;
import com.slodon.b2b2c.dao.read.system.ExpressReadMapper;
import com.slodon.b2b2c.dao.write.business.*;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.system.pojo.Express;
import com.slodon.b2b2c.vo.business.AfsApplyInfoVO;
import com.slodon.b2b2c.vo.business.AfsProductVO;
import com.slodon.b2b2c.vo.business.OrderProductListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_SELLER_MSG;

@Component
@Slf4j
public class OrderAfterServiceModel {

    @Resource
    private OrderAfterServiceReadMapper orderAfterServiceReadMapper;
    @Resource
    private OrderAfterServiceWriteMapper orderAfterServiceWriteMapper;
    @Resource
    private OrderReturnReadMapper orderReturnReadMapper;
    @Resource
    private OrderReturnWriteMapper orderReturnWriteMapper;
    @Resource
    private OrderReplacementReadMapper orderReplacementReadMapper;
    @Resource
    private OrderReplacementWriteMapper orderReplacementWriteMapper;
    @Resource
    private OrderReadMapper orderReadMapper;
    @Resource
    private OrderWriteMapper orderWriteMapper;
    @Resource
    private OrderProductReadMapper orderProductReadMapper;
    @Resource
    private OrderProductWriteMapper orderProductWriteMapper;
    @Resource
    private OrderExtendReadMapper orderExtendReadMapper;
    @Resource
    private OrderAfterSaleLogWriteMapper orderAfterSaleLogWriteMapper;
    @Resource
    private ExpressReadMapper expressReadMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增订单售后服务表
     *
     * @param orderAfterService
     * @return
     */
    public Integer saveOrderAfterService(OrderAfterService orderAfterService) {
        int count = orderAfterServiceWriteMapper.insert(orderAfterService);
        if (count == 0) {
            throw new MallException("添加订单售后服务表失败，请重试");
        }
        return count;
    }

    /**
     * 根据afsId删除订单售后服务表
     *
     * @param afsId afsId
     * @return
     */
    public Integer deleteOrderAfterService(Integer afsId) {
        if (StringUtils.isEmpty(afsId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderAfterServiceWriteMapper.deleteByPrimaryKey(afsId);
        if (count == 0) {
            log.error("根据afsId：" + afsId + "删除订单售后服务表失败");
            throw new MallException("删除订单售后服务表失败,请重试");
        }
        return count;
    }

    /**
     * 根据afsId更新订单售后服务表
     *
     * @param orderAfterService
     * @return
     */
    public Integer updateOrderAfterService(OrderAfterService orderAfterService) {
        if (StringUtils.isEmpty(orderAfterService.getAfsId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(orderAfterService);
        if (count == 0) {
            log.error("根据afsId：" + orderAfterService.getAfsId() + "更新订单售后服务表失败");
            throw new MallException("更新订单售后服务表失败,请重试");
        }
        return count;
    }

    /**
     * 根据afsId获取订单售后服务表详情
     *
     * @param afsId afsId
     * @return
     */
    public OrderAfterService getOrderAfterServiceByAfsId(Integer afsId) {
        return orderAfterServiceReadMapper.getByPrimaryKey(afsId);
    }

    /**
     * 根据条件获取订单售后服务表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderAfterService> getOrderAfterServiceList(OrderAfterServiceExample example, PagerInfo pager) {
        List<OrderAfterService> orderAfterServiceList;
        if (pager != null) {
            pager.setRowsCount(orderAfterServiceReadMapper.countByExample(example));
            orderAfterServiceList = orderAfterServiceReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderAfterServiceList = orderAfterServiceReadMapper.listByExample(example);
        }
        return orderAfterServiceList;
    }

    /**
     * 根据afsSn获取订单售后服务信息
     *
     * @param afsSn
     * @return
     */
    public OrderAfterService getAfterServiceByAfsSn(String afsSn) {
        OrderAfterServiceExample example = new OrderAfterServiceExample();
        example.setAfsSn(afsSn);
        List<OrderAfterService> afterServiceList = orderAfterServiceReadMapper.listByExample(example);
        AssertUtil.notEmpty(afterServiceList, "获取售后服务单信息为空，请重试");

        return afterServiceList.get(0);
    }

    /**
     * 根据售后单号获取退货信息
     *
     * @param afsSn
     * @return
     */
    private OrderReturn getOrderReturnByAfsSn(String afsSn) {
        OrderReturnExample example = new OrderReturnExample();
        example.setAfsSn(afsSn);
        List<OrderReturn> list = orderReturnReadMapper.listByExample(example);
        AssertUtil.notEmpty(list, "获取退货信息为空，请重试");
        return list.get(0);
    }

    /**
     * 根据售后单号获取换货信息
     *
     * @param afsSn
     * @return
     */
    private OrderReplacement getOrderReplacementByAfsSn(String afsSn) {
        OrderReplacementExample example = new OrderReplacementExample();
        example.setAfsSn(afsSn);
        List<OrderReplacement> list = orderReplacementReadMapper.listByExample(example);
        AssertUtil.notEmpty(list, "获取换货信息为空，请重试");
        return list.get(0);
    }

    /**
     * 退款申请信息
     *
     * @param memberId
     * @param orderSn
     * @param orderProductId
     * @return
     */
    public AfsApplyInfoVO getAfterSaleApplyInfo(Integer memberId, String orderSn, Long orderProductId) {
        /*
         *货品可退金额计算方法（平台优惠券不退的情况,每次退款都按比例退现金和积分）（RMB采用四舍五入计算，积分采用向下取整方式）
         * 1.订单总共可退RMB = 现金支付金额 + 余额支付金额 - 运费 ；   订单总共可退积分 = 订单使用的积分数量；订单购物赠送积分：扩展信息获取
         * 2.计算出本货品总共可退金额（金额包括积分、RMB）、总共需要扣除的购物赠送的积分：（为了保证展示的可退金额，与实际退款金额一致，采用如下算法，保证同一货品在展示与打款时，分配的可退金额相同）
         *      2.1 查询订单下的货品列表，按照数据库中的顺序
         *      2.2 如果不是最后一个货品，可退金额 = 订单总可退金额 * 货品明细金额/（订单总金额-运费）
         *      2.3 如果是最后一个货品，可退金额 = 订单总可退金额 - 其他订单分配的总额
         * 3.计算本货品剩余可退金额（金额包括积分、RMB） 、 剩余要扣除的购物赠送的积分 :
         *      3.1 本货品剩余可退金额 = 本货品总共可退金额 - 本货品总共可退金额 * 换货数量总和 / 货品总数量 - 所有退货金额总和
         */

        //获取数据库中的订单,订单货品，数据检测
        OrderExample ordersExample = new OrderExample();
        ordersExample.setMemberId(memberId);
        ordersExample.setOrderSn(orderSn);
        Order orderDb = orderReadMapper.listByExample(ordersExample).get(0);
        AssertUtil.notNull(orderDb, "订单不存在");
        OrderProduct orderProductDb = orderProductReadMapper.getByPrimaryKey(orderProductId);
        AssertUtil.notNull(orderProductDb, "订单货品不存在");
        AssertUtil.isTrue(!memberId.equals(orderProductDb.getMemberId()), "您无权操作此订单货品");


        //检测订单信息
        this.checkOrderInfo(memberId, orderDb, orderProductDb);

        //舍入类型
        MathContext amountContext = new MathContext(3, RoundingMode.HALF_UP);   //四舍五入，保留2位小数
        MathContext integralContext = new MathContext(1, RoundingMode.DOWN);    //舍弃所有小数

        OrderExtendExample extendExample = new OrderExtendExample();
        extendExample.setOrderSn(orderDb.getOrderSn());
        OrderExtend orderExtendDb = orderExtendReadMapper.listByExample(extendExample).get(0);
        //2.计算出本货品总共可退金额：
        Map<String, BigDecimal> orderProductTotalReturnMap = this.getOrderProductTotalReturn(orderDb, orderExtendDb, orderProductDb, amountContext, integralContext);
        //货品总共可退RMB
        BigDecimal productTotalReturnAmount = orderProductTotalReturnMap.get("productTotalReturnAmount");
        //货品总共可退积分
        BigDecimal productTotalReturnIntegral = orderProductTotalReturnMap.get("productTotalReturnIntegral");
        //货品购物赠送总积分
        BigDecimal productTotalSendIntegral = orderProductTotalReturnMap.get("productTotalSendIntegral");
        //返回数据
        AfsApplyInfoVO afsApplyInfoVO = this.getOrderProductCanReturnAmount(orderProductDb, productTotalReturnAmount,
                productTotalReturnIntegral, productTotalSendIntegral, amountContext, integralContext);
        afsApplyInfoVO.setNumber(orderProductDb.getProductNum() - orderProductDb.getReplacementNumber() - orderProductDb.getReplacementNumber());
        OrderProductListVO orderProductListVO = new OrderProductListVO(orderProductDb);
        afsApplyInfoVO.setOrderProduct(orderProductListVO);
        return afsApplyInfoVO;
    }

    /**
     * 检测订单信息是否可用
     *
     * @param orderDb
     * @param orderProductDb
     */
    private void checkOrderInfo(Integer memberId, Order orderDb, OrderProduct orderProductDb) {
        AssertUtil.isTrue(!orderDb.getMemberId().equals(memberId), "您无权操作此订单");

        //获取售后限制时间
        int limitDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_after_sale"));
        Date date = TimeUtil.getDateApartDay(-limitDay);
        AssertUtil.isTrue(OrderConst.ORDER_STATE_40 == orderDb.getOrderState()
                && orderDb.getFinishTime().before(date), "很抱歉，售后服务时间已过");
        AssertUtil.isTrue(!orderProductDb.getOrderSn().equals(orderDb.getOrderSn()), "订单货品与订单信息不匹配");

        int num = orderProductDb.getProductNum() - orderProductDb.getReplacementNumber() - orderProductDb.getReplacementNumber();
        AssertUtil.isTrue(num <= 0, "退换货信息有误");
    }

    /**
     * 计算订单中某一货品，总共可退的RMB、积分，购物赠送的总积分
     * 计算规则为：按订单中所有货品的顺序，按照货品明细金额占订单金额（除运费）的比例计算，最后一个货品为总额-其他货品可退额之和
     *
     * @param orderDb
     * @param orderExtendDb
     * @param orderProductDb
     * @param amountContext   RMB舍入方式
     * @param integralContent 积分舍入方式
     * @return
     */
    private Map<String, BigDecimal> getOrderProductTotalReturn(Order orderDb, OrderExtend orderExtendDb, OrderProduct orderProductDb,
                                                               MathContext amountContext, MathContext integralContent) {
        //订单总共可退RMB = 三方支付金额 + 余额支付金额 - 运费
        BigDecimal orderTotalReturnAmount = orderDb.getBalanceAmount().add(orderDb.getPayAmount()).subtract(orderDb.getExpressFee());
        //订单总共可退积分
        BigDecimal orderTotalReturnIntegral = BigDecimal.valueOf(orderDb.getIntegral());
        //订单购物赠送的总积分
        BigDecimal orderTotalSendIntegral = BigDecimal.valueOf(orderExtendDb.getOrderPointsCount());


        //2.1 查询订单下的货品列表，按照数据库中的顺序
        OrderProductExample productExample = new OrderProductExample();
        productExample.setOrderSn(orderDb.getOrderSn());
        List<OrderProduct> orderProductList = orderProductReadMapper.listByExample(productExample);
        AssertUtil.notEmpty(orderProductList, "订单信息有误");

        BigDecimal productTotalReturnAmount;//货品总共可退RMB
        BigDecimal productTotalReturnIntegral;//货品总共可退积分
        BigDecimal productTotalSendIntegral;//货品购物赠送总积分

        // 2.2 如果不是最后一个货品，可退金额 = 订单总可退金额 * 货品明细金额/（订单总金额-运费）
        if (!orderProductList.get(orderProductList.size() - 1).getProductId().equals(orderProductDb.getOrderProductId())) {
            if ((orderDb.getOrderAmount().subtract(orderDb.getExpressFee())).compareTo(BigDecimal.ZERO) == 0) {
                productTotalReturnAmount = BigDecimal.ZERO;
                productTotalReturnIntegral = BigDecimal.ZERO;
                productTotalSendIntegral = BigDecimal.ZERO;
            } else {
                productTotalReturnAmount = orderTotalReturnAmount                                    //订单总可退金额
                        .multiply(orderProductDb.getMoneyAmount())                                   //*货品明细金额
                        .divide(orderDb.getOrderAmount().subtract(orderDb.getExpressFee()), amountContext); // /（订单总金额-运费）
                productTotalReturnIntegral = orderTotalReturnIntegral                                //订单总可退积分
                        .multiply(orderProductDb.getMoneyAmount())                                   //*货品明细金额
                        .divide(orderDb.getOrderAmount().subtract(orderDb.getExpressFee()), integralContent); // /（订单总金额-运费）

                productTotalSendIntegral = orderTotalSendIntegral                                    //订单购物赠送积分总额
                        .multiply(orderProductDb.getMoneyAmount())                                   //*货品明细金额
                        .divide(orderDb.getOrderAmount().subtract(orderDb.getExpressFee()), integralContent); // /（订单总金额-运费）
            }
        } else {
            //2.3 如果是最后一个货品，可退金额 = 订单总可退金额 - 其他订单分配的总额
            BigDecimal totalAssignAmount = BigDecimal.ZERO;//已经分配的RMB
            BigDecimal totalAssignIntegral = BigDecimal.ZERO;//已经分配的可退积分
            BigDecimal totalAssignSendIntegral = BigDecimal.ZERO;//已经分配的购物赠送积分
            for (int i = 0; i < orderProductList.size() - 1; i++) {
                OrderProduct op = orderProductList.get(i);
                //当前货品分配RMB
                BigDecimal currentAssignAmount = BigDecimal.ZERO;
                if ((orderDb.getOrderAmount().subtract(orderDb.getExpressFee())).compareTo(BigDecimal.ZERO) != 0) {
                    currentAssignAmount = orderTotalReturnAmount                              //订单总可退金额
                            .multiply(op.getMoneyAmount())                                               //*货品明细金额
                            .divide(orderDb.getOrderAmount().subtract(orderDb.getExpressFee()), amountContext); // /（订单总金额-运费）
                }
                //累加
                totalAssignAmount = totalAssignAmount.add(currentAssignAmount);
                //当前货品分配积分
                BigDecimal currentAssignIntegral = BigDecimal.ZERO;
                if ((orderDb.getOrderAmount().subtract(orderDb.getExpressFee())).compareTo(BigDecimal.ZERO) != 0) {
                    currentAssignIntegral = orderTotalReturnIntegral                          //订单总可退积分
                            .multiply(op.getMoneyAmount())                                               //*货品明细金额
                            .divide(orderDb.getOrderAmount().subtract(orderDb.getExpressFee()), integralContent); // /（订单总金额-运费）
                }
                //累加
                totalAssignIntegral = totalAssignIntegral.add(currentAssignIntegral);

                //当前货品分配积分
                BigDecimal currentAssignSendIntegral = BigDecimal.ZERO;
                if ((orderDb.getOrderAmount().subtract(orderDb.getExpressFee())).compareTo(BigDecimal.ZERO) != 0) {
                    currentAssignSendIntegral = orderTotalSendIntegral                          //订单总可退积分
                            .multiply(op.getMoneyAmount())                                               //*货品明细金额
                            .divide(orderDb.getOrderAmount().subtract(orderDb.getExpressFee()), integralContent); // /（订单总金额-运费）
                }
                //累加
                totalAssignSendIntegral = totalAssignSendIntegral.add(currentAssignSendIntegral);
            }
            productTotalReturnAmount = orderTotalReturnAmount.subtract(totalAssignAmount);
            productTotalReturnIntegral = orderTotalReturnIntegral.subtract(totalAssignIntegral);
            productTotalSendIntegral = orderTotalSendIntegral.subtract(totalAssignSendIntegral);
        }

        //返回数据
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("productTotalReturnAmount", productTotalReturnAmount);
        result.put("productTotalReturnIntegral", productTotalReturnIntegral);
        result.put("productTotalSendIntegral", productTotalSendIntegral);
        return result;
    }

    /**
     * 计算订单货品剩余可退金额
     * 本货品剩余可退金额 = 本货品总共可退金额 - 本货品总共可退金额 * 换货数量总和 / 货品总数量 - 所有退货金额总和
     *
     * @param orderProductDb
     * @param productTotalReturnAmount   货品总可退RMB
     * @param productTotalReturnIntegral 货品总可退积分
     * @param productTotalSendIntegral   货品总共购物赠送的积分
     * @param amountContext              RMB舍入方式
     * @param integralContext            积分舍入方式
     * @return
     */
    private AfsApplyInfoVO getOrderProductCanReturnAmount(OrderProduct orderProductDb,
                                                          BigDecimal productTotalReturnAmount,
                                                          BigDecimal productTotalReturnIntegral,
                                                          BigDecimal productTotalSendIntegral,
                                                          MathContext amountContext, MathContext integralContext) {
        AfsApplyInfoVO afsApplyInfoVO = new AfsApplyInfoVO();
        Map<String, BigDecimal> canReturnMap = new HashMap<>();//返回数据
        BigDecimal moneyCanReturn;//剩余可退RMB
        BigDecimal integralCanReturn;//剩余可退积分
        BigDecimal integralCanDeduct;//剩余可扣积分（购物赠送积分）

        if (orderProductDb.getReplacementNumber() + orderProductDb.getReturnNumber() == 0) {
            //没有退换货记录，直接返回该货品的总可退金额
            moneyCanReturn = productTotalReturnAmount;
            integralCanReturn = productTotalReturnIntegral;
            integralCanDeduct = productTotalSendIntegral;
        } else {
            //3.计算本货品剩余可退金额 :
            //3.1 本货品剩余可退金额 = 本货品总共可退金额 - 本货品总共可退金额 * 换货数量总和 / 货品总数量 - 所有退货金额总和
            //查询售后列表
            OrderAfterServiceExample afterServiceExample = new OrderAfterServiceExample();
            afterServiceExample.setOrderSn(orderProductDb.getOrderSn());
            afterServiceExample.setOrderProductId(orderProductDb.getOrderProductId());
            List<OrderAfterService> afterServiceList = orderAfterServiceReadMapper.listByExample(afterServiceExample);
            BigDecimal replacementNum = BigDecimal.ZERO;//换货总数
            BigDecimal returnMoney = BigDecimal.ZERO;//退款总数
            BigDecimal returnIntegral = BigDecimal.ZERO;//退积分总数
            BigDecimal deductIntegral = BigDecimal.ZERO;//扣减积分总数
            for (OrderAfterService afterService : afterServiceList) {
                if (afterService.getAfsType() == OrdersAfsConst.AFS_TYPE_REPLACEMENT) {
                    //换货，累加换货数量
                    replacementNum = replacementNum.add(BigDecimal.valueOf(afterService.getAfsNum()));
                } else {
                    OrderReturnExample example = new OrderReturnExample();
                    example.setAfsSn(afterService.getAfsSn());
                    List<OrderReturn> list = orderReturnReadMapper.listByExample(example);
                    AssertUtil.notEmpty(list, "获取退货记录信息失败");
                    //退货，查询退货表，退款金额累加
                    OrderReturn orderReturn = list.get(0);
                    returnMoney = returnMoney.add(orderReturn.getReturnMoneyAmount());
                    returnIntegral = returnIntegral.add(BigDecimal.valueOf(orderReturn.getReturnIntegralAmount()));
                    deductIntegral = deductIntegral.add(BigDecimal.valueOf(orderReturn.getDeductIntegralAmount()));
                }
            }
            //按比例计算换货的退款、退积分、扣积分数
            //计算换货RMB
            BigDecimal replaceMoney = productTotalReturnAmount                                 //本货品总共可退金额
                    .multiply(replacementNum)                                                  //* 换货数量
                    .divide(BigDecimal.valueOf(orderProductDb.getProductNum()), amountContext); // / 货品总数量


            //计算换货积分
            BigDecimal replaceIntegral = productTotalReturnIntegral                               //本货品总共可退金额
                    .multiply(replacementNum)                                                     //* 换货数量
                    .divide(BigDecimal.valueOf(orderProductDb.getProductNum()), integralContext);  // / 货品总数量


            //换货扣除的购物赠送积分
            BigDecimal replaceDeductIntegral = productTotalSendIntegral                          //货品购物赠送总积分
                    .multiply(replacementNum)                                                    //* 换货数量
                    .divide(BigDecimal.valueOf(orderProductDb.getProductNum()), integralContext); // / 货品总数量

            //计算返回数据 = 总数据 - 退货数据 - 换货数据
            moneyCanReturn = productTotalReturnAmount.subtract(returnMoney).subtract(replaceMoney);
            integralCanReturn = productTotalReturnIntegral.subtract(returnIntegral).subtract(replaceIntegral);
            integralCanDeduct = productTotalSendIntegral.subtract(deductIntegral).subtract(replaceDeductIntegral);
        }
        afsApplyInfoVO.setMoneyCanReturn(moneyCanReturn);
        afsApplyInfoVO.setIntegralCanReturn(integralCanReturn);
        afsApplyInfoVO.setIntegralCanDeduct(integralCanDeduct);
        return afsApplyInfoVO;
    }

    /**
     * 退款申请提交
     *
     * @param orderAfterDTO
     * @param memberId
     */
    @Transactional
    public List<String> submitAfterSaleApply(OrderAfterDTO orderAfterDTO, Integer memberId) {
        List<String> afsSnList = new ArrayList<>();
        OrderExample example = new OrderExample();
        example.setOrderSn(orderAfterDTO.getOrderSn());
        Order orderDb = orderReadMapper.listByExample(example).get(0);
        AssertUtil.isTrue(!orderDb.getMemberId().equals(memberId), "您无权操作此订单");
        //参数校验
        this.checkAfsApplyInfo(orderDb, orderAfterDTO);

        //1.处理售后货品，统计各个货品退款信息
        List<AfsProductVO> afsProductVOS = this.dealAfsProduct(orderDb, orderAfterDTO);

        afsProductVOS.forEach(afsProductVO -> {
            String afsSn = GoodsIdGenerator.getAfsSn();
            afsSnList.add(afsSn);

            //2.插入售后申请表，
            this.insertAfterService(afsSn, afsProductVO, orderAfterDTO, orderDb);

            //3.根据售后类型插入退货表或者换货表，保存售后日志
            this.insertReturnOrReplacement(afsSn, afsProductVO, orderAfterDTO, orderDb);

            //4.更新订单货品中的退换货数量
            this.updateOrderProduct(afsProductVO, orderAfterDTO);

            //发送消息
            List<MessageSendProperty> messageSendProperties = new ArrayList<>();
            String msgLinkInfo = "";
            //模板类型
            String tplType = "";
            if (orderAfterDTO.getAfsType() == OrdersAfsConst.AFS_TYPE_RETURN) {
                messageSendProperties.add(new MessageSendProperty("returnSn", afsSn));
                msgLinkInfo = "{\"afsSn\":\"" + afsSn + "\",\"type\":\"return_news\"}";
                tplType = StoreTplConst.MEMBER_RETURN_REMINDER;
            } else if (orderAfterDTO.getAfsType() == OrdersAfsConst.AFS_TYPE_REFUND) {
                messageSendProperties.add(new MessageSendProperty("refundSn", afsSn));
                msgLinkInfo = "{\"afsSn\":\"" + afsSn + "\",\"type\":\"refund_news\"}";
                tplType = StoreTplConst.MEMBER_REFUND_REMINDER;
            } else {
//                messageSendProperties.add(new MessageSendProperty("replaceSn", afsSn));
//                msgLinkInfo = "{\"afsSn\":\"" + afsSn + "\",\"type\":\"replacement_news\"}";
//                tplType = StoreTplConst.MEMBER_REPLACEMENT_REMINDER;
            }
            MessageSendVO messageSendVO = new MessageSendVO(messageSendProperties, null, orderDb.getStoreId(), tplType, msgLinkInfo);
            //发送到mq
            rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
        });
        //5.更新订单信息（锁定订单）
        this.updateOrder(orderDb);
        return afsSnList;
    }

    /**
     * 校验售后申请信息
     *
     * @param order
     * @param orderAfterDTO
     */
    private void checkAfsApplyInfo(Order order, OrderAfterDTO orderAfterDTO) {
        AssertUtil.isTrue(!order.getOrderState().equals(OrderConst.ORDER_STATE_20)
                && !order.getOrderState().equals(OrderConst.ORDER_STATE_30)
                && !order.getOrderState().equals(OrderConst.ORDER_STATE_40), "订单状态有误");

        //获取售后限制时间
        if (order.getOrderState().equals(OrderConst.ORDER_STATE_40)) {
            int limitDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_after_sale"));
            Date date = TimeUtil.getDateApartDay(-limitDay);
            AssertUtil.isTrue(order.getFinishTime().before(date), "很抱歉，售后服务时间已过");
        }

        AssertUtil.isTrue(order.getLockState().compareTo(0) > 0, "订单锁定，不能申请售后");

        AssertUtil.notEmpty(orderAfterDTO.getProductList(), "请选择申请售后的订单货品");

        //各售后类型参数校验
        switch (orderAfterDTO.getAfsType()) {
            case OrdersAfsConst.AFS_TYPE_RETURN://退货退款
                break;
            case OrdersAfsConst.AFS_TYPE_REPLACEMENT:
                throw new MallException("暂不支持换货申请");
            case OrdersAfsConst.AFS_TYPE_REFUND://仅退款
                if (order.getOrderState().equals(OrderConst.ORDER_STATE_30)
                        || order.getOrderState().equals(OrderConst.ORDER_STATE_40)) {
                    //已发货申请仅退款，货物状态必填
                    AssertUtil.notNull(orderAfterDTO.getGoodsState(), "货物状态不能为空");
                    if (orderAfterDTO.getGoodsState() == OrdersAfsConst.GOODS_STATE_YES) {
                        //仅退款，已收到货，可以修改退款金额，退款金额必传
                        AssertUtil.notNull(orderAfterDTO.getFinalReturnAmount(), "退款金额不能为空");
                    }
                }
                break;
            default:
                throw new MallException("暂不支持该类型的售后");
        }
    }

    /**
     * 构造售后货品信息列表，计算各个货品退款金额
     *
     * @param order
     * @param orderAfterDTO
     * @return
     */
    private List<AfsProductVO> dealAfsProduct(Order order, OrderAfterDTO orderAfterDTO) {
        List<AfsProductVO> vos = new ArrayList<>();

        BigDecimal maxReturnMoney = new BigDecimal("0.00");//最大可退金额(不包含运费)
        StringBuilder orderProductIds = new StringBuilder();//此次计算的订单id集合，用于查询是否是本订单最后一次售后
        for (OrderAfterDTO.AfterProduct afterProduct : orderAfterDTO.getProductList()) {
            //判断是否有正在进行的或者完成的售后单
            OrderReturnExample returnExample = new OrderReturnExample();
            returnExample.setOrderSn(orderAfterDTO.getOrderSn());
            returnExample.setOrderProductId(afterProduct.getOrderProductId());
            List<OrderReturn> returnList = orderReturnReadMapper.listByExample(returnExample);
            if (!CollectionUtils.isEmpty(returnList)) {
                OrderReturn orderReturn = returnList.get(0);
                AssertUtil.isTrue(!orderReturn.getState().equals(OrdersAfsConst.RETURN_STATE_202), "您有正在进行的或已完成的售后单,请重试");
            }

            orderProductIds.append(",").append(afterProduct.getOrderProductId());
            OrderProduct orderProduct = orderProductReadMapper.getByPrimaryKey(afterProduct.getOrderProductId());

            AfsProductVO vo = new AfsProductVO(orderProduct);
            vo.setAfsNum(afterProduct.getAfsNum());
            vo.setCommissionAmount(orderProduct.getCommissionAmount().divide(new BigDecimal(afterProduct.getAfsNum()), 2, RoundingMode.HALF_UP));

            if (afterProduct.getAfsNum().compareTo(orderProduct.getProductNum()) >= 0) {
                //要退换的数量大于等于商品数量，默认计算所有商品数量
                maxReturnMoney = maxReturnMoney.add(orderProduct.getMoneyAmount());

                vo.setAfsNum(orderProduct.getProductNum());
                vo.setReturnMoneyAmount(orderProduct.getMoneyAmount());
                vo.setReturnIntegralAmount(orderProduct.getIntegral());
                //退还优惠券
                OrderExtendExample extendExample = new OrderExtendExample();
                extendExample.setOrderSn(order.getOrderSn());
                OrderExtend orderExtendDb = orderExtendReadMapper.listByExample(extendExample).get(0);
                if (!StringUtil.isEmpty(orderExtendDb.getVoucherCode())) {
                    vo.setReturnVoucherCode(orderExtendDb.getVoucherCode());
                }
            } else {
                //根据传来的数量计算要退的金额
                int actualNum = afterProduct.getAfsNum();
                BigDecimal returnMoney = orderProduct.getMoneyAmount()
                        .multiply(BigDecimal.valueOf(actualNum))
                        .divide(BigDecimal.valueOf(orderProduct.getProductNum()), 2, RoundingMode.DOWN);//舍去第三位小数
                Integer returnIntegral = orderProduct.getIntegral() * actualNum / orderProduct.getProductNum();//只保留整数位
                maxReturnMoney = maxReturnMoney.add(returnMoney);

                vo.setReturnMoneyAmount(returnMoney);
                vo.setReturnIntegralAmount(returnIntegral);
            }

            vos.add(vo);
        }

        //运费处理
        if (order.getOrderState().equals(OrderConst.ORDER_STATE_20)
                && order.getExpressFee().compareTo(BigDecimal.ZERO) > 0) {
            //待发货状态，处理运费
            OrderProductExample orderProductExample = new OrderProductExample();
            orderProductExample.setOrderProductIdNotIn(orderProductIds.toString().substring(1));
            orderProductExample.setReturnNumber(0);
            orderProductExample.setReplacementNumber(0);
            orderProductExample.setOrderSn(order.getOrderSn());
            int count = orderProductReadMapper.countByExample(orderProductExample);
            if (count == 0) {
                //说明此次是最后一次售后，退还运费,运费退还至第一个售后货品中
                vos.get(0).setReturnExpressAmount(order.getExpressFee());
            }
        }

        //修改退款金额处理
        if (orderAfterDTO.getAfsType().equals(OrdersAfsConst.AFS_TYPE_REFUND)
                && (order.getOrderState().equals(OrderConst.ORDER_STATE_30)
                || order.getOrderState().equals(OrderConst.ORDER_STATE_40))
                && orderAfterDTO.getGoodsState() == OrdersAfsConst.GOODS_STATE_YES) {
            //发货后的仅退款，用户若选择已收货，可以修改退款金额，
            if (orderAfterDTO.getFinalReturnAmount().compareTo(maxReturnMoney) < 0) {
                // 退款金额小于最大可退金额时，说明用户修改了退款金额，重新分配各个货品的退款金额
                BigDecimal totalAmount = orderAfterDTO.getFinalReturnAmount();
                for (int i = 0; i < vos.size(); i++) {
                    if (i == vos.size() - 1) {
                        //最后一件货品，退款金额==剩余金额
                        vos.get(i).setReturnMoneyAmount(totalAmount);
                    } else {
                        //按比例计算当前货品分配金额，舍去第三位小数
                        BigDecimal current = vos.get(i).getReturnMoneyAmount().multiply(orderAfterDTO.getFinalReturnAmount()).divide(maxReturnMoney, 2, RoundingMode.DOWN);
                        //设置新的金额
                        vos.get(i).setReturnMoneyAmount(current);
                        //计算剩余金额
                        totalAmount = totalAmount.subtract(current);
                    }
                }
            }
        }
        return vos;
    }

    /**
     * 保存售后信息
     *
     * @param afsSn
     * @param afsProductVO
     * @param orderAfterDTO
     * @param order
     */
    private void insertAfterService(String afsSn, AfsProductVO afsProductVO, OrderAfterDTO orderAfterDTO, Order order) {
        OrderAfterService orderAfterService = new OrderAfterService();
        orderAfterService.setAfsSn(afsSn);
        orderAfterService.setStoreId(order.getStoreId());
        orderAfterService.setStoreName(order.getStoreName());
        orderAfterService.setOrderSn(orderAfterDTO.getOrderSn());
        orderAfterService.setMemberId(order.getMemberId());
        orderAfterService.setMemberName(order.getMemberName());
        orderAfterService.setGoodsId(afsProductVO.getGoodsId());
        orderAfterService.setOrderProductId(afsProductVO.getOrderProductId());
        orderAfterService.setAfsNum(afsProductVO.getAfsNum());
        orderAfterService.setApplyImage(orderAfterDTO.getApplyImage());
        orderAfterService.setContactName(order.getMemberName());
        orderAfterService.setContactPhone(order.getReceiverMobile());
        orderAfterService.setAfsType(orderAfterDTO.getAfsType());
        orderAfterService.setApplyReasonContent(orderAfterDTO.getApplyReasonContent());
        orderAfterService.setAfsDescription(orderAfterDTO.getAfsDescription());
        orderAfterService.setBuyerApplyTime(new Date());
        orderAfterService.setStoreReceiveTime(order.getFinishTime());
        orderAfterService.setGoodsState(orderAfterDTO.getGoodsState());
        int result = orderAfterServiceWriteMapper.insert(orderAfterService);
        AssertUtil.isTrue(result == 0, "保存售后工单信息失败");
    }

    /**
     * 保存退换货信息和售后日志
     *
     * @param afsSn
     * @param afsProductVO
     * @param orderAfterDTO
     * @param order
     */
    private void insertReturnOrReplacement(String afsSn, AfsProductVO afsProductVO, OrderAfterDTO orderAfterDTO, Order order) {
        if (orderAfterDTO.getAfsType() == OrdersAfsConst.AFS_TYPE_REPLACEMENT) {
            //todo 换货
        } else {
            //仅退款或退货退款
            OrderReturn orderReturn = new OrderReturn();
            orderReturn.setAfsSn(afsSn);
            orderReturn.setOrderSn(order.getOrderSn());
            orderReturn.setReturnNum(afsProductVO.getAfsNum());
            orderReturn.setStoreId(order.getStoreId());
            orderReturn.setStoreName(order.getStoreName());
            orderReturn.setMemberId(order.getMemberId());
            orderReturn.setMemberName(order.getMemberName());
            orderReturn.setReturnMoneyType(Integer.valueOf(stringRedisTemplate.opsForValue().get("refund_setting_switch")));//退款方式取系统设置
            orderReturn.setReturnIntegralAmount(afsProductVO.getReturnIntegralAmount());
            orderReturn.setDeductIntegralAmount(afsProductVO.getDeductIntegralAmount());
            orderReturn.setReturnMoneyAmount(afsProductVO.getReturnMoneyAmount());
            orderReturn.setReturnExpressAmount(afsProductVO.getReturnExpressAmount());
            orderReturn.setCommissionRate(afsProductVO.getCommissionRate());
            orderReturn.setCommissionAmount(afsProductVO.getCommissionAmount());
            orderReturn.setReturnVoucherCode(afsProductVO.getReturnVoucherCode());
            if (orderAfterDTO.getAfsType() == OrdersAfsConst.AFS_TYPE_REFUND) {
                orderReturn.setState(OrdersAfsConst.RETURN_STATE_100);
                orderReturn.setReturnType(OrdersAfsConst.RETURN_TYPE_1);
            } else if (orderAfterDTO.getAfsType() == OrdersAfsConst.AFS_TYPE_RETURN) {
                orderReturn.setState(OrdersAfsConst.RETURN_STATE_101);
                orderReturn.setReturnType(OrdersAfsConst.RETURN_TYPE_2);
            }
            orderReturn.setApplyTime(new Date());
            int count = orderReturnWriteMapper.insert(orderReturn);
            AssertUtil.isTrue(count == 0, "保存退货信息失败");

            //记录售后日志
            OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
            afterSaleLog.setLogRole(OrderConst.LOG_ROLE_MEMBER);
            afterSaleLog.setLogUserId(Long.parseLong(String.valueOf(order.getMemberId())));
            afterSaleLog.setLogUserName(order.getMemberName());
            afterSaleLog.setAfsSn(afsSn);
            if (orderAfterDTO.getAfsType() == OrdersAfsConst.AFS_TYPE_REFUND) {
                afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_REFUND);
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_100 + "");
                afterSaleLog.setContent("买家申请仅退款");
            } else if (orderAfterDTO.getAfsType() == OrdersAfsConst.AFS_TYPE_RETURN) {
                afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_RETURN);
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_101 + "");
                afterSaleLog.setContent("买家申请退货退款");
            }
            afterSaleLog.setCreateTime(new Date());
            count = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
            AssertUtil.isTrue(count == 0, "记录退货信息失败");
        }
    }

    /**
     * 更新订单货品退换货数量
     *
     * @param afsProductVO
     * @param orderAfterDTO
     */
    private void updateOrderProduct(AfsProductVO afsProductVO, OrderAfterDTO orderAfterDTO) {
        OrderProduct updateOrderProduct = new OrderProduct();
        updateOrderProduct.setOrderProductId(afsProductVO.getOrderProductId());
        if (orderAfterDTO.getAfsType() == OrdersAfsConst.AFS_TYPE_REPLACEMENT) {
            //todo 换货
            updateOrderProduct.setReplacementNumber(afsProductVO.getAfsNum());
        } else {
            //退货
            updateOrderProduct.setReturnNumber(afsProductVO.getAfsNum());
        }
        int count = orderProductWriteMapper.updateByPrimaryKeySelective(updateOrderProduct);
        AssertUtil.isTrue(count == 0, "更新订单货品信息失败");
    }

    /**
     * 更新订单信息，待发货订单申请售后后锁定订单
     *
     * @param orderDb
     */
    private void updateOrder(Order orderDb) {
        if (orderDb.getOrderState().equals(OrderConst.ORDER_STATE_20) || orderDb.getOrderState().equals(OrderConst.ORDER_STATE_30)) {
            //待发货状态下申请售后，需要锁定订单，不允许在申请售后，商家也不能发货
            Order updateOrder = new Order();
            updateOrder.setOrderId(orderDb.getOrderId());
            updateOrder.setLockState(orderDb.getLockState() + 1);//锁定状态+1
            int count = orderWriteMapper.updateByPrimaryKeySelective(updateOrder);
            AssertUtil.isTrue(count == 0, "订单锁定失败");
        }
    }

    /**
     * 根据申请件数获取退款金额
     *
     * @param orderProductId
     * @param applyNum
     * @return
     */
    public BigDecimal getReturnMoney(String orderProductId, Integer applyNum) {
        //根据orderProductId获取订单货品信息
        OrderProduct orderProductDb = orderProductReadMapper.getByPrimaryKey(orderProductId);

        //获取最大可退数量
        Integer orderTotalCanReturn = orderProductDb.getProductNum() - orderProductDb.getReturnNumber();
        //获取本货品总可退金额
        BigDecimal orderTotalReturnAmount = orderProductDb.getMoneyAmount()
                .subtract(orderProductDb.getMoneyAmount()
                        .multiply(new BigDecimal(orderProductDb.getReturnNumber()))
                        .divide(new BigDecimal(orderProductDb.getProductNum()), 2, RoundingMode.DOWN));
        //计算本次可退金额 :
        BigDecimal moneyCanReturn = orderTotalReturnAmount
                .multiply(new BigDecimal(applyNum))
                .divide(new BigDecimal(orderTotalCanReturn), 2, RoundingMode.DOWN);
        return moneyCanReturn;
    }

    /**
     * 用户发货
     *
     * @param memberId
     * @param afsSn
     * @param expressId
     * @param logisticsNumber
     * @return
     */
    public boolean memberDeliverGoods(Integer memberId, String afsSn, Integer expressId, String logisticsNumber) {
        //获取数据库中的售后信息
        OrderAfterService orderAfterServiceDb = this.getAfterServiceByAfsSn(afsSn);
        AssertUtil.isTrue(!orderAfterServiceDb.getMemberId().equals(memberId), "您无权操作");

        //获取快递公司信息
        Express express = expressReadMapper.getByPrimaryKey(expressId);
        AssertUtil.notNull(express, "获取物流信息为空");

        //更新售后服务信息
        OrderAfterService updateAfterService = new OrderAfterService();
        updateAfterService.setAfsId(orderAfterServiceDb.getAfsId());
        updateAfterService.setBuyerDeliverTime(new Date());
        updateAfterService.setBuyerExpressName(express.getExpressName());
        updateAfterService.setBuyerExpressCode(express.getExpressCode());
        updateAfterService.setBuyerExpressNumber(logisticsNumber);
        int update = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(updateAfterService);
        AssertUtil.isTrue(update == 0, "更新用户发货信息失败");

        if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_RETURN) {
            //退货发货，更新退货表
            OrderReturn orderReturnDb = this.getOrderReturnByAfsSn(afsSn);
            AssertUtil.isTrue(!(orderReturnDb.getState().equals(OrdersAfsConst.RETURN_STATE_200)
                    || orderReturnDb.getState().equals(OrdersAfsConst.RETURN_STATE_201)), "未到发货状态");

            OrderReturn updateReturn = new OrderReturn();
            updateReturn.setReturnId(orderReturnDb.getReturnId());
            updateReturn.setState(OrdersAfsConst.RETURN_STATE_102);
            update = orderReturnWriteMapper.updateByPrimaryKeySelective(updateReturn);
            AssertUtil.isTrue(update == 0, "更新退货信息失败");

            //记录售后日志
            OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
            afterSaleLog.setLogRole(OrderConst.LOG_ROLE_MEMBER);
            afterSaleLog.setLogUserId(Long.parseLong(String.valueOf(memberId)));
            afterSaleLog.setLogUserName(orderAfterServiceDb.getMemberName());
            afterSaleLog.setAfsSn(afsSn);
            afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_RETURN);
            afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_102 + "");
            afterSaleLog.setContent("买家退货给商家");
            afterSaleLog.setCreateTime(new Date());
            update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
            AssertUtil.isTrue(update == 0, "记录售后日志失败");

        } else if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_REPLACEMENT) {
            //换货发货，更新换货表
            OrderReplacement orderReplacementDb = this.getOrderReplacementByAfsSn(afsSn);
            AssertUtil.isTrue(!orderReplacementDb.getState().equals(OrdersAfsConst.REPLACEMENT_STATE_STORE_AUDIT_PASS), "未达发货状态");

            OrderReplacement updateReplacement = new OrderReplacement();
            updateReplacement.setReplacementId(orderReplacementDb.getReplacementId());
            updateReplacement.setState(OrdersAfsConst.REPLACEMENT_STATE_MEMBER_DELIVERY);
            update = orderReplacementWriteMapper.updateByPrimaryKeySelective(updateReplacement);
            AssertUtil.isTrue(update == 0, "更新换货信息失败");

            //记录售后日志
            OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
            afterSaleLog.setLogRole(OrderConst.LOG_ROLE_MEMBER);
            afterSaleLog.setLogUserName(orderAfterServiceDb.getMemberName());
            afterSaleLog.setLogUserId(Long.parseLong(String.valueOf(memberId)));
            afterSaleLog.setAfsSn(afsSn);
            afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_REPLACEMENT);
            afterSaleLog.setState(OrdersAfsConst.REPLACEMENT_STATE_MEMBER_DELIVERY + "");
            afterSaleLog.setContent("买家已发货，等待商家收货");
            afterSaleLog.setCreateTime(new Date());
            update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
            AssertUtil.isTrue(update == 0, "记录售后日志失败");

        } else {
            throw new MallException("售后类型错误");
        }
        return true;
    }

    /**
     * 系统自动处理售后单
     * 1.商户未审核的退货退款申请
     * 2.商户未审核的换货
     * 3.商户未审核的仅退款申请
     * 4.用户退货发货但是到时间限制商户还未收货
     *
     * @return
     */
    @Transactional
    public Map<String, List<OrderReturn>> jobSystemDealAfterService() {
        Map<String, List<OrderReturn>> resultMap = new HashMap<>();

        //退货收货集合
        List<OrderReturn> returnsReceiveList = new ArrayList<>();
        //退货退款申请集合
        List<OrderReturn> returnsApplyList = new ArrayList<>();
        //仅退款申请集合
        List<OrderReturn> refundApplyList = new ArrayList<>();

        /**-----------------------------------处理退货未收货自动处理-----------------------------------*/
        // 获取配置的相关自动天数期限设置
        int receiveDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_afs_seller_receive"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -receiveDay);

        String deliverTime = TimeUtil.getDateTimeString(cal.getTime());

        //获取状态为待收货的退货单
        OrderReturnExample example = new OrderReturnExample();
        example.setState(OrdersAfsConst.RETURN_STATE_102);
        example.setDeliverTimeEnd(deliverTime);
        List<OrderReturn> orderReturnList = orderReturnReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(orderReturnList)) {
            //单条数据处理异常不影响其他数据执行
            for (OrderReturn orderReturn : orderReturnList) {
                //获取会员发货时间时间
                OrderAfterServiceExample orderAfterServiceExample = new OrderAfterServiceExample();
                orderAfterServiceExample.setAfsSn(orderReturn.getAfsSn());
                List<OrderAfterService> afterServiceList = orderAfterServiceReadMapper.listByExample(orderAfterServiceExample);
                if (CollectionUtils.isEmpty(afterServiceList)) {
                    continue;
                }
                OrderAfterService orderAfterServiceDb = afterServiceList.get(0);

                //说明需要自动处理确认收货
                //更新退货状态
                OrderReturn orderReturnUpdate = new OrderReturn();
                orderReturnUpdate.setReturnId(orderReturn.getReturnId());
                orderReturnUpdate.setState(OrdersAfsConst.RETURN_STATE_203);
                int update = orderReturnWriteMapper.updateByPrimaryKeySelective(orderReturnUpdate);
                AssertUtil.notNull(update == 0, "修改系统自动确认收货状态信息失败");

                //更新售后服务单时间
                OrderAfterService orderAfterServiceUpdate = new OrderAfterService();
                orderAfterServiceUpdate.setAfsId(orderAfterServiceDb.getAfsId());
                orderAfterServiceUpdate.setStoreReceiveTime(new Date());
                update = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(orderAfterServiceUpdate);
                AssertUtil.notNull(update == 0, "修改系统自动确认收货时间失败");

                //记录售后日志
                OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
                afterSaleLog.setLogRole(OrderConst.LOG_ROLE_ADMIN);
                afterSaleLog.setLogUserId(0L);
                afterSaleLog.setLogUserName("系统自动处理");
                afterSaleLog.setAfsSn(orderAfterServiceDb.getAfsSn());
                afterSaleLog.setAfsType(orderAfterServiceDb.getAfsType());
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_203 + "");
                afterSaleLog.setContent("系统自动确认收货");
                afterSaleLog.setCreateTime(new Date());
                update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
                AssertUtil.notNull(update == 0, "记录售后服务操作日志失败");

                returnsReceiveList.add(orderReturn);
            }
        }
        //商家处理申请时间
        int auditDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_afs_seller_audit"));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -auditDay);

        /**-----------------------------------自动处理未处理的退货退款申请-----------------------------------*/
        //获取所有未处理的退货退款申请
        OrderReturnExample orderReturnExample = new OrderReturnExample();
        orderReturnExample.setState(OrdersAfsConst.RETURN_STATE_101);
        orderReturnExample.setApplyTimeBefore(calendar.getTime());
        List<OrderReturn> returnList = orderReturnReadMapper.listByExample(orderReturnExample);
        if (!CollectionUtils.isEmpty(returnList)) {
            // 单条数据处理异常不影响其他数据执行
            for (OrderReturn orderReturn : returnList) {
                //获取对应的售后单号
                OrderAfterServiceExample orderAfterServiceExample = new OrderAfterServiceExample();
                orderAfterServiceExample.setAfsSn(orderReturn.getAfsSn());
                List<OrderAfterService> afterServiceList = orderAfterServiceReadMapper.listByExample(orderAfterServiceExample);
                if (CollectionUtils.isEmpty(afterServiceList)) {
                    continue;
                }
                OrderAfterService orderAfterServiceDb = afterServiceList.get(0);

                //更新用户申请的状态
                OrderReturn orderReturnUpdate = new OrderReturn();
                orderReturnUpdate.setReturnId(orderReturn.getReturnId());
                orderReturnUpdate.setState(OrdersAfsConst.RETURN_STATE_201);
                int update = orderReturnWriteMapper.updateByPrimaryKeySelective(orderReturnUpdate);
                AssertUtil.notNull(update == 0, "修改系统自动确认同意退货退款申请状态失败");

                //更新售后服务单时间
                OrderAfterService orderAfterServiceUpdate = new OrderAfterService();
                orderAfterServiceUpdate.setAfsId(orderAfterServiceDb.getAfsId());
                orderAfterServiceUpdate.setStoreAuditTime(new Date());
                update = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(orderAfterServiceUpdate);
                AssertUtil.notNull(update == 0, "修改系统自动确认同意退货退款审核时间失败");

                //记录售后日志
                OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
                afterSaleLog.setLogRole(OrderConst.LOG_ROLE_ADMIN);
                afterSaleLog.setLogUserId(0L);
                afterSaleLog.setLogUserName("系统自动处理");
                afterSaleLog.setAfsSn(orderAfterServiceDb.getAfsSn());
                afterSaleLog.setAfsType(orderAfterServiceDb.getAfsType());
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_201 + "");
                afterSaleLog.setContent("系统自动确认同意退货退款申请");
                afterSaleLog.setCreateTime(new Date());
                update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
                AssertUtil.notNull(update == 0, "记录售后服务操作日志失败");

                returnsApplyList.add(orderReturn);
            }
        }

        /**-----------------------------------自动处理未处理的仅退款申请-----------------------------------*/
        //获取所有未处理的仅退款申请
        OrderReturnExample returnExample = new OrderReturnExample();
        returnExample.setState(OrdersAfsConst.RETURN_STATE_100);
        returnExample.setApplyTimeBefore(calendar.getTime());
        List<OrderReturn> orderReturns = orderReturnReadMapper.listByExample(returnExample);
        if (!CollectionUtils.isEmpty(orderReturns)) {
            // 单条数据处理异常不影响其他数据执行
            for (OrderReturn orderReturn : orderReturns) {
                //获取对应的售后单号
                OrderAfterServiceExample orderAfterServiceExample = new OrderAfterServiceExample();
                orderAfterServiceExample.setAfsSn(orderReturn.getAfsSn());
                List<OrderAfterService> afterServiceList = orderAfterServiceReadMapper.listByExample(orderAfterServiceExample);
                if (CollectionUtils.isEmpty(afterServiceList)) {
                    continue;
                }
                OrderAfterService orderAfterServiceDb = afterServiceList.get(0);

                //更新用户申请的状态
                OrderReturn orderReturnUpdate = new OrderReturn();
                orderReturnUpdate.setReturnId(orderReturn.getReturnId());
                orderReturnUpdate.setState(OrdersAfsConst.RETURN_STATE_200);
                int update = orderReturnWriteMapper.updateByPrimaryKeySelective(orderReturnUpdate);
                AssertUtil.notNull(update == 0, "修改系统自动确认同意仅退款申请状态失败");

                //更新售后服务单时间
                OrderAfterService orderAfterServiceUpdate = new OrderAfterService();
                orderAfterServiceUpdate.setAfsId(orderAfterServiceDb.getAfsId());
                orderAfterServiceUpdate.setStoreAuditTime(new Date());
                update = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(orderAfterServiceUpdate);
                AssertUtil.notNull(update == 0, "修改系统自动确认仅退款审核时间失败");

                //记录售后日志
                OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
                afterSaleLog.setLogRole(OrderConst.LOG_ROLE_ADMIN);
                afterSaleLog.setLogUserId(0L);
                afterSaleLog.setLogUserName("系统自动处理");
                afterSaleLog.setAfsSn(orderAfterServiceDb.getAfsSn());
                afterSaleLog.setAfsType(orderAfterServiceDb.getAfsType());
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_200 + "");
                afterSaleLog.setContent("系统自动确认同意仅退款申请");
                afterSaleLog.setCreateTime(new Date());
                update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
                AssertUtil.notNull(update == 0, "记录售后服务操作日志失败");

                refundApplyList.add(orderReturn);
            }
        }

        resultMap.put("returnsReceiveList", returnsReceiveList);
        resultMap.put("returnsApplyList", returnsApplyList);
        resultMap.put("refundApplyList", refundApplyList);
        return resultMap;
    }
}