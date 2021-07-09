package com.slodon.b2b2c.controller.business.admin;

import com.slodon.b2b2c.business.dto.OrderDayDTO;
import com.slodon.b2b2c.business.dto.SaleTotalDayDTO;
import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.member.dto.MemberDayDTO;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderReportModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.example.StoreExample;
import com.slodon.b2b2c.vo.business.AdminIndexVO;
import com.slodon.b2b2c.vo.business.SalesVolumeVO;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-首页概况")
@RestController
@RequestMapping("v3/business/admin/dashboard")
public class AdminIndexController extends BaseController {

    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderReportModel orderReportModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private GoodsModel goodsModel;

    @SneakyThrows
    @ApiOperation("首页概况信息")
    @GetMapping("indexInfo")
    public JsonResult<AdminIndexVO> index(HttpServletRequest request) {
        //时间处理
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date startTime = sdf.parse(TimeUtil.getToday() + " 00:00:00");
        Date endTime = sdf.parse(TimeUtil.getToday() + " 23:59:59");
        //组织数据
        AdminIndexVO vo = new AdminIndexVO();

        //今日营业额
        vo.setDailySale(orderReportModel.getDailySale(OrderConst.API_PAY_STATE_1));
        //今日下单数
        OrderExample orderExample = new OrderExample();
        orderExample.setCreateTimeAfter(startTime);
        orderExample.setCreateTimeBefore(endTime);
        orderExample.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        vo.setOrderNum(orderModel.getOrderCount(orderExample));
        //今日新增会员数
        MemberExample example = new MemberExample();
        example.setRegisterTimeAfter(startTime);
        example.setRegisterTimeBefore(endTime);
        vo.setNewMemberNum(memberModel.getMemberCount(example));
        //会员总数
        vo.setMemberTotal(memberModel.getMemberCount(new MemberExample()));
        //今日新增会员数
        StoreExample storeExample = new StoreExample();
        storeExample.setCreateTimeAfter(startTime);
        storeExample.setCreateTimeBefore(endTime);
        vo.setNewStoreNum(storeModel.getStoreCount(storeExample));
        //会员总数
        vo.setStoreTotal(storeModel.getStoreCount(new StoreExample()));
        //今日新增商品数
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setCreateTimeAfter(startTime);
        goodsExample.setCreateTimeBefore(endTime);
        vo.setNewGoodsNum(goodsModel.getGoodsCount(goodsExample));
        //商品总数
        vo.setGoodsTotal(goodsModel.getGoodsCount(new GoodsExample()));

        //一周概况
        //订单增长
        OrderExample orderExample1 = new OrderExample();
        orderExample1.setCreateTimeAfter(TimeUtil.getDayAgoDate(new Date(), -6));
        orderExample1.setCreateTimeBefore(TimeUtil.getDayAgoDate(new Date(), 0));
        orderExample1.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        List<OrderDayDTO> orderDayDto = orderModel.getOrderDayDto(orderExample1);
        vo.setOrderWeeklyReport(getWeeklyReport(orderDayDto));

        //会员增长
        MemberExample memberExample = new MemberExample();
        memberExample.setRegisterTimeAfter(TimeUtil.getDayAgoDate(new Date(), -6));
        memberExample.setRegisterTimeBefore(TimeUtil.getDayAgoDate(new Date(), 0));
        List<MemberDayDTO> memberNumDayDto = memberModel.getMemberDayDto(memberExample);
        vo.setMemberWeeklyReport(getMemberWeeklyReport(memberNumDayDto));

        //销售总额增长
        OrderExample orderExample2 = new OrderExample();
        orderExample2.setCreateTimeAfter(TimeUtil.getDayAgoDate(new Date(), -6));
        orderExample2.setCreateTimeBefore(TimeUtil.getDayAgoDate(new Date(), 0));
        orderExample2.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        List<SaleTotalDayDTO> saleTotalDayDto = orderReportModel.getSaleTotalDayDto(orderExample2);
        vo.setSaleTotalWeeklyReport(getSaleTotalWeeklyReport(saleTotalDayDto));

        //销售额类别占比
        List<SalesVolumeVO> yearSaleCateRate = orderReportModel.getSaleCateStatistics("year");
        List<SalesVolumeVO> monthSaleCateRate = orderReportModel.getSaleCateStatistics("month");
        List<SalesVolumeVO> weekSaleCateRate = orderReportModel.getSaleCateStatistics("week");
        vo.setYearSaleCateRate(yearSaleCateRate);
        vo.setMonthSaleCateRate(monthSaleCateRate);
        vo.setWeekSaleCateRate(weekSaleCateRate);

        //7日内店铺销售排名数据
        //店铺销量排行，下单付款后即计算商品销量，商品销量 = 购买数量-退货数量
        OrderExample orderExample3 = new OrderExample();
        orderExample3.setCreateTimeAfter(TimeUtil.getDayAgoDate(new Date(), -6));
        orderExample3.setCreateTimeBefore(TimeUtil.getDayAgoDate(new Date(), 0));
        orderExample3.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        List<AdminIndexVO.StoreSaleRankVO> storeSaleRank = orderReportModel.storeSaleRank(orderExample3);
        vo.setStoreSaleRank(storeSaleRank);

        //7日内商品销售排名数据
        //商品销量排行，下单付款后即计算商品销量，商品销量 = 购买数量-退货数量
        OrderExample orderExample4 = new OrderExample();
        orderExample4.setCreateTimeAfter(TimeUtil.getDayAgoDate(new Date(), -6));
        orderExample4.setCreateTimeBefore(TimeUtil.getDayAgoDate(new Date(), 0));
        orderExample3.setOrderStateIn(OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30 + "," + OrderConst.ORDER_STATE_40);
        List<AdminIndexVO.GoodsSaleRankVO> goodsSaleRank = orderReportModel.goodsSaleRank(orderExample4);
        vo.setGoodsSaleRank(goodsSaleRank);
        return SldResponse.success(vo);
    }

