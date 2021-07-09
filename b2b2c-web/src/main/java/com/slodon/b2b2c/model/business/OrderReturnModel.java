package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.example.*;
import com.slodon.b2b2c.business.pojo.*;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.business.*;
import com.slodon.b2b2c.dao.read.member.MemberReadMapper;
import com.slodon.b2b2c.dao.write.business.*;
import com.slodon.b2b2c.dao.write.member.MemberBalanceLogWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberIntegralLogWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberWriteMapper;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import com.slodon.b2b2c.member.pojo.MemberIntegralLog;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.SeckillOrderExtendModel;
import com.slodon.b2b2c.model.promotion.SeckillStageProductModel;
import com.slodon.b2b2c.promotion.example.SeckillOrderExtendExample;
import com.slodon.b2b2c.promotion.pojo.SeckillOrderExtend;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.starter.entity.SlodonRefundRequest;
import com.slodon.b2b2c.starter.entity.SlodonRefundResponse;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.starter.pay.SlodonPay;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.util.PayPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_MEMBER_MSG;

@Component
@Slf4j
public class OrderReturnModel {

    @Resource
    private OrderReturnReadMapper orderReturnReadMapper;
    @Resource
    private OrderReturnWriteMapper orderReturnWriteMapper;
    @Resource
    private OrderAfterServiceReadMapper orderAfterServiceReadMapper;
    @Resource
    private OrderAfterServiceWriteMapper orderAfterServiceWriteMapper;
    @Resource
    private OrderAfterSaleLogWriteMapper orderAfterSaleLogWriteMapper;
    @Resource
    private OrderReplacementReadMapper orderReplacementReadMapper;
    @Resource
    private OrderReplacementWriteMapper orderReplacementWriteMapper;
    @Resource
    private MemberReadMapper memberReadMapper;
    @Resource
    private MemberWriteMapper memberWriteMapper;
    @Resource
    private MemberIntegralLogWriteMapper memberIntegralLogWriteMapper;
    @Resource
    private MemberBalanceLogWriteMapper memberBalanceLogWriteMapper;
    @Resource
    private OrderProductReadMapper orderProductReadMapper;
    @Resource
    private OrderProductWriteMapper orderProductWriteMapper;
    @Resource
    private OrderReadMapper orderReadMapper;
    @Resource
    private OrderModel orderModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private SeckillOrderExtendModel seckillOrderExtendModel;
    @Resource
    private SeckillStageProductModel seckillStageProductModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private PayPropertiesUtil payPropertiesUtil;
    @Resource
    private SlodonPay slodonPay;

    /**
     * 新增订单退货表
     *
     * @param orderReturn
     * @return
     */
    public Integer saveOrderReturn(OrderReturn orderReturn) {
        int count = orderReturnWriteMapper.insert(orderReturn);
        if (count == 0) {
            throw new MallException("添加订单退货表失败，请重试");
        }
        return count;
    }

