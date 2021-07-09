package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.dto.SaleTotalDayDTO;
import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.business.OrderProductReadMapper;
import com.slodon.b2b2c.dao.read.business.OrderReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsCategoryReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreReadMapper;
import com.slodon.b2b2c.vo.business.AdminIndexVO;
import com.slodon.b2b2c.vo.business.SalesVolumeVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderReportModel {

    @Resource
    private OrderReadMapper orderReadMapper;
    @Resource
    private OrderProductReadMapper orderProductReadMapper;
    @Resource
    private GoodsCategoryReadMapper goodsCategoryReadMapper;
    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private StoreReadMapper storeReadMapper;

    /**
     * 销售总额统计
     *
     * @param example
     * @return
     */
    public List<SaleTotalDayDTO> getSaleTotalDayDto(OrderExample example) {
        return orderReadMapper.getSaleTotalDayDto(example);
    }

    /**
     * 今日累计营业额
     *
     * @param payState
     * @return
     */
    @SneakyThrows
    public BigDecimal getDailySale(String payState) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        OrderExample example = new OrderExample();
        String today = TimeUtil.getToday();
        example.setCreateTimeAfter(sdf.parse(today + " 00:00:00"));
        example.setCreateTimeBefore(sdf.parse(today + " 23:59:59"));
        if (payState.equals(OrderConst.API_PAY_STATE_0)) {
            //待付款
            example.setOrderState(OrderConst.ORDER_STATE_10);
        } else if (payState.equals(OrderConst.API_PAY_STATE_1)) {
            example.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        }
        List<Order> orderList = orderReadMapper.listByExample(example);
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(orderList)) {
            for (Order order : orderList) {
                totalAmount = totalAmount.add(order.getOrderAmount());
            }
        }
        return totalAmount;
    }

    /**
     * 商品销售额类别占比
     *
     * @param model
     * @return
     */
    public List<SalesVolumeVO> getSaleCateStatistics(String model) {
        //订单支付完成即计算销售额
        Date date = new Date();
        Date payTimeStart;
        Date payTimeEnd = new Date();
        switch (model) {
            case "year":
                payTimeStart = TimeUtil.getYearAgoDate(date, -1);
                break;
            case "month":
                payTimeStart = TimeUtil.getMonthAgoDate(date, -1);
                break;
            case "week":
                payTimeStart = TimeUtil.getDayAgoDate(date, -7);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + model);
        }
        //查询已完成的订单
        OrderExample ordersExample = new OrderExample();
        ordersExample.setPayTimeAfter(payTimeStart);
        ordersExample.setPayTimeBefore(payTimeEnd);
        ordersExample.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        List<Order> ordersList = orderReadMapper.listByExample(ordersExample);
        List<SalesVolumeVO> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ordersList)) {
            //原始数据，key为分类id，value为该分类的总销售额
            Map<Integer, BigDecimal> dataMap = new HashMap<>();
            BigDecimal totalAmount = BigDecimal.ZERO;//总的销售额，用于计算各分类占比
            for (Order orders : ordersList) {
                //根据订单号查询货品列表
                OrderProductExample productExample = new OrderProductExample();
                productExample.setOrderSn(orders.getOrderSn());
                List<OrderProduct> productList = orderProductReadMapper.listByExample(productExample);
                if (productList != null && productList.size() > 0) {
                    //封装原始数据
                    for (OrderProduct op : productList) {
                        if (dataMap.containsKey(op.getGoodsCategoryId())) {
                            //相同分类的货品，销售额累加
                            dataMap.replace(op.getGoodsCategoryId(), dataMap.get(op.getGoodsCategoryId()).add(op.getMoneyAmount()));
                        } else {
                            dataMap.put(op.getGoodsCategoryId(), op.getMoneyAmount());
                        }
                        totalAmount = totalAmount.add(op.getMoneyAmount());
                    }
                }
            }
            if (totalAmount.compareTo(BigDecimal.ZERO) == 0){
                //总销售额为0，不统计
                return result;
            }

            //对构造好的原始dataMap 按照销售总额倒序排列,放入newMap
            Map<Integer, BigDecimal> newMap = new LinkedHashMap<>();
            if (dataMap.size() > 0) {
                dataMap.entrySet()
                        .stream()
                        .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))//定义排序规则，倒序
                        .collect(Collectors.toList())
                        .forEach(ele -> newMap.put(ele.getKey(), ele.getValue()));
            }

            //循环newMap,构造返回数据
            if (newMap.size() <= 5) {
                //分类少于等于5个，直接记录每个分类的销售额
                for (Map.Entry<Integer, BigDecimal> entry : newMap.entrySet()) {
                    Integer cateId = entry.getKey();
                    BigDecimal moneyAmount = entry.getValue();
                    SalesVolumeVO vo = new SalesVolumeVO();
                    //key为分类id，value为销售额
                    //按照key查询分类名称
                    String cateName = goodsCategoryReadMapper.getByPrimaryKey(cateId).getCategoryName();
                    vo.setName(cateName);
                    vo.setMoneyAmount(moneyAmount);
                    vo.setPer(moneyAmount.multiply(BigDecimal.valueOf(100)).divide(totalAmount,2,RoundingMode.HALF_UP).toString() + "%");
                    result.add(vo);
                }
            } else {
                //分类大于5，记录前5个，剩余记为其他
                Iterator<Map.Entry<Integer, BigDecimal>> iterator = newMap.entrySet().iterator();
                int i = 0;
                BigDecimal otherAmount = totalAmount;//其他分类金额
                BigDecimal otherPer = new BigDecimal(100);//其他分类占比
                while (iterator.hasNext() && i < 5) {
                    Map.Entry<Integer, BigDecimal> entry = iterator.next();
                    Integer cateId = entry.getKey();
                    BigDecimal moneyAmount = entry.getValue();

                    SalesVolumeVO vo = new SalesVolumeVO();
                    //key为分类id，value为销售额
                    //按照key查询分类名称
                    String cateName = goodsCategoryReadMapper.getByPrimaryKey(cateId).getCategoryName();
                    vo.setName(cateName);
                    vo.setMoneyAmount(moneyAmount);
                    BigDecimal per = moneyAmount.multiply(BigDecimal.valueOf(100)).divide(totalAmount, 2,RoundingMode.HALF_UP);
                    vo.setPer(per.toString() + "%");
                    result.add(vo);

                    //数据计算
                    otherAmount = otherAmount.subtract(moneyAmount);
                    otherPer = otherPer.subtract(per);
                    i++;
                }
                //其他
                SalesVolumeVO vo = new SalesVolumeVO();
                vo.setName("其他");
                vo.setMoneyAmount(otherAmount);
                vo.setPer(otherPer.toString() + "%");
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * 商品销量排行
     *
     * @param example
     * @return
     */
    public List<AdminIndexVO.GoodsSaleRankVO> goodsSaleRank(OrderExample example) {
        List<AdminIndexVO.GoodsSaleRankVO> result = new ArrayList<>();
        List<Order> ordersList = orderReadMapper.listByExample(example);
        if (ordersList != null && ordersList.size() > 0) {
            //原始数据，key为goodsId，value为商品销量 = 销售数量-退货数量
            Map<Long, Integer> dataMap = new HashMap<>();
            ordersList.forEach(order -> {
                //根据订单号查询货品列表
                OrderProductExample productExample = new OrderProductExample();
                productExample.setOrderSn(order.getOrderSn());
                List<OrderProduct> productList = orderProductReadMapper.listByExample(productExample);
                productList.forEach(op -> {
                    if (dataMap.containsKey(op.getGoodsId())) {
                        //已经有该商品的数据，累加
                        dataMap.replace(op.getGoodsId(), dataMap.get(op.getGoodsId()) + op.getProductNum() - op.getReturnNumber());
                    } else {
                        //没有该商品的数据，新增
                        dataMap.put(op.getGoodsId(), op.getProductNum() - op.getReturnNumber());
                    }
                });
            });

            //对构造好的原始dataMap 按照商品销量倒序排列,放入newMap
            Map<Long, Integer> newMap = new LinkedHashMap<>();
            if (dataMap.size() > 0) {
                dataMap.entrySet()
                        .stream()
                        .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))//定义排序规则，倒序
                        .collect(Collectors.toList())
                        .forEach(ele -> newMap.put(ele.getKey(), ele.getValue()));
            }

            //取newMap中前20个元素，封装返回数据
            Iterator<Map.Entry<Long, Integer>> iterator = newMap.entrySet().iterator();
            int i = 0;
            while (iterator.hasNext() && i < 20) {
                Map.Entry<Long, Integer> entry = iterator.next();
                AdminIndexVO.GoodsSaleRankVO vo = new AdminIndexVO.GoodsSaleRankVO();
                String goodsName = goodsReadMapper.getByPrimaryKey(entry.getKey()).getGoodsName();
                vo.setGoodsName(goodsName);
                vo.setNumber(entry.getValue());
                result.add(vo);
                i++;
            }
        }
        return result;
    }

    /**
     * 店铺销量排行
     *
     * @param example
     * @return
     */
    public List<AdminIndexVO.StoreSaleRankVO> storeSaleRank(OrderExample example) {
        List<AdminIndexVO.StoreSaleRankVO> result = new ArrayList<>();
        List<Order> ordersList = orderReadMapper.listByExample(example);
        if (ordersList != null && ordersList.size() > 0) {
            //原始数据，key为storeId，value为销售额 = 订单总金额-退款金额
            Map<Long, BigDecimal> dataMap = new HashMap<>();
            ordersList.forEach(order -> {
                if (dataMap.containsKey(order.getStoreId())) {
                    //已经有该商品的数据，累加
                    dataMap.replace(order.getStoreId(), dataMap.get(order.getStoreId()).add(order.getOrderAmount().subtract(order.getRefundAmount())));
                } else {
                    //没有该商品的数据，新增
                    dataMap.put(order.getStoreId(), order.getOrderAmount().subtract(order.getRefundAmount()));
                }
            });

            //对构造好的原始dataMap 按照销售总额倒序排列,放入newMap
            Map<Long, BigDecimal> newMap = new LinkedHashMap<>();
            if (dataMap.size() > 0) {
                dataMap.entrySet()
                        .stream()
                        .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))//定义排序规则，倒序
                        .collect(Collectors.toList())
                        .forEach(ele -> newMap.put(ele.getKey(), ele.getValue()));
            }

            //取newMap中前20个元素，封装返回数据
            Iterator<Map.Entry<Long, BigDecimal>> iterator = newMap.entrySet().iterator();
            int i = 0;
            while (iterator.hasNext() && i < 20) {
                Map.Entry<Long, BigDecimal> entry = iterator.next();
                AdminIndexVO.StoreSaleRankVO vo = new AdminIndexVO.StoreSaleRankVO();
                String storeName = storeReadMapper.getByPrimaryKey(entry.getKey()).getStoreName();
                vo.setStoreName(storeName);
                vo.setAmount(entry.getValue());
                result.add(vo);
                i++;
            }
        }
        return result;
    }
}