    private List<SaleTotalDayDTO> getSaleTotalWeeklyReport(List<SaleTotalDayDTO> result) {
        List<SaleTotalDayDTO> list = new ArrayList<>(7);
        if (CollectionUtils.isEmpty(result)) {
            for (int i = 0; i < 7; i++) {
                SaleTotalDayDTO saleTotalDayDTO = new SaleTotalDayDTO();
                saleTotalDayDTO.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                saleTotalDayDTO.setAmount(BigDecimal.ZERO);
                list.add(saleTotalDayDTO);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                String yesterday = TimeUtil.getYesterday(i - 6);
                SaleTotalDayDTO saleTotalDayDTO = new SaleTotalDayDTO();
                saleTotalDayDTO.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                saleTotalDayDTO.setAmount(BigDecimal.ZERO);
                for (SaleTotalDayDTO o : result) {
                    if (o.getDay().equals(yesterday)) {
                        saleTotalDayDTO.setAmount(o.getAmount());
                        break;
                    }
                }
                list.add(saleTotalDayDTO);
            }
        }
        return list;
    }

    private List<AdminIndexVO.MemberReportVO> getMemberWeeklyReport(List<MemberDayDTO> result) {
        List<AdminIndexVO.MemberReportVO> list = new ArrayList<>(7);
        if (CollectionUtils.isEmpty(result)) {
            for (int i = 0; i < 7; i++) {
                AdminIndexVO.MemberReportVO vo = new AdminIndexVO.MemberReportVO();
                vo.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                vo.setNumber(0);
                list.add(vo);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                String yesterday = TimeUtil.getYesterday(i - 6);
                AdminIndexVO.MemberReportVO vo = new AdminIndexVO.MemberReportVO();
                vo.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                vo.setNumber(0);
                for (MemberDayDTO o : result) {
                    if (o.getDay().equals(yesterday)) {
                        vo.setNumber(o.getNumber());
                        break;
                    }
                }
                list.add(vo);
            }
        }
        return list;
    }

    private List<AdminIndexVO.OrderReportVO> getWeeklyReport(List<OrderDayDTO> result) {
        List<AdminIndexVO.OrderReportVO> list = new ArrayList<>(7);
        if (CollectionUtils.isEmpty(result)) {
            for (int i = 0; i < 7; i++) {
                AdminIndexVO.OrderReportVO vo = new AdminIndexVO.OrderReportVO();
                vo.setDay(TimeUtil.getYesterdayOfWeek(i - 6).toString());
                vo.setNumber(0);
                list.add(vo);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                String yesterday = TimeUtil.getYesterday(i - 6);
                AdminIndexVO.OrderReportVO vo = new AdminIndexVO.OrderReportVO();
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
