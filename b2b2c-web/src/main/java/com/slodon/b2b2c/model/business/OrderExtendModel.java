package com.slodon.b2b2c.model.business;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitParamDTO;
import com.slodon.b2b2c.business.example.OrderExtendExample;
import com.slodon.b2b2c.business.pojo.OrderExtend;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.business.OrderExtendReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberInvoiceReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderExtendWriteMapper;
import com.slodon.b2b2c.member.pojo.MemberAddress;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.alibaba.fastjson.serializer.SerializerFeature.MapSortField;
import static com.alibaba.fastjson.serializer.SerializerFeature.SortField;

@Component
@Slf4j
public class OrderExtendModel {

    @Resource
    private OrderExtendReadMapper orderExtendReadMapper;
    @Resource
    private OrderExtendWriteMapper orderExtendWriteMapper;
    @Resource
    private MemberInvoiceReadMapper memberInvoiceReadMapper;

    /**
     * 新增订单信息扩展表
     *
     * @param orderExtend
     * @return
     */
    public Integer saveOrderExtend(OrderExtend orderExtend) {
        int count = orderExtendWriteMapper.insert(orderExtend);
        if (count == 0) {
            throw new MallException("添加订单信息扩展表失败，请重试");
        }
        return count;
    }

    /**
     * 根据extendId删除订单信息扩展表
     *
     * @param extendId extendId
     * @return
     */
    public Integer deleteOrderExtend(Integer extendId) {
        if (StringUtils.isEmpty(extendId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderExtendWriteMapper.deleteByPrimaryKey(extendId);
        if (count == 0) {
            log.error("根据extendId：" + extendId + "删除订单信息扩展表失败");
            throw new MallException("删除订单信息扩展表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId更新订单信息扩展表
     *
     * @param orderExtend
     * @return
     */
    public Integer updateOrderExtend(OrderExtend orderExtend) {
        if (StringUtils.isEmpty(orderExtend.getExtendId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderExtendWriteMapper.updateByPrimaryKeySelective(orderExtend);
        if (count == 0) {
            log.error("根据extendId：" + orderExtend.getExtendId() + "更新订单信息扩展表失败");
            throw new MallException("更新订单信息扩展表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId获取订单信息扩展表详情
     *
     * @param extendId extendId
     * @return
     */
    public OrderExtend getOrderExtendByExtendId(Integer extendId) {
        return orderExtendReadMapper.getByPrimaryKey(extendId);
    }

    /**
     * 根据orderSn获取订单信息扩展详情
     *
     * @param orderSn
     * @return
     */
    public OrderExtend getOrderExtendByOrderSn(String orderSn) {
        OrderExtendExample example = new OrderExtendExample();
        example.setOrderSn(orderSn);
        List<OrderExtend> extendList = orderExtendReadMapper.listByExample(example);
        AssertUtil.notEmpty(extendList, "获取订单扩展信息为空，请重试");
        return extendList.get(0);
    }

    /**
     * 根据条件获取订单信息扩展表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderExtend> getOrderExtendList(OrderExtendExample example, PagerInfo pager) {
        List<OrderExtend> orderExtendList;
        if (pager != null) {
            pager.setRowsCount(orderExtendReadMapper.countByExample(example));
            orderExtendList = orderExtendReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderExtendList = orderExtendReadMapper.listByExample(example);
        }
        return orderExtendList;
    }

    /**
     * 根据example条件更新订单信息扩展表
     *
     * @param orderExtend
     * @param example
     * @return
     */
    public Integer updateOrderExtendByExample(OrderExtend orderExtend, OrderExtendExample example) {
        return orderExtendWriteMapper.updateByExampleSelective(orderExtend, example);
    }


    /**
     * 提交订单-保存订单扩展信息
     *
     * @param orderSn             订单号
     * @param orderInfo           订单信息
     * @param memberAddress       收货地址
     * @param orderSubmitParamDTO 用户提交参数
     */
    public void insertOrderExtend(String orderSn, OrderSubmitDTO.OrderInfo orderInfo, MemberAddress memberAddress, OrderSubmitParamDTO orderSubmitParamDTO) {
        //店铺提交参数
        OrderSubmitParamDTO.StoreInfo storeInfo = orderSubmitParamDTO.getStoreInfoByStoreId(orderInfo.getStoreId());

        OrderExtend orderExtend = new OrderExtend();
        orderExtend.setOrderSn(orderSn);
        orderExtend.setStoreId(orderInfo.getStoreId());
        orderExtend.setOrderRemark(storeInfo == null ? null : storeInfo.getRemark());
        orderExtend.setVoucherPrice(orderInfo.getVoucherAmount());
        orderExtend.setVoucherCode(orderInfo.getVoucherCode());
        orderExtend.setOrderFrom(orderSubmitParamDTO.getOrderFrom());
        orderExtend.setReceiverProvinceCode(memberAddress.getProvinceCode());
        orderExtend.setReceiverCityCode(memberAddress.getCityCode());
        orderExtend.setReceiverDistrictCode(memberAddress.getDistrictCode());
        orderExtend.setReceiverName(memberAddress.getMemberName());
        orderExtend.setReceiverInfo(memberAddress.getAddressAll() + memberAddress.getDetailAddress());
        if (storeInfo != null && !StringUtil.isNullOrZero(storeInfo.getInvoiceId())) {
            //发票信息
            MemberInvoice memberInvoice = memberInvoiceReadMapper.getByPrimaryKey(storeInfo.getInvoiceId());
            orderExtend.setInvoiceInfo(JSON.toJSONString(memberInvoice));
            orderExtend.setInvoiceStatus(OrderConst.INVOICE_STATE_1);
        }
        //活动优惠明细
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (!CollectionUtils.isEmpty(promotionInfoList)) {
            orderExtend.setPromotionInfo(JSON.toJSONString(orderInfo.getPromotionInfoList(), SortField, MapSortField));
        }
        orderExtend.setStoreVoucherAmount(orderInfo.getStoreVoucherAmount());
        orderExtend.setPlatformVoucherAmount(orderInfo.getPlatformVoucherAmount());

        this.saveOrderExtend(orderExtend);
    }

    /**
     * 修改订单扩展信息
     *
     * @param orderSn
     * @param orderInfo
     * @param memberAddress
     * @param orderSubmitParamDTO
     */
    public void updateOrderExtend(String orderSn, OrderSubmitDTO.OrderInfo orderInfo, MemberAddress memberAddress, OrderSubmitParamDTO orderSubmitParamDTO) {
        //店铺提交参数
        OrderSubmitParamDTO.StoreInfo storeInfo = orderSubmitParamDTO.getStoreInfoByStoreId(orderInfo.getStoreId());

        OrderExtendExample extendExample = new OrderExtendExample();
        extendExample.setOrderSn(orderSn);
        OrderExtend orderExtend = new OrderExtend();
        orderExtend.setStoreId(orderInfo.getStoreId());
        orderExtend.setOrderRemark(storeInfo == null ? null : storeInfo.getRemark());
        orderExtend.setVoucherPrice(orderInfo.getVoucherAmount());
        orderExtend.setVoucherCode(orderInfo.getVoucherCode());
        orderExtend.setOrderFrom(orderSubmitParamDTO.getOrderFrom());
        orderExtend.setReceiverProvinceCode(memberAddress.getProvinceCode());
        orderExtend.setReceiverCityCode(memberAddress.getCityCode());
        orderExtend.setReceiverDistrictCode(memberAddress.getDistrictCode());
        orderExtend.setReceiverName(memberAddress.getMemberName());
        orderExtend.setReceiverInfo(memberAddress.getAddressAll() + memberAddress.getDetailAddress());
        if (storeInfo != null && !StringUtil.isNullOrZero(storeInfo.getInvoiceId())) {
            //发票信息
            MemberInvoice memberInvoice = memberInvoiceReadMapper.getByPrimaryKey(storeInfo.getInvoiceId());
            orderExtend.setInvoiceInfo(JSON.toJSONString(memberInvoice));
            orderExtend.setInvoiceStatus(OrderConst.INVOICE_STATE_1);
        }
        //活动优惠明细
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (!CollectionUtils.isEmpty(promotionInfoList)) {
            orderExtend.setPromotionInfo(JSON.toJSONString(orderInfo.getPromotionInfoList(), SortField, MapSortField));
        }
        orderExtend.setStoreVoucherAmount(orderInfo.getStoreVoucherAmount());
        orderExtend.setPlatformVoucherAmount(orderInfo.getPlatformVoucherAmount());

        this.updateOrderExtendByExample(orderExtend, extendExample);
    }
}