    /**
     * 根据returnId删除订单退货表
     *
     * @param returnId returnId
     * @return
     */
    public Integer deleteOrderReturn(Integer returnId) {
        if (StringUtils.isEmpty(returnId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderReturnWriteMapper.deleteByPrimaryKey(returnId);
        if (count == 0) {
            log.error("根据returnId：" + returnId + "删除订单退货表失败");
            throw new MallException("删除订单退货表失败,请重试");
        }
        return count;
    }

    /**
     * 根据returnId更新订单退货表
     *
     * @param orderReturn
     * @return
     */
    public Integer updateOrderReturn(OrderReturn orderReturn) {
        if (StringUtils.isEmpty(orderReturn.getReturnId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderReturnWriteMapper.updateByPrimaryKeySelective(orderReturn);
        if (count == 0) {
            log.error("根据returnId：" + orderReturn.getReturnId() + "更新订单退货表失败");
            throw new MallException("更新订单退货表失败,请重试");
        }
        return count;
    }

    /**
     * 根据returnId获取订单退货表详情
     *
     * @param returnId returnId
     * @return
     */
    public OrderReturn getOrderReturnByReturnId(Integer returnId) {
        return orderReturnReadMapper.getByPrimaryKey(returnId);
    }

    /**
     * 根据条件获取订单退货表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderReturn> getOrderReturnList(OrderReturnExample example, PagerInfo pager) {
        List<OrderReturn> orderReturnList;
        if (pager != null) {
            pager.setRowsCount(orderReturnReadMapper.countByExample(example));
            orderReturnList = orderReturnReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderReturnList = orderReturnReadMapper.listByExample(example);
        }
        return orderReturnList;
    }

    /**
     * 根据条件获取订单退货数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getOrderReturnCount(OrderReturnExample example) {
        return orderReturnReadMapper.countByExample(example);
    }

    /**
     * 根据售后单号获取退货信息
     *
     * @param afsSn
     * @return
     */
    public OrderReturn getOrderReturnByAfsSn(String afsSn) {
        OrderReturnExample example = new OrderReturnExample();
        example.setAfsSn(afsSn);
        List<OrderReturn> list = orderReturnReadMapper.listByExample(example);
        AssertUtil.notEmpty(list, "查询退货信息为空，请重试");
        return list.get(0);
    }

    /**
     * 根据售后单号获取售后服务信息
     *
     * @param afsSn
     * @return
     */
    public OrderAfterService getOrderAfterServiceByAfsSn(String afsSn) {
        OrderAfterServiceExample example = new OrderAfterServiceExample();
        example.setAfsSn(afsSn);
        List<OrderAfterService> list = orderAfterServiceReadMapper.listByExample(example);
        AssertUtil.notEmpty(list, "查询售后服务信息为空，请重试");
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
        AssertUtil.notEmpty(list, "查询换货信息为空，请重试");
        return list.get(0);
    }

    /**
     * 商家审核退款申请
     *
     * @param vendor
     * @param afsSn
     * @param isPass
     * @param remark
     * @param storeAddressId
     */
    public void afsStoreAudit(Vendor vendor, String afsSn, boolean isPass, String remark, Integer storeAddressId) {
        //查询售后服务信息
        OrderAfterService orderAfterServiceDb = this.getOrderAfterServiceByAfsSn(afsSn);
        AssertUtil.isTrue(!vendor.getStoreId().equals(orderAfterServiceDb.getStoreId()), "无权限");
        if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_RETURN) {
            //退货退款时店铺地址id必传
            AssertUtil.isTrue(isPass && StringUtil.isNullOrZero(storeAddressId), "请选择收货地址");
        }

        //记录售后日志
        OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
        afterSaleLog.setLogRole(OrderConst.LOG_ROLE_VENDOR);
        afterSaleLog.setLogUserId(vendor.getVendorId());
        afterSaleLog.setLogUserName(vendor.getVendorName());
        afterSaleLog.setAfsSn(afsSn);
        afterSaleLog.setCreateTime(new Date());

        int afsNun = 0;//售后数量
        BigDecimal afsAmount = BigDecimal.ZERO;//售后金额

        if (isPass) {
            //审核通过
            //更新售后服务信息
            OrderAfterService updateAfter = new OrderAfterService();
            updateAfter.setAfsId(orderAfterServiceDb.getAfsId());

            //商家收货地址
            updateAfter.setStoreAfsAddress(storeAddressId == null ? null : storeAddressId + "");

            updateAfter.setStoreAuditTime(new Date());
            updateAfter.setStoreRemark(remark);
            int update = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(updateAfter);
            AssertUtil.isTrue(update == 0, "更新售后服务信息失败");

            //更新退换货表
            if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_RETURN) {
                //类型为退货退款，更新退货表
                OrderReturn orderReturnDb = this.getOrderReturnByAfsSn(afsSn);
                AssertUtil.isTrue(!orderReturnDb.getState().equals(OrdersAfsConst.RETURN_STATE_101), "未到商家审核状态");

                OrderReturn updateReturn = new OrderReturn();
                updateReturn.setReturnId(orderReturnDb.getReturnId());
                updateReturn.setState(OrdersAfsConst.RETURN_STATE_201);
                update = orderReturnWriteMapper.updateByPrimaryKeySelective(updateReturn);
                AssertUtil.isTrue(update == 0, "更新退货信息失败");

                //日志信息
                afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_RETURN);
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_201 + "");
                afterSaleLog.setContent("商家同意退货退款申请");

                afsAmount = orderReturnDb.getReturnMoneyAmount().add(orderReturnDb.getReturnExpressAmount());
                afsNun = orderReturnDb.getReturnNum();
            } else if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_REPLACEMENT) {
                //类型为换货，更新换货表
                OrderReplacement orderReplacementDb = this.getOrderReplacementByAfsSn(afsSn);
                AssertUtil.isTrue(!orderReplacementDb.getState().equals(OrdersAfsConst.REPLACEMENT_STATE_MEMBER_APPLY), "未到商家审核状态");

                //更新换货信息
                OrderReplacement updateReplacement = new OrderReplacement();
                updateReplacement.setReplacementId(orderReplacementDb.getReplacementId());
                updateReplacement.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_AUDIT_PASS);
                update = orderReplacementWriteMapper.updateByPrimaryKeySelective(updateReplacement);
                AssertUtil.isTrue(update == 0, "更新换货信息失败");

                //日志信息
                afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_REPLACEMENT);
                afterSaleLog.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_AUDIT_PASS + "");
                afterSaleLog.setContent("商家审核通过,等待买家发货");

                afsNun = orderReplacementDb.getReplacementNum();
            } else if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_REFUND) {
                //类型为仅退款，更新退货表
                OrderReturn orderReturnDb = this.getOrderReturnByAfsSn(afsSn);
                AssertUtil.isTrue(!orderReturnDb.getState().equals(OrdersAfsConst.RETURN_STATE_100), "未到商家审核状态");

                OrderReturn updateReturn = new OrderReturn();
                updateReturn.setReturnId(orderReturnDb.getReturnId());
                updateReturn.setState(OrdersAfsConst.RETURN_STATE_200);
                update = orderReturnWriteMapper.updateByPrimaryKeySelective(updateReturn);
                AssertUtil.isTrue(update == 0, "更新退货信息失败");

                //日志信息
                afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_REFUND);
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_200 + "");
                afterSaleLog.setContent("商家同意退款申请");

                afsAmount = orderReturnDb.getReturnMoneyAmount().add(orderReturnDb.getReturnExpressAmount());
                afsNun = orderReturnDb.getReturnNum();
            } else {
                throw new MallException("售后类型不正确");
            }
        } else {
            //审核失败
            AssertUtil.isTrue(StringUtils.isEmpty(remark), "请填写拒绝原因");

            //更新售后信息
            OrderAfterService updateAfter = new OrderAfterService();
            updateAfter.setAfsId(orderAfterServiceDb.getAfsId());
            updateAfter.setStoreRemark(remark);
            updateAfter.setStoreAuditTime(new Date());
            int update = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(updateAfter);
            AssertUtil.isTrue(update == 0, "更新售后信息失败");

            //更新退换货表
            if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_RETURN
                    || orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_REFUND) {
                //类型为退货退款/仅退款，更新退货表
                OrderReturn orderReturnDb = this.getOrderReturnByAfsSn(afsSn);

                OrderReturn updateReturn = new OrderReturn();
                updateReturn.setReturnId(orderReturnDb.getReturnId());
                updateReturn.setState(OrdersAfsConst.RETURN_STATE_202);
                update = orderReturnWriteMapper.updateByPrimaryKeySelective(updateReturn);
                AssertUtil.isTrue(update == 0, "更新退货信息失败");

                //审核失败，修改订单锁定状态
                Order order = orderModel.getOrderByOrderSn(orderReturnDb.getOrderSn());
                Order orderUpdate = new Order();
                orderUpdate.setOrderSn(orderReturnDb.getOrderSn());
                orderUpdate.setLockState(order.getLockState() - 1);
                update = orderModel.updateOrder(orderUpdate);
                AssertUtil.isTrue(update == 0, "修改订单锁定状态失败");

                //审核失败，修改退货数量
                OrderProduct orderProductDb = orderProductReadMapper.getByPrimaryKey(orderAfterServiceDb.getOrderProductId());
                AssertUtil.notNull(orderProductDb, "订单货品不存在");
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrderProductId(orderAfterServiceDb.getOrderProductId());
                orderProduct.setReturnNumber(orderProductDb.getReturnNumber() - orderReturnDb.getReturnNum());
                update = orderProductWriteMapper.updateByPrimaryKeySelective(orderProduct);
                AssertUtil.isTrue(update == 0, "修改订单货品退货数量失败");

                //日志信息
                afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_RETURN);
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_202 + "");
                afterSaleLog.setContent("退款拒绝，拒绝原因：" + remark);

                afsAmount = orderReturnDb.getReturnMoneyAmount().add(orderReturnDb.getReturnExpressAmount());
                afsNun = orderReturnDb.getReturnNum();
            } else if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_REPLACEMENT) {
                //类型为换货，更新换货表
                OrderReplacement orderReplacementDb = this.getOrderReplacementByAfsSn(afsSn);

                //更新换货信息
                OrderReplacement updateReplacement = new OrderReplacement();
                updateReplacement.setReplacementId(orderReplacementDb.getReplacementId());
                updateReplacement.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_AUDIT_FAIL);
                update = orderReplacementWriteMapper.updateByPrimaryKeySelective(updateReplacement);
                AssertUtil.isTrue(update == 0, "更新换货信息失败");

                //日志信息
                afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_REPLACEMENT);
                afterSaleLog.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_AUDIT_FAIL + "");
                afterSaleLog.setContent("审核拒绝，拒绝原因：" + remark);

                afsNun = orderReplacementDb.getReplacementNum();
            } else {
                throw new MallException("售后类型不正确");
            }
        }
        int update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
        if (update == 0) {
            throw new MallException("记录售后日志信息失败");
        }

        //发送消息通知
        this.sendMsgAfterSale(orderAfterServiceDb, afsNun, afsAmount);
    }

    /**
     * 商家确认收货
     *
     * @param vendor
     * @param afsSn
     * @param isReceive
     * @param remark
     */
    public void afsStoreReceive(Vendor vendor, String afsSn, boolean isReceive, String remark) {
        //查询售后服务信息
        OrderAfterService orderAfterServiceDb = this.getOrderAfterServiceByAfsSn(afsSn);
        AssertUtil.isTrue(!vendor.getStoreId().equals(orderAfterServiceDb.getStoreId()), "无权限");

        int afsNun = 0;//售后数量
        BigDecimal afsAmount = BigDecimal.ZERO;//售后金额

        //更新售后服务信息
        OrderAfterService updateAfter = new OrderAfterService();
        updateAfter.setAfsId(orderAfterServiceDb.getAfsId());
        updateAfter.setStoreRemark(remark);
        updateAfter.setStoreReceiveTime(new Date());
        int update = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(updateAfter);
        AssertUtil.isTrue(update == 0, "更新售后服务信息失败");

        //记录售后日志
        OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
        afterSaleLog.setLogRole(OrderConst.LOG_ROLE_VENDOR);
        afterSaleLog.setLogUserId(vendor.getVendorId());
        afterSaleLog.setLogUserName(vendor.getVendorName());
        afterSaleLog.setAfsSn(afsSn);
        afterSaleLog.setCreateTime(new Date());

        if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_RETURN) {
            //退货退款
            OrderReturn orderReturnDb = this.getOrderReturnByAfsSn(afsSn);
            AssertUtil.isTrue(!orderReturnDb.getState().equals(OrdersAfsConst.RETURN_STATE_102), "未到商家收货状态");

            //更新退货状态
            OrderReturn updateReturn = new OrderReturn();
            updateReturn.setReturnId(orderReturnDb.getReturnId());
            if (isReceive) {
                //商家收货
                updateReturn.setState(OrdersAfsConst.RETURN_STATE_203);
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_203 + "");
                afterSaleLog.setContent("商家确认收货");
            } else {
                AssertUtil.isTrue(StringUtils.isEmpty(remark), "请填写拒收原因");

                updateReturn.setState(OrdersAfsConst.RETURN_STATE_202);
                afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_202 + "");
                afterSaleLog.setContent("商家拒收，拒收原因：" + remark);
            }
            afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_RETURN);
            update = orderReturnWriteMapper.updateByPrimaryKeySelective(updateReturn);
            AssertUtil.isTrue(update == 0, "更新退货信息失败");

            afsAmount = orderReturnDb.getReturnMoneyAmount().add(orderReturnDb.getReturnExpressAmount());
            afsNun = orderReturnDb.getReturnNum();

        } else if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_REPLACEMENT) {
            //换货
            OrderReplacement orderReplacementDb = this.getOrderReplacementByAfsSn(afsSn);
            AssertUtil.isTrue(!orderReplacementDb.getState().equals(OrdersAfsConst.REPLACEMENT_STATE_MEMBER_DELIVERY), "未到商家收货状态");

            //更新换货状态
            OrderReplacement updateReplacement = new OrderReplacement();
            updateReplacement.setReplacementId(orderReplacementDb.getReplacementId());
            if (isReceive) {
                //商家收货
                updateReplacement.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_RECEIVE);
                afterSaleLog.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_RECEIVE + "");
                afterSaleLog.setContent("商家已收货，待发货");
            } else {
                AssertUtil.isTrue(StringUtils.isEmpty(remark), "请填写拒收原因");

                updateReplacement.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_REJECTION);
                afterSaleLog.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_REJECTION + "");
                afterSaleLog.setContent("商家拒收，拒收原因：" + remark);
            }
            afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_REPLACEMENT);
            update = orderReplacementWriteMapper.updateByPrimaryKeySelective(updateReplacement);
            AssertUtil.isTrue(update == 0, "更新换货信息失败");

            afsNun = orderReplacementDb.getReplacementNum();
        } else {
            throw new MallException("售后类型错误");
        }

        update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
        AssertUtil.isTrue(update == 0, "记录售后日志信息失败");

        //发送消息通知
        this.sendMsgAfterSale(orderAfterServiceDb, afsNun, afsAmount);
    }

    //region ---------------平台确认退款 start-----------------------------------------------------------------------------------

    /**
     * 平台确认退款
     *
     * @param admin
     * @param afsSns
     * @param remark
     */
    @Transactional
    public void adminConfirmRefund(Admin admin, String afsSns, String remark) {
        String[] afsSnArr = afsSns.split(",");
        for (String afsSn : afsSnArr) {
            //获取售后服务信息
            OrderAfterService orderAfterServiceDb = this.getOrderAfterServiceByAfsSn(afsSn);
            //获取退货信息
            OrderReturn orderReturnDb = this.getOrderReturnByAfsSn(afsSn);
            AssertUtil.isTrue(!(!orderReturnDb.getState().equals(OrdersAfsConst.RETURN_STATE_200)
                    || !orderReturnDb.getState().equals(OrdersAfsConst.RETURN_STATE_203)), "订单未处于可退款状态");

            //平台确认退款，更新售后信息表
            OrderAfterService updateService = new OrderAfterService();
            updateService.setAfsId(orderAfterServiceDb.getAfsId());
            updateService.setPlatformAuditTime(new Date());
            updateService.setPlatformRemark(remark);
            int update = orderAfterServiceWriteMapper.updateByPrimaryKeySelective(updateService);
            AssertUtil.notNull(update == 0, "更新售后服务信息失败");

            //获取退款路径：原路退回or退款到余额
            int returnMoneyType = Integer.parseInt(stringRedisTemplate.opsForValue().get("refund_setting_switch"));

            //更新退货表
            OrderReturn updateReturn = new OrderReturn();
            updateReturn.setReturnId(orderReturnDb.getReturnId());
            updateReturn.setReturnMoneyType(returnMoneyType);
            updateReturn.setState(OrdersAfsConst.RETURN_STATE_300);
            update = orderReturnWriteMapper.updateByPrimaryKeySelective(updateReturn);
            AssertUtil.notNull(update == 0, "更新退货信息失败");

            //更新订单状态
            OrderAfterServiceExample orderAfterServiceExample = new OrderAfterServiceExample();
            orderAfterServiceExample.setOrderSn(orderAfterServiceDb.getOrderSn());
            int count = orderAfterServiceReadMapper.countByExample(orderAfterServiceExample);
            orderAfterServiceExample.setAfsType(OrdersAfsConst.AFS_TYPE_RETURN);
            orderAfterServiceExample.setGoodsState(OrdersAfsConst.GOODS_STATE_YES);
            int countAfter = orderAfterServiceReadMapper.countByExample(orderAfterServiceExample);
            if (count == countAfter) {
                Boolean orderState = true;
                OrderProductExample orderProductExample = new OrderProductExample();
                orderProductExample.setOrderSn(orderAfterServiceDb.getOrderSn());
                List<OrderProduct> orderProductList = orderProductReadMapper.listByExample(orderProductExample);
                for (OrderProduct orderProduct : orderProductList) {
                    if (!orderProduct.getProductNum().equals(orderProduct.getReturnNumber())) {
                        orderState = false;
                    }
                }
                if (orderState) {
                    //订单状态-已关闭
                    Order order = new Order();
                    order.setOrderState(OrderConst.ORDER_STATE_50);
                    OrderExample orderExample = new OrderExample();
                    orderExample.setOrderSn(orderAfterServiceDb.getOrderSn());
                    Integer integer = orderModel.updateOrderByExample(order, orderExample);
                    AssertUtil.notNullOrZero(integer, "更新订单状态失败,请重试");
                }
            }

            //记录售后日志
            OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
            afterSaleLog.setLogRole(OrderConst.LOG_ROLE_ADMIN);
            afterSaleLog.setLogUserId(Long.parseLong(String.valueOf(admin.getAdminId())));
            afterSaleLog.setLogUserName(admin.getAdminName());
            afterSaleLog.setAfsSn(afsSn);
            afterSaleLog.setAfsType(orderAfterServiceDb.getAfsType());
            afterSaleLog.setState(OrdersAfsConst.RETURN_STATE_300 + "");
            afterSaleLog.setContent("平台确认退款");
            afterSaleLog.setCreateTime(new Date());
            update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
            AssertUtil.notNull(update == 0, "记录售后服务操作日志失败");

            this.doReturnMoney(orderReturnDb, orderAfterServiceDb, admin, returnMoneyType);

            int afsNun = orderReturnDb.getReturnNum();//售后数量
            BigDecimal afsAmount = orderReturnDb.getReturnMoneyAmount().add(orderReturnDb.getReturnExpressAmount());//售后金额
            //发送消息通知
            this.sendMsgAfterSale(orderAfterServiceDb, afsNun, afsAmount);
        }
    }

    private void doReturnMoney(OrderReturn orderReturnDb, OrderAfterService orderAfterServiceDb, Admin admin, int returnMoneyType) {
        //查询订单信息
        Order orderDb = orderModel.getOrderByOrderSn(orderAfterServiceDb.getOrderSn());
        //查询会员信息
        Member memberDb = memberReadMapper.getByPrimaryKey(orderAfterServiceDb.getMemberId());
        AssertUtil.notNull(memberDb, "获取会员信息为空，请重试");

        /**
         * 平台打款逻辑（原路退回）：
         * 查询订单支付方式
         * 1 货到付款，直接修改退货状态
         * 2 余额支付，直接退款到余额，修改状态
         * 3 三方支付：
         *      3.1 未使用余额，直接退回到三方支付原账户
         *      3.2 使用了余额（组合支付），查询所有已经退款的退货单，退款总
         */

        //实际退款金额要加上退的运费
        BigDecimal returnMoneyAll = orderReturnDb.getReturnMoneyAmount().add(orderReturnDb.getReturnExpressAmount());
        //不是货到付款，并且退款金额大于0才执行打款
        if (orderDb.getOrderType() != OrderConst.ORDER_TYPE_0 && returnMoneyAll.compareTo(BigDecimal.ZERO) > 0) {
            if (returnMoneyType == OrdersAfsConst.MONEY_RETURN_TYPE_TO_BALANCE) {
                //退回到余额，修改会员余额，记录余额变动日志
                this.returnToBalance(memberDb, returnMoneyAll, admin, orderReturnDb);
            } else {
                //原路退回：总体原则为先退余额支付金额，再退三方支付金额
                //判断订单已退金额与余额支付金额的关系
                //1.已退大于等于余额支付-调用三方的原路退回
                //2.已退小于余额支付，先将一部分或者全部退款退至余额，再退剩余部分到三方
                BigDecimal balanceAmount = orderDb.getBalanceAmount();//余额支付金额
                BigDecimal refundAmount = orderDb.getRefundAmount();//已退款金额
                BigDecimal moneyReturnAmount = returnMoneyAll;//本次退款金额

                if (refundAmount.compareTo(balanceAmount) >= 0) {
                    //1.已退大于等于余额支付-调用三方的原路退回
                    this.returnToCash(orderDb, orderReturnDb.getAfsSn(), moneyReturnAmount);
                } else {
                    //2.已退小于余额支付，先将一部分或者全部退款退至余额，再退剩余部分到三方
                    //余额支付-已退  与  本次退款金额比较
                    if (balanceAmount.subtract(refundAmount).compareTo(moneyReturnAmount) >= 0) {
                        //本次退款全部退至余额
                        this.returnToBalance(memberDb, moneyReturnAmount, admin, orderReturnDb);
                    } else {
                        //一部分退余额，一部分退三方
                        //退余额
                        this.returnToBalance(memberDb, balanceAmount.subtract(refundAmount), admin, orderReturnDb);
                        //退三方
                        this.returnToCash(orderDb, orderReturnDb.getAfsSn(), moneyReturnAmount.subtract(balanceAmount.subtract(refundAmount)));
                    }
                }
            }

            //如果全部退货，则修改订单状态
            boolean isCancel = true;
            //查询订单货品明细
            OrderProductExample orderProductExample = new OrderProductExample();
            orderProductExample.setOrderSn(orderAfterServiceDb.getOrderSn());
            List<OrderProduct> orderProductList = orderProductReadMapper.listByExample(orderProductExample);
            AssertUtil.notEmpty(orderProductList, "获取订单货品明细为空");
            for (OrderProduct orderProduct : orderProductList) {
                if (!orderProduct.getReturnNumber().equals(orderProduct.getProductNum())) {
                    isCancel = false;
                    break;
                }
            }
            //修改订单退款金额和锁定状态
            Order updateOrder = new Order();
            updateOrder.setOrderSn(orderDb.getOrderSn());
            updateOrder.setRefundAmount(orderDb.getRefundAmount().add(orderReturnDb.getReturnMoneyAmount()));
            if (isCancel) {
                updateOrder.setOrderState(OrderConst.ORDER_STATE_0);
            }
            updateOrder.setLockState(orderDb.getLockState() - 1);
            orderModel.updateOrder(updateOrder);
        }

        //记录积分消耗日志
        if (orderReturnDb.getReturnIntegralAmount() != 0) {
            //记录退货增加积分记录
            MemberIntegralLog integralLog = new MemberIntegralLog();
            integralLog.setMemberId(memberDb.getMemberId());
            integralLog.setMemberName(memberDb.getMemberName());
            integralLog.setValue(orderReturnDb.getReturnIntegralAmount());
            integralLog.setCreateTime(new Date());
            integralLog.setType(MemberGradeConst.MEMBER_GRD_INT_LOG_OPT_T_8);
            integralLog.setDescription("退货返还积分(售后单号:" + orderReturnDb.getAfsSn() + ")");
            integralLog.setRefCode(orderReturnDb.getAfsSn());
            integralLog.setOptId(0);
            integralLog.setOptName("");
            int result = memberIntegralLogWriteMapper.insert(integralLog);
            AssertUtil.isTrue(result == 0, "记录积分返还日志失败");
        }

        if (orderReturnDb.getReturnIntegralAmount() != 0) {
            //记录订单货品明细退货追回积分
            MemberIntegralLog integralLog = new MemberIntegralLog();
            integralLog.setMemberId(memberDb.getMemberId());
            integralLog.setMemberName(memberDb.getMemberName());
            integralLog.setValue(orderReturnDb.getReturnIntegralAmount());
            integralLog.setCreateTime(new Date());
            integralLog.setType(MemberGradeConst.MEMBER_GRD_INT_LOG_OPT_T_12);
            integralLog.setDescription("订单货品明细退货追回积分(售后单号:" + orderReturnDb.getAfsSn() + ")");
            integralLog.setRefCode(orderReturnDb.getAfsSn());
            integralLog.setOptId(0);
            integralLog.setOptName("");
            int result = memberIntegralLogWriteMapper.insert(integralLog);
            AssertUtil.isTrue(result == 0, "记录积分追回日志失败");
        }

        //计算要退的积分,如果为负数，说明要扣除积分
        int returnIntegral = orderReturnDb.getDeductIntegralAmount();
        if (returnIntegral != 0) {
            //更新会员积分
            Member member = new Member();
            member.setMemberId(memberDb.getMemberId());
            member.setMemberIntegral(memberDb.getMemberIntegral() + returnIntegral);
            member.setUpdateTime(new Date());
            int update = memberWriteMapper.updateByPrimaryKeySelective(member);
            AssertUtil.isTrue(update == 0, "更新会员积分信息失败");
        }

        //退还优惠券
        if (!StringUtil.isEmpty(orderReturnDb.getReturnVoucherCode())) {
            orderModel.doReturnCoupon(orderReturnDb.getReturnVoucherCode(), orderReturnDb.getOrderSn());
        }

        //修改库存（仅退款并且未收到货）
        if (orderAfterServiceDb.getAfsType() == OrdersAfsConst.AFS_TYPE_REFUND && orderAfterServiceDb.getGoodsState() == OrdersAfsConst.GOODS_STATE_NO) {
            this.increaseStock(orderAfterServiceDb.getGoodsId(), orderAfterServiceDb.getOrderProductId(), orderAfterServiceDb.getAfsNum(), orderDb.getOrderType());
        }
    }

    /**
     * 仅退款未收货-更新商品库存
     *
     * @param goodsId        商品id
     * @param orderProductId 订单货品id
     * @param buyNum         购买数量
     */
    public void increaseStock(Long goodsId, Long orderProductId, Integer buyNum, Integer orderType) {
        Goods goodsDb = goodsModel.getGoodsByGoodsId(goodsId);
        AssertUtil.notNull(goodsDb, "商品不存在");
        OrderProduct orderProduct = orderProductReadMapper.getByPrimaryKey(orderProductId);
        AssertUtil.notNull(orderProduct, "订单货品不存在");
        Product productDb = productModel.getProductByProductId(orderProduct.getProductId());
        AssertUtil.notNull(productDb, "货品不存在");
        //增加sku库存
        Product updateProduct = new Product();
        updateProduct.setProductId(productDb.getProductId());
        updateProduct.setProductStock(productDb.getProductStock() + buyNum);
        productModel.updateProduct(updateProduct);
        //修改默认货品库存
        if (goodsDb.getDefaultProductId().equals(productDb.getProductId())) {
            //此次购买的是默认货品，更新商品库存
            Goods updateGoods = new Goods();
            updateGoods.setGoodsId(goodsDb.getGoodsId());
            updateGoods.setGoodsStock(updateProduct.getProductStock());
            updateGoods.setUpdateTime(new Date());
            goodsModel.updateGoods(goodsDb);
        }
        //秒杀订单
        if (orderType.equals(PromotionConst.PROMOTION_TYPE_104)) {
            //查询秒杀订单扩展信息
            SeckillOrderExtendExample extendExample = new SeckillOrderExtendExample();
            extendExample.setOrderSn(orderProduct.getOrderSn());
            List<SeckillOrderExtend> seckillOrderExtendList = seckillOrderExtendModel.getSeckillOrderExtendList(extendExample, null);
            AssertUtil.notEmpty(seckillOrderExtendList, "秒杀订单扩展信息不存在");
            SeckillOrderExtend seckillOrderExtend = seckillOrderExtendList.get(0);
            //查询秒杀商品信息
            SeckillStageProduct stageProduct = seckillStageProductModel.getSeckillStageProductByStageProductId(seckillOrderExtend.getStageProductstageProductId());
            AssertUtil.notNull(stageProduct, "秒杀商品不存在");
            Date date = new Date();
            //未结束修改redis中的秒杀库存
            if (!date.after(stageProduct.getEndTime())) {
                String stockKey = RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + productDb.getProductId();
                String memberLimitKey = RedisConst.REDIS_SECKILL_MEMBER_BUY_NUM_PREFIX + productDb.getProductId() + "_" + orderProduct.getMemberId();
                if (stringRedisTemplate.opsForValue().get(stockKey) != null) {
                    //加库存
                    stringRedisTemplate.opsForValue().increment(stockKey, buyNum);
                }
                if (stringRedisTemplate.opsForValue().get(memberLimitKey) != null) {
                    stringRedisTemplate.opsForValue().decrement(memberLimitKey, buyNum);
                }
            }
            //修改秒杀商品表的已购买人数和购买数量
            SeckillStageProduct seckillStageProduct = new SeckillStageProduct();
            seckillStageProduct.setStageProductId(stageProduct.getStageProductId());
            seckillStageProduct.setBuyerCount(stageProduct.getBuyerCount() - 1);
            seckillStageProduct.setBuyQuantity(stageProduct.getBuyQuantity() - buyNum);
            seckillStageProductModel.updateSeckillStageProduct(seckillStageProduct);
        }
    }

    /**
     * 退款至余额
     *
     * @param memberDb      退款前的会员信息
     * @param returnMoney   退款金额
     * @param admin         操作人
     * @param orderReturnDb 售后单
     */
    public void returnToBalance(Member memberDb, BigDecimal returnMoney, Admin admin, OrderReturn orderReturnDb) {
        //记录余额变动日志
        MemberBalanceLog balanceLog = new MemberBalanceLog();
        balanceLog.setMemberId(memberDb.getMemberId());
        balanceLog.setMemberName(memberDb.getMemberName());
        balanceLog.setAfterChangeAmount(memberDb.getBalanceAvailable().add(returnMoney));
        balanceLog.setChangeValue(returnMoney);
        balanceLog.setFreezeAmount(memberDb.getBalanceFrozen());
        balanceLog.setFreezeValue(BigDecimal.ZERO);
        balanceLog.setCreateTime(new Date());
        balanceLog.setType(MemberConst.TYPE_2);
        if (orderReturnDb.getReturnType() == OrdersAfsConst.RETURN_TYPE_1) {
            balanceLog.setDescription("退款成功，售后单号：" + orderReturnDb.getAfsSn());
        } else {
            balanceLog.setDescription("退货退款成功，售后单号：" + orderReturnDb.getAfsSn());
        }
        balanceLog.setAdminId(admin.getAdminId());
        balanceLog.setAdminName(admin.getAdminName());
        int update = memberBalanceLogWriteMapper.insert(balanceLog);
        AssertUtil.isTrue(update == 0, "记录用户余额变动失败");

        //修改会员余额
        Member member = new Member();
        member.setMemberId(memberDb.getMemberId());
        member.setBalanceAvailable(memberDb.getBalanceAvailable().add(returnMoney));
        member.setUpdateTime(new Date());
        update = memberWriteMapper.updateByPrimaryKeySelective(member);
        AssertUtil.isTrue(update == 0, "修改用户余额失败");

        //发送余额变动消息通知
        memberDb.setBalanceAvailable(memberDb.getBalanceAvailable().add(returnMoney));
        this.sendMsgAccountChange(balanceLog);
    }

    /**
     * 退款至三方账户
     *
     * @param orderDb
     * @param afsSn
     * @param moneyReturn
     */
    public void returnToCash(Order orderDb, String afsSn, BigDecimal moneyReturn) {
        SlodonRefundRequest refundRequest = new SlodonRefundRequest();
        //执行原路退回
        if (orderDb.getPaymentCode().toLowerCase().contains(PayConst.METHOD_ALIPAY.toLowerCase())) {
            //支付宝退款
            refundRequest.setAlipayProperties(payPropertiesUtil.getAliPayProperties());
        } else if (orderDb.getPaymentCode().toLowerCase().contains(PayConst.METHOD_WX.toLowerCase())) {
            //微信退款
            refundRequest.setWxPayProperties(payPropertiesUtil.getWxPayProperties(orderDb.getPaymentCode().replace(PayConst.METHOD_WX.toUpperCase(), ""), 1));

            //微信需要设置原支付金额
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
                refundRequest.setTotalAmount(new BigDecimal("0.01"));
            } else {
                OrderExample orderExample = new OrderExample();
                orderExample.setPaySn(orderDb.getPaySn());
                List<Order> orderList = orderReadMapper.listByExample(orderExample);
                BigDecimal totalPayAmount = new BigDecimal("0.00");//支付单号下总支付金额
                for (Order order : orderList) {
                    totalPayAmount = totalPayAmount.add(order.getPayAmount());
                }
                refundRequest.setTotalAmount(totalPayAmount);
            }
        } else {
            throw new MallException("支付方式未开启");
        }
        refundRequest.setPaySn(orderDb.getPaySn());
        refundRequest.setRefundSn(afsSn);
        if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
            refundRequest.setRefundAmount(new BigDecimal("0.01"));
        } else {
            refundRequest.setRefundAmount(moneyReturn);
        }
        try {
            SlodonRefundResponse refundResponse = slodonPay.refund(refundRequest, orderDb.getPaymentCode());
            if (!refundResponse.getSuccess()) {
                log.error("原路退回失败：" + refundResponse.getMsg());
                throw new MallException("原路退回失败");
            }
        } catch (Exception e) {
            log.error("原路退回失败：", e);
            throw new MallException("原路退回失败");
        }
    }

    //endregion ---------------------------------------平台确认退款 end --------------------------------------------------------

    /**
     * 发送售后消息通知
     *
     * @param afterService 售后信息
     * @param afsNum       售后数量
     * @param afsAmount    售后金额
     */
    public void sendMsgAfterSale(OrderAfterService afterService, Integer afsNum, BigDecimal afsAmount) {
        String msgType = "";
        if (afterService.getAfsType().equals(OrdersAfsConst.AFS_TYPE_RETURN)) {
            msgType = "return_news";
        } else if (afterService.getAfsType().equals(OrdersAfsConst.AFS_TYPE_REPLACEMENT)) {
            msgType = "replacement_news";
        } else {
            msgType = "refund_news";
        }
        //添加消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("afsSn", afterService.getAfsSn()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的售后单有新的变化。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", afterService.getAfsSn()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", afsAmount + "元"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", afsNum + "件"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", afterService.getApplyReasonContent()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("remark", "点击【详情】查看售后详情"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("url", DomainUrlUtil.SLD_H5_URL + "/#/pages/order/refundDetail?afsSn=" + afterService.getAfsSn()));
        //发送消息通知
        String msgLinkInfo = "{\"type\":\"" + msgType + "\",\"afsSn\":\"" + afterService.getAfsSn() + "\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, null, afterService.getMemberId(), MemberTplConst.AFTER_SALE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }

    /**
     * 发送余额变动消息通知
     *
     * @param memberBalanceLog 余额变动信息
     */
    public void sendMsgAccountChange(MemberBalanceLog memberBalanceLog) {
        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("description", "订单退款"));
        messageSendPropertyList.add(new MessageSendProperty("availableBalance", memberBalanceLog.getAfterChangeAmount().toString()));
        messageSendPropertyList.add(new MessageSendProperty("frozenBalance", memberBalanceLog.getFreezeAmount().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了资金变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", "订单退款"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", memberBalanceLog.getChangeValue().toString()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", TimeUtil.getDateTimeString(memberBalanceLog.getCreateTime())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", memberBalanceLog.getAfterChangeAmount().toString()));
        String msgLinkInfo = "{\"type\":\"balance_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", memberBalanceLog.getMemberId(), MemberTplConst.BALANCE_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }
}