package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.example.OrderReturnExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.business.pojo.OrderReturn;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.ExcelUtil;
import com.slodon.b2b2c.core.util.FileDownloadUtils;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.business.OrderProductReadMapper;
import com.slodon.b2b2c.dao.read.business.OrderReadMapper;
import com.slodon.b2b2c.dao.read.business.OrderReplacementReadMapper;
import com.slodon.b2b2c.dao.read.business.OrderReturnReadMapper;
import com.slodon.b2b2c.dao.read.promotion.PresellDepositCompensationReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreReadMapper;
import com.slodon.b2b2c.dao.read.system.BillReadMapper;
import com.slodon.b2b2c.dao.read.system.SettingReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderWriteMapper;
import com.slodon.b2b2c.dao.write.system.BillLogWriteMapper;
import com.slodon.b2b2c.dao.write.system.BillOrderBindWriteMapper;
import com.slodon.b2b2c.dao.write.system.BillWriteMapper;
import com.slodon.b2b2c.promotion.example.PresellDepositCompensationExample;
import com.slodon.b2b2c.promotion.pojo.PresellDepositCompensation;
import com.slodon.b2b2c.seller.example.StoreExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.system.example.BillExample;
import com.slodon.b2b2c.system.pojo.Bill;
import com.slodon.b2b2c.system.pojo.BillLog;
import com.slodon.b2b2c.system.pojo.BillOrderBind;
import com.slodon.b2b2c.vo.system.BillExportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_SELLER_MSG;

/**
 * 结算表model
 */
@Component
@Slf4j
public class BillModel {

    @Resource
    private BillReadMapper billReadMapper;
    @Resource
    private BillWriteMapper billWriteMapper;
    @Resource
    private BillOrderBindWriteMapper billOrderBindWriteMapper;
    @Resource
    private BillLogWriteMapper billLogWriteMapper;
    @Resource
    private SettingReadMapper settingReadMapper;
    @Resource
    private StoreReadMapper storeReadMapper;
    @Resource
    private OrderReadMapper orderReadMapper;
    @Resource
    private OrderWriteMapper orderWriteMapper;
    @Resource
    private OrderProductReadMapper orderProductReadMapper;
    @Resource
    private OrderReturnReadMapper orderReturnReadMapper;
    @Resource
    private OrderReplacementReadMapper orderReplacementReadMapper;
    @Resource
    private PresellDepositCompensationReadMapper presellDepositCompensationReadMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增结算表
     *
     * @param bill
     * @return
     */
    public Integer saveBill(Bill bill) {
        int count = billWriteMapper.insert(bill);
        if (count == 0) {
            throw new MallException("添加结算表失败，请重试");
        }
        return count;
    }

