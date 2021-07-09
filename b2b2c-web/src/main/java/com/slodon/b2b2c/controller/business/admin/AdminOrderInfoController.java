package com.slodon.b2b2c.controller.business.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.business.example.*;
import com.slodon.b2b2c.business.pojo.*;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.*;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.util.OrderExportUtil;
import com.slodon.b2b2c.vo.business.OrderListVO;
import com.slodon.b2b2c.vo.business.OrderProductListVO;
import com.slodon.b2b2c.vo.business.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Api(tags = "admin-订单管理")
@RestController
@RequestMapping("v3/business/admin/orderInfo")
@Slf4j
public class AdminOrderInfoController extends BaseController {

    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderExtendModel orderExtendModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderLogModel orderLogModel;
    @Resource
    private OrderAfterServiceModel orderAfterServiceModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("订单列表相关接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "下单开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", paramType = "query"),
            @ApiImplicitParam(name = "orderState", value = "订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<OrderListVO>> getList(HttpServletRequest request, String orderSn, String memberName,
                                                   String goodsName, Date startTime, Date endTime, Integer orderState) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSnLikes(orderSn);
        orderExample.setMemberNameLike(memberName);
        orderExample.setGoodsNameLike(goodsName);
        orderExample.setCreateTimeAfter(startTime);
        orderExample.setCreateTimeBefore(endTime);
        orderExample.setOrderState(orderState);

        List<Order> orderList = orderModel.getOrderList(orderExample, pager);
        List<OrderListVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderList)) {
            orderList.forEach(order -> {
                OrderListVO vo = new OrderListVO(order);
                //获取订单货品列表信息
                OrderProductExample orderProductExample = new OrderProductExample();
                orderProductExample.setOrderSn(order.getOrderSn());
                List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
                List<OrderProductListVO> productListVOS = new ArrayList<>();
                if (!CollectionUtils.isEmpty(orderProductList)) {
                    for (OrderProduct orderProduct : orderProductList) {
                        OrderProductListVO orderProductListVO = new OrderProductListVO(orderProduct);
                        productListVOS.add(orderProductListVO);
                    }
                }
                vo.setOrderProductListVOList(productListVOS);
                vos.add(vo);
            });
        }
        return  SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取订单详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true)
    })
    @GetMapping("detail")
    public JsonResult<OrderVO> getOrderDetail(HttpServletRequest request, @RequestParam("orderSn") String orderSn) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        Order order = orderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "无此订单");

        //获取订单扩展表信息
        OrderExtendExample orderExtendExample = new OrderExtendExample();
        orderExtendExample.setOrderSn(orderSn);
        OrderExtend orderExtend = orderExtendModel.getOrderExtendList(orderExtendExample, null).get(0);

        OrderVO orderVO = new OrderVO(order, orderExtend);

        //获取会员信息
        Member member = memberModel.getMemberByMemberId(order.getMemberId());
        orderVO.setMemberEmail(member.getMemberEmail());

        //获取货品信息

        //图片处理
        List<OrderProduct> orderProductList = order.getOrderProductList();
        for (OrderProduct orderProduct : orderProductList) {
            orderProduct.setProductImage(FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null));
        }

        //该货品是否有售后信息
        for (OrderProduct orderProduct : order.getOrderProductList()) {
            OrderAfterServiceExample orderAfterServiceExample = new OrderAfterServiceExample();
            orderAfterServiceExample.setOrderProductId(orderProduct.getOrderProductId());
            orderAfterServiceExample.setOrderBy("afs_id desc");
            List<OrderAfterService> orderAfterServiceList = orderAfterServiceModel.getOrderAfterServiceList(orderAfterServiceExample, null);
            if (CollectionUtils.isEmpty(orderAfterServiceList)) {
                orderProduct.setIsHasAfs(OrdersAfsConst.NOT_HAS_AFS);
            } else {
                orderProduct.setIsHasAfs(OrdersAfsConst.IS_HAS_AFS);
                orderProduct.setAfsSn(orderAfterServiceList.get(0).getAfsSn());
            }

        }

        orderVO.setOrderProductList(order.getOrderProductList());
        //获取订单日志信息
        OrderLogExample orderLogExample = new OrderLogExample();
        orderLogExample.setOrderSn(orderSn);
        orderLogExample.setOrderBy("log_time asc");
        List<OrderLog> orderLogList = orderLogModel.getOrderLogList(orderLogExample, null);
        orderVO.setOrderLogs(orderLogList);

        return SldResponse.success(orderVO);
    }

    @ApiOperation("订单导出")
    @OperationLogger(option = "订单导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "下单开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", paramType = "query"),
            @ApiImplicitParam(name = "orderState", value = "订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭", paramType = "query")
    })
    @GetMapping("export")
    public JsonResult export(HttpServletRequest request,HttpServletResponse response, String orderSn, String memberName,
                          String goodsName, Date startTime, Date endTime, Integer orderState) throws IOException, InterruptedException {
        long l = System.currentTimeMillis();
        OrderExample example = new OrderExample();
        example.setOrderSnLike(orderSn);
        example.setMemberNameLike(memberName);
        example.setGoodsNameLike(goodsName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);
        example.setOrderState(orderState);
        String orderCode = stringRedisTemplate.opsForValue().get("order_list_code");
        OrderExportUtil util = new OrderExportUtil(orderModel,example,orderCode);
        Map<String, SXSSFWorkbook> excelMap = util.doExport();
        if (excelMap.size() == 1){
            //导出一个excel文件，无需压缩
            FileDownloadUtils.setExcelHeadInfo(response,request,"导出订单-" + ExcelExportUtil.getSystemDate() + ".xls");
            excelMap.entrySet().iterator().next().getValue().write(response.getOutputStream());
        }else {
            //压缩
            FileDownloadUtils.setZipDownLoadHeadInfo(response,request,"导出订单-" + ExcelExportUtil.getSystemDate() + ".zip");
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

            for (Map.Entry<String, SXSSFWorkbook> entry : excelMap.entrySet()) {
                String fileName = entry.getKey();
                SXSSFWorkbook sxssfWorkbook = entry.getValue();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                sxssfWorkbook.write(os);
                zipOutputStream.putNextEntry(new ZipEntry(fileName));
                zipOutputStream.write(os.toByteArray());
                zipOutputStream.closeEntry();
                os.flush();
                os.close();
            }
            zipOutputStream.flush();
            zipOutputStream.close();
        }

        log.debug("耗时：{}ms",(System.currentTimeMillis() - l));
        return null;
    }
}
