package com.slodon.b2b2c.controller.business.seller;

import com.slodon.b2b2c.business.dto.OrderDayDTO;
import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.example.OrderReturnExample;
import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderReturnModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.system.BillModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.example.BillExample;
import com.slodon.b2b2c.vo.business.SellerIndexVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-首页概况")
@RestController
@RequestMapping("v3/business/seller/dashboard")
public class SellerIndexController extends BaseController {

    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderReturnModel orderReturnModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private BillModel billModel;

    @SneakyThrows
    @ApiOperation("首页概况信息")
    @GetMapping("indexInfo")
    public JsonResult<SellerIndexVO> index(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //组装数据
        SellerIndexVO vo = new SellerIndexVO();
        //待发货
        OrderExample orderExample = new OrderExample();
        orderExample.setStoreId(vendor.getStoreId());
        orderExample.setOrderState(OrderConst.ORDER_STATE_20);
        vo.setToDeliveredNum(orderModel.getOrderCount(orderExample));
        //售后中订单
        OrderReturnExample returnExample = new OrderReturnExample();
        returnExample.setStoreId(vendor.getStoreId());
        returnExample.setStateNotIn(OrdersAfsConst.RETURN_STATE_202 + "," + OrdersAfsConst.RETURN_STATE_300);
        vo.setAfsOrderNum(orderReturnModel.getOrderReturnCount(returnExample));
        //出售中的商品
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setStoreId(vendor.getStoreId());
        goodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
        vo.setOnSaleGoodsNum(goodsModel.getGoodsCount(goodsExample));
        //待审核的商品
        goodsExample.setState(null);
        goodsExample.setStateIn(GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT + "," + GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT);
        vo.setToAuditGoodsNum(goodsModel.getGoodsCount(goodsExample));
        //违规的商品
        goodsExample.setStateIn(null);
        goodsExample.setState(GoodsConst.GOODS_STATE_LOWER_BY_SYSTEM);
        vo.setViolationGoodsNum(goodsModel.getGoodsCount(goodsExample));
        //待确认的结算单
        BillExample billExample = new BillExample();
        billExample.setStoreId(vendor.getStoreId());
        billExample.setState(BillConst.STATE_1);
        vo.setToConfirmBillNUm(billModel.getBillCount(billExample));

        //销量排名(默认取前四条)
        PagerInfo pager = new PagerInfo(4, 1);
        goodsExample.setState(null);
        goodsExample.setStateIn(null);
        goodsExample.setQuerySales("notNull");
        goodsExample.setOrderBy("actual_sales desc");
        goodsExample.setPager(pager);
        List<Goods> goodsList = goodsModel.getGoodsList(goodsExample, null);
        List<SellerIndexVO.GoodsSaleRankVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            goodsList.forEach(goods -> {
                vos.add(new SellerIndexVO.GoodsSaleRankVO(goods));
            });
        }
        vo.setGoodsSaleRank(vos);

        //七日订单数
        orderExample.setOrderState(null);
        orderExample.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        orderExample.setCreateTimeAfter(TimeUtil.getDayAgoDate(new Date(), -6));
        orderExample.setCreateTimeBefore(TimeUtil.getDayAgoDate(new Date(), 0));
        List<OrderDayDTO> orderDayDto = orderModel.getOrderDayDto(orderExample);
        vo.setOrderWeeklyReport(getWeeklyReport(orderDayDto));

        //七日销售额
        vo.setWeekSaleReport(getSaleTotalWeeklyReport(orderDayDto));
        return SldResponse.success(vo);
    }

    private List<SellerIndexVO.SellerSalesVolumeVO> getSaleTotalWeeklyReport(List<OrderDayDTO> result) {
        List<SellerIndexVO.SellerSalesVolumeVO> list = new ArrayList<>(7);
        if (CollectionUtils.isEmpty(result)) {
            for (int i = 0; i < 7; i++) {
                SellerIndexVO.SellerSalesVolumeVO saleTotalDayDTO = new SellerIndexVO.SellerSalesVolumeVO();
                saleTotalDayDTO.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                saleTotalDayDTO.setAmount(BigDecimal.ZERO);
                list.add(saleTotalDayDTO);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                String yesterday = TimeUtil.getYesterday(i - 6);
                SellerIndexVO.SellerSalesVolumeVO saleTotalDayDTO = new SellerIndexVO.SellerSalesVolumeVO();
                saleTotalDayDTO.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                saleTotalDayDTO.setAmount(BigDecimal.ZERO);
                for (OrderDayDTO o : result) {
                    if (o.getOrderDay().equals(yesterday)) {
                        saleTotalDayDTO.setAmount(o.getOrderAmount().subtract(o.getRefundAmount()));
                        break;
                    }
                }
                list.add(saleTotalDayDTO);
            }
        }
        return list;
    }

    private List<SellerIndexVO.OrderReportVO> getWeeklyReport(List<OrderDayDTO> result) {
        List<SellerIndexVO.OrderReportVO> list = new ArrayList<>(7);
        if (CollectionUtils.isEmpty(result)) {
            for (int i = 0; i < 7; i++) {
                SellerIndexVO.OrderReportVO vo = new SellerIndexVO.OrderReportVO();
                vo.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                vo.setNumber(0);
                list.add(vo);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                String yesterday = TimeUtil.getYesterday(i - 6);
                SellerIndexVO.OrderReportVO vo = new SellerIndexVO.OrderReportVO();
                vo.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                vo.setNumber(0);
                for (OrderDayDTO o : result) {
                    if (o.getOrderDay().equals(yesterday)) {
                        vo.setNumber(o.getCount());
                        break;
                    }
                }
                list.add(vo);
            }
        }
        return list;
    }

}