    /**
     * 根据billId删除结算表
     *
     * @param billId billId
     * @return
     */
    public Integer deleteBill(Integer billId) {
        if (StringUtils.isEmpty(billId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = billWriteMapper.deleteByPrimaryKey(billId);
        if (count == 0) {
            log.error("根据billId：" + billId + "删除结算表失败");
            throw new MallException("删除结算表失败,请重试");
        }
        return count;
    }

    /**
     * 根据billId更新结算表
     *
     * @param bill
     * @return
     */
    public Integer updateBill(Bill bill) {
        if (StringUtils.isEmpty(bill.getBillId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = billWriteMapper.updateByPrimaryKeySelective(bill);
        if (count == 0) {
            log.error("根据billId：" + bill.getBillId() + "更新结算表失败");
            throw new MallException("更新结算表失败,请重试");
        }
        return count;
    }

    /**
     * 根据billId获取结算表详情
     *
     * @param billId billId
     * @return
     */
    public Bill getBillByBillId(Integer billId) {
        return billReadMapper.getByPrimaryKey(billId);
    }

    /**
     * 根据条件获取结算表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Bill> getBillList(BillExample example, PagerInfo pager) {
        List<Bill> billList;
        if (pager != null) {
            pager.setRowsCount(billReadMapper.countByExample(example));
            billList = billReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            billList = billReadMapper.listByExample(example);
        }
        return billList;
    }

    /**
     * 根据条件获取结算表数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getBillCount(BillExample example) {
        return billReadMapper.countByExample(example);
    }

    //region 系统自动生成结算单
    /**
     * 商家结算账单生成定时任务
     * -保存结算单
     * -保存结算日志
     * -保存订单结算绑定关系
     * -修改订单结算状态
     * -发送消息通知
     *
     * @return
     */
    @Transactional
    public void billJobSchedule() {
        //当天结算的店铺
        List<Store> storeList = this.getBillStoreList();
        if (CollectionUtils.isEmpty(storeList)){
            //当天没有需要结算的店铺
            return;
        }

        //售后期限，过了售后期并且没有正在进行的售后的订单，才可以结算
        int afterSaleDay = Integer.parseInt(settingReadMapper.getByPrimaryKey("time_limit_of_after_sale").getValue());
        for (Store store : storeList) {
            //查询当前店铺可结算的订单
            List<Order> billOrderList = this.getBillOrderList(store.getStoreId(),afterSaleDay);
            if (CollectionUtils.isEmpty(billOrderList)) {
                //当前店铺没有要结算的订单
                continue;
            }

            //-保存结算单、-保存订单结算绑定关系
            Bill bill = this.jobAddBill(store, billOrderList);

            //-保存结算日志
            this.saveBillLog(bill.getBillSn());

            //-修改订单结算状态
            billOrderList.forEach(order -> {
                Order updateOrder = new Order();
                updateOrder.setIsSettlement(OrderConst.IS_SETTLEMENT_YES);
                OrderExample orderExample = new OrderExample();
                orderExample.setOrderSn(order.getOrderSn());
                int insert = orderWriteMapper.updateByExampleSelective(updateOrder,orderExample);
                AssertUtil.isTrue(insert == 0,"修改订单结算状态失败");
            });

            //发送消息通知
            List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
            messageSendPropertyList.add(new MessageSendProperty("startTime", TimeUtil.getDateTimeString(bill.getStartTime())));
            messageSendPropertyList.add(new MessageSendProperty("endTime", TimeUtil.getDateTimeString(bill.getEndTime())));
            messageSendPropertyList.add(new MessageSendProperty("billSn", bill.getBillSn()));
            String msgLinkInfo = "{\"billId\":\"" + bill.getBillId() + "\",\"type\":\"bill_news\"}";
            MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, bill.getStoreId(), StoreTplConst.BILL_ORDER_REMINDER, msgLinkInfo);

            //发送到mq
            rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
        }
    }

    /**
     * 获取今日结算的店铺列表
     *
     * @return
     */
    private List<Store> getBillStoreList(){
        //获取当前日
        String today = TimeUtil.getDay();
        //获取今天星期几
        Integer week = TimeUtil.getYesterdayOfWeek(0);

        //按月查询当天结算的店铺
        StoreExample example = new StoreExample();
        example.setBillType(StoreConst.BILL_TYPE_MONTH);
        example.setBillDay(today);
        List<Store> storeList = storeReadMapper.listByExample(example);

        //按周查询当天结算的店铺
        example.setBillType(StoreConst.BILL_TYPE_WEEK);
        example.setBillDay(week.toString());
        storeList.addAll(storeReadMapper.listByExample(example));

        return storeList;
    }

    /**
     * 获取店铺可结算的订单
     *
     * @param storeId 店铺id
     * @param afterSaleLimit 售后期限，单位天
     * @return
     */
    private List<Order> getBillOrderList(Long storeId,int afterSaleLimit){
        OrderExample ordersExample = new OrderExample();
        ordersExample.setStoreId(storeId);
        ordersExample.setFinishTimeBefore(TimeUtil.getDateApartDay(-afterSaleLimit));//过了售后期
        ordersExample.setOrderState(OrderConst.ORDER_STATE_40);//已完成的订单
        ordersExample.setIsSettlement(OrderConst.IS_SETTLEMENT_NO);//未结算的订单
        List<Order> orderList = orderReadMapper.listByExample(ordersExample);
        if (CollectionUtils.isEmpty(orderList)) {
            //当前店铺没有要结算的订单
            return null;
        }
        List<Order> billOrderList = new ArrayList<>();//最终可结算的订单
        for (Order order : orderList) {
            //查询是否有正在进行的售后
            OrderReturnExample orderReturnExample = new OrderReturnExample();
            orderReturnExample.setOrderSn(order.getOrderSn());
            orderReturnExample.setStateNotIn(OrdersAfsConst.RETURN_STATE_202 + "," + OrdersAfsConst.RETURN_STATE_300);//售后状态不是完成或关闭
            List<OrderReturn> notFinishedReturnList = orderReturnReadMapper.listByExample(orderReturnExample);//未完成的售后列表
            //todo 查询未完成的换货单
            if (CollectionUtils.isEmpty(notFinishedReturnList)){
                //没有未完成的售后单，可以进行结算
                billOrderList.add(order);
            }
        }
        return billOrderList;
    }

    /**
     * 生成结算单,保存订单结算绑定关系
     *
     * @param store 结算的店铺
     * @param billOrderList 店铺内可结算的订单
     * @return
     */
    private Bill jobAddBill(Store store,List<Order> billOrderList){
        //结算开始
        BigDecimal zero = new BigDecimal("0.00");
        String billSn = GoodsIdGenerator.getBillSn();//结算单号
        Bill bill = new Bill();
        bill.setBillSn(billSn);
        bill.setStoreId(store.getStoreId());
        bill.setStoreName(store.getStoreName());
        bill.setStartTime(this.getBillStartTime(store));
        bill.setEndTime(new Date());
        bill.setOrderAmount(zero);//订单表获取
        bill.setCommission(zero);//订单货品表获取
        bill.setRefundCommission(zero);//退货表获取
        bill.setRefundAmount(zero);//退货表获取
        bill.setPlatformActivityAmount(zero);//订单货品表获取
        bill.setPlatformVoucherAmount(zero);//订单货品表获取
        bill.setIntegralCashAmount(zero);//订单货品表获取
        bill.setCompensationAmount(zero);//预售定金赔偿表获取
        bill.setSettleAmount(zero);//自行计算
        bill.setState(BillConst.STATE_1);
        bill.setCreateTime(new Date());
        bill.setUpdateTime(new Date());

        for (Order order : billOrderList) {
            //构造订单结算绑定关系
            BillOrderBind billOrderBind = new BillOrderBind();
            billOrderBind.setBillSn(billSn);
            billOrderBind.setOrderSn(order.getOrderSn());
            billOrderBind.setOrderAmount(order.getOrderAmount());
            billOrderBind.setCommission(zero);
            billOrderBind.setRefundCommission(zero);
            billOrderBind.setRefundAmount(zero);
            billOrderBind.setPlatformActivityAmount(zero);
            billOrderBind.setPlatformVoucherAmount(zero);
            billOrderBind.setIntegralCashAmount(zero);
            billOrderBind.setCreateTime(order.getCreateTime());
            billOrderBind.setFinishTime(order.getFinishTime());

            bill.setOrderAmount(bill.getOrderAmount().add(order.getOrderAmount()));

            //查询订单货品列表
            OrderProductExample orderProductExample = new OrderProductExample();
            orderProductExample.setOrderSn(order.getOrderSn());
            List<OrderProduct> orderProductList = orderProductReadMapper.listByExample(orderProductExample);
            orderProductList.forEach(orderProduct -> {

                //当前订单货品对应的平台优惠活动金额（不包含平台优惠券、积分，按比例计算未退货的货品）
                BigDecimal platformActivityAmount = orderProduct.getPlatformActivityAmount().subtract(orderProduct.getPlatformVoucherAmount()).subtract(orderProduct.getIntegralCashAmount())
                        .multiply(new BigDecimal(orderProduct.getProductNum() - orderProduct.getReturnNumber()))
                        .divide(new BigDecimal(orderProduct.getProductNum()), 2, RoundingMode.HALF_UP);
                //当前订单货品对应的平台优惠券金额（按比例计算未退货的货品）
                BigDecimal platformVoucherAmount = orderProduct.getPlatformVoucherAmount()
                        .multiply(new BigDecimal(orderProduct.getProductNum() - orderProduct.getReturnNumber()))
                        .divide(new BigDecimal(orderProduct.getProductNum()), 2, RoundingMode.HALF_UP);
                //当前订单货品对应的积分金额（按比例计算未退货的货品）
                BigDecimal integralCashAmount = orderProduct.getIntegralCashAmount()
                        .multiply(new BigDecimal(orderProduct.getProductNum() - orderProduct.getReturnNumber()))
                        .divide(new BigDecimal(orderProduct.getProductNum()), 2, RoundingMode.HALF_UP);

                //金额累加
                bill.setCommission(bill.getCommission().add(orderProduct.getCommissionAmount()));
                bill.setPlatformActivityAmount(bill.getPlatformActivityAmount().add(platformActivityAmount));
                bill.setPlatformVoucherAmount(bill.getPlatformVoucherAmount().add(platformVoucherAmount));
                bill.setIntegralCashAmount(bill.getIntegralCashAmount().add(integralCashAmount));

                billOrderBind.setCommission(billOrderBind.getCommission().add(orderProduct.getCommissionAmount()));
                billOrderBind.setPlatformActivityAmount(billOrderBind.getPlatformActivityAmount().add(platformActivityAmount));
                billOrderBind.setPlatformVoucherAmount(billOrderBind.getPlatformVoucherAmount().add(platformVoucherAmount));
                billOrderBind.setIntegralCashAmount(billOrderBind.getIntegralCashAmount().add(integralCashAmount));
            });

            //退还佣金、退款金额从退货表中查询（由于退货时可指定退款金额）
            OrderReturnExample orderReturnExample = new OrderReturnExample();
            orderReturnExample.setOrderSn(order.getOrderSn());
            orderReturnExample.setState(OrdersAfsConst.RETURN_STATE_300);//已完成的退货
            List<OrderReturn> orderReturnList = orderReturnReadMapper.listByExample(orderReturnExample);
            if (!CollectionUtils.isEmpty(orderReturnList)) {
                orderReturnList.forEach(orderReturn -> {
                    //当前订单货品退还佣金
                    BigDecimal refundCommission = orderReturn.getCommissionAmount();
                    //当前订单货品退款金额(包括退还运费)
                    BigDecimal refundAmount = orderReturn.getReturnMoneyAmount().add(orderReturn.getReturnExpressAmount());

                    //金额累加
                    bill.setRefundCommission(bill.getRefundCommission().add(refundCommission));
                    bill.setRefundAmount(bill.getRefundAmount().add(refundAmount));

                    billOrderBind.setRefundCommission(billOrderBind.getRefundCommission().add(refundCommission));
                    billOrderBind.setRefundAmount(billOrderBind.getRefundAmount().add(refundAmount));
                });
            }

            //预售订单查询是否有赔偿记录
            if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
                PresellDepositCompensationExample depositCompensationExample = new PresellDepositCompensationExample();
                depositCompensationExample.setOrderSn(order.getOrderSn());
                List<PresellDepositCompensation> depositCompensationList = presellDepositCompensationReadMapper.listByExample(depositCompensationExample);
                if (!CollectionUtils.isEmpty(depositCompensationList)) {
                    PresellDepositCompensation depositCompensation = depositCompensationList.get(0);
                    //赔偿金额要减去定金
                    billOrderBind.setCompensationAmount(depositCompensation.getCompensationAmount().subtract(depositCompensation.getDepositAmount()));
                    bill.setCompensationAmount(depositCompensation.getCompensationAmount().subtract(depositCompensation.getDepositAmount()));
                }
            }

            //-保存订单结算绑定关系
            int insert = billOrderBindWriteMapper.insert(billOrderBind);
            AssertUtil.isTrue(insert == 0, "结算单与订单绑定保存失败！");
        }

        //计算结算费用:订单金额-平台佣金-赔偿金额+退还佣金-退单金额+平台活动费用+平台优惠券+积分抵现金额
        bill.setSettleAmount(bill.getOrderAmount()
                .subtract(bill.getCommission())
                .subtract(bill.getCompensationAmount())
                .add(bill.getRefundCommission())
                .subtract(bill.getRefundAmount())
                .add(bill.getPlatformActivityAmount())
                .add(bill.getPlatformVoucherAmount())
                .add(bill.getIntegralCashAmount()));

        this.saveBill(bill);

        return bill;
    }

    /**
     * 获取店铺的结算开始时间
     *
     * @param store
     * @return
     */
    private Date getBillStartTime(Store store){
        BillExample billExample = new BillExample();
        billExample.setStoreId(store.getStoreId());
        List<Bill> billList = billReadMapper.listByExample(billExample);
        if (CollectionUtils.isEmpty(billList)) {
            //店铺从未结算过，结算开始时间为店铺的开店时间
            return store.getCreateTime();
        }
        //返回上一次结算的结束时间
        return billList.get(0).getEndTime();
    }

    /**
     * 记录结算日志
     *
     * @param billSn
     */
    private void saveBillLog(String billSn){
        BillLog billLog = new BillLog();
        billLog.setBillSn(billSn);
        billLog.setOperatorId(0L);
        billLog.setOperatorName("system");
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_1);
        billLog.setContent("生成结算单");
        billLog.setCreateTime(new Date());
        int insert = billLogWriteMapper.insert(billLog);
        AssertUtil.isTrue(insert == 0, "结算日志保存失败！");
    }

    //endregion

    //region 结算导出

    /**
     * 结算导出
     *
     * @param request
     * @param response
     * @param example
     * @return
     */
    public boolean billExport(HttpServletRequest request, HttpServletResponse response, BillExample example) {
        List<Bill> billList = billReadMapper.listByExample(example);
        List<BillExportVO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(billList)) {
            billList.forEach(bill -> {
                list.add(new BillExportVO(bill));
            });
        }
        // 用于存放生成的excel文件名称
        List<File> fileNames = new ArrayList<>();
        // 每次导出的excel的文件名
        String fileNameS = "";
        // 将excel导出的文件位置
        String filePath = "/upload/";
        File files = new File(filePath);
        // 如果文件夹不存在则创建
        if (!files.exists() && !files.isDirectory()) {
            files.mkdirs();
        }

        //excel标题
        String titles = "ID,店铺名称,开始时间,结束时间,订单金额,佣金,退单金额,退还佣金,店铺活动费用,平台优惠券,积分抵现金额,应结金额,结算状态";
        String[] titleArray = titles.split(",");

        // 创建Excel文件,每个excel限制2000条数据,超过则打包zip
        int count = list.size();
        int max = 2000;
        int num = count % max;
        int num1;
        if (num == 0) {
            num1 = count / max;
        } else {
            num1 = count / max + 1;
        }

        try {
            if (list.size() <= max) {
                // 导出的excel的文件名
                fileNameS = "结算单-" + ExcelUtil.getSystemDate() + "-" + list.size() + ".xls";

                //下载单个excle
                byte b[] = ExcelUtil.export("结算单", titleArray, list);

                File f = new File(filePath + fileNameS);
                FileUtils.writeByteArrayToFile(f, b, true);

                // 下载
                FileDownloadUtils.setExcelHeadInfo(response, request, fileNameS);

                ServletOutputStream out = response.getOutputStream();

                String path = "/upload/" + fileNameS; //Excel在服务器上的路径

                File file = new File(path);

                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(4096);

                byte[] cache = new byte[4096];
                for (int offset = fis.read(cache); offset != -1; offset = fis.read(cache)) {
                    byteOutputStream.write(cache, 0, offset);
                }

                byte[] bt = byteOutputStream.toByteArray();

                out.write(bt);
                out.flush();
                out.close();
                fis.close();
                if (file.exists()) {
                    file.delete();
                }

            } else {
                for (int i = 1; i <= num1; i++) {
                    fileNameS = "结算单-" + ExcelUtil.getSystemDate() + "-" + i + ".xls";
                    List<BillExportVO> exportDatas = new ArrayList<>();
                    if (i == num1) {
                        for (int j = max * (i - 1); j < list.size(); j++) {
                            exportDatas.add(list.get(j));
                        }
                    } else {
                        for (int j = max * (i - 1); j < 2000 * i; j++) {
                            exportDatas.add(list.get(j));
                        }
                    }

                    //下载单个excle
                    byte b[] = ExcelUtil.export("结算单", titleArray, exportDatas);

                    File f = new File(filePath + fileNameS);
                    FileUtils.writeByteArrayToFile(f, b, true);

                    fileNames.add(new File(filePath + fileNameS));
                }

                String zipName = "结算单-" + ExcelUtil.getSystemDate() + ".zip";
                // 导出压缩文件的全路径
                String zipFilePath = filePath + zipName;
                System.out.println(zipFilePath);
                // 导出zip
                File zip = new File(zipFilePath);

                // 下载
                FileDownloadUtils.setZipDownLoadHeadInfo(response, request, zipName);

                // 将excel文件生成压缩文件
                File srcFile[] = new File[fileNames.size()];
                for (int j = 0, n1 = fileNames.size(); j < n1; j++) {
                    srcFile[j] = new File(String.valueOf(fileNames.get(j)));
                }
                FileDownloadUtils.ZipFiles(srcFile, zip);

                OutputStream outputStream = response.getOutputStream();
                InputStream inputStream = new FileInputStream(zipFilePath);
                byte[] buffer = new byte[8192];
                int i = -1;
                while ((i = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, i);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                FileDownloadUtils.deleteDir(new File(filePath));
            }

        } catch (Exception e) {
            try {
                if (!response.isCommitted()) {
                    response.setContentType("text/html;charset=utf-8");
                    response.setHeader("Content-Disposition", "");
                    String html = FileDownloadUtils.getErrorHtml("下载失败");
                    response.getOutputStream().write(html.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return true;
    }
    //endregion 结算导出
}