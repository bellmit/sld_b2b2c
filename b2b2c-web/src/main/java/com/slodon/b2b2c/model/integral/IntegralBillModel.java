package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.ExcelUtil;
import com.slodon.b2b2c.core.util.FileDownloadUtils;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.integral.IntegralBillReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralOrderReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralBillLogWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralBillOrderBindWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralBillWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralOrderWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralBillExample;
import com.slodon.b2b2c.integral.example.IntegralOrderExample;
import com.slodon.b2b2c.integral.pojo.IntegralBill;
import com.slodon.b2b2c.integral.pojo.IntegralBillLog;
import com.slodon.b2b2c.integral.pojo.IntegralBillOrderBind;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.example.StoreExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.vo.integral.IntegralBillExportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 积分商城结算表model
 */
@Component
@Slf4j
public class IntegralBillModel {

    @Resource
    private IntegralBillReadMapper integralBillReadMapper;
    @Resource
    private IntegralBillWriteMapper integralBillWriteMapper;
    @Resource
    private IntegralOrderReadMapper integralOrderReadMapper;
    @Resource
    private IntegralOrderWriteMapper integralOrderWriteMapper;
    @Resource
    private IntegralBillOrderBindWriteMapper integralBillOrderBindWriteMapper;
    @Resource
    private IntegralBillLogWriteMapper integralBillLogWriteMapper;
    @Resource
    private StoreModel storeModel;

    /**
     * 新增积分商城结算表
     *
     * @param integralBill
     * @return
     */
    public Integer saveIntegralBill(IntegralBill integralBill) {
        int count = integralBillWriteMapper.insert(integralBill);
        if (count == 0) {
            throw new MallException("添加积分商城结算表失败，请重试");
        }
        return count;
    }

    /**
     * 根据billId删除积分商城结算表
     *
     * @param billId billId
     * @return
     */
    public Integer deleteIntegralBill(Integer billId) {
        if (StringUtils.isEmpty(billId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralBillWriteMapper.deleteByPrimaryKey(billId);
        if (count == 0) {
            log.error("根据billId：" + billId + "删除积分商城结算表失败");
            throw new MallException("删除积分商城结算表失败,请重试");
        }
        return count;
    }

    /**
     * 根据billId更新积分商城结算表
     *
     * @param integralBill
     * @return
     */
    public Integer updateIntegralBill(IntegralBill integralBill) {
        if (StringUtils.isEmpty(integralBill.getBillId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralBillWriteMapper.updateByPrimaryKeySelective(integralBill);
        if (count == 0) {
            log.error("根据billId：" + integralBill.getBillId() + "更新积分商城结算表失败");
            throw new MallException("更新积分商城结算表失败,请重试");
        }
        return count;
    }

    /**
     * 根据billId获取积分商城结算表详情
     *
     * @param billId billId
     * @return
     */
    public IntegralBill getIntegralBillByBillId(Integer billId) {
        return integralBillReadMapper.getByPrimaryKey(billId);
    }

    /**
     * 根据条件获取积分商城结算表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralBill> getIntegralBillList(IntegralBillExample example, PagerInfo pager) {
        List<IntegralBill> integralBillList;
        if (pager != null) {
            pager.setRowsCount(integralBillReadMapper.countByExample(example));
            integralBillList = integralBillReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralBillList = integralBillReadMapper.listByExample(example);
        }
        return integralBillList;
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
        if (CollectionUtils.isEmpty(storeList)) {
            //当天没有需要结算的店铺
            return;
        }

        for (Store store : storeList) {
            //查询当前店铺可结算的订单
            List<IntegralOrder> billOrderList = this.getBillOrderList(store.getStoreId());
            if (CollectionUtils.isEmpty(billOrderList)) {
                //当前店铺没有要结算的订单
                continue;
            }

            //-保存结算单、-保存订单结算绑定关系
            IntegralBill bill = this.jobAddBill(store, billOrderList);

            //-保存结算日志
            this.saveBillLog(bill.getBillSn());

            //-修改订单结算状态
            billOrderList.forEach(order -> {
                IntegralOrder updateOrder = new IntegralOrder();
                updateOrder.setOrderId(order.getOrderId());
                updateOrder.setIsSettlement(OrderConst.IS_SETTLEMENT_YES);
                integralOrderWriteMapper.updateByPrimaryKeySelective(updateOrder);
            });
        }
    }

    /**
     * 获取今日结算的店铺列表
     *
     * @return
     */
    private List<Store> getBillStoreList() {
        //获取当前日
        String today = TimeUtil.getDay();
        //获取今天星期几
        Integer week = TimeUtil.getYesterdayOfWeek(0);

        //按月查询当天结算的店铺
        StoreExample example = new StoreExample();
        example.setBillType(StoreConst.BILL_TYPE_MONTH);
        example.setBillDay(today);
        List<Store> storeList = storeModel.getStoreList(example,null);

        //按周查询当天结算的店铺
        example.setBillType(StoreConst.BILL_TYPE_WEEK);
        example.setBillDay(week.toString());
        storeList.addAll(storeModel.getStoreList(example,null));

        return storeList;
    }

    /**
     * 获取店铺可结算的订单
     *
     * @param storeId 店铺id
     * @return
     */
    private List<IntegralOrder> getBillOrderList(Long storeId) {
        IntegralOrderExample ordersExample = new IntegralOrderExample();
        ordersExample.setStoreId(storeId);
        ordersExample.setOrderState(OrderConst.ORDER_STATE_40);//已完成的订单
        ordersExample.setIsSettlement(OrderConst.IS_SETTLEMENT_NO);//未结算的订单
        List<IntegralOrder> orderList = integralOrderReadMapper.listByExample(ordersExample);
        if (CollectionUtils.isEmpty(orderList)) {
            //当前店铺没有要结算的订单
            return null;
        }
        return orderList;
    }

    /**
     * 生成结算单,保存订单结算绑定关系
     *
     * @param store         结算的店铺
     * @param billOrderList 店铺内可结算的订单
     * @return
     */
    private IntegralBill jobAddBill(Store store, List<IntegralOrder> billOrderList) {
        //结算开始
        BigDecimal zero = new BigDecimal("0.00");
        String billSn = GoodsIdGenerator.getBillSn();//结算单号
        IntegralBill bill = new IntegralBill();
        bill.setBillSn(billSn);
        bill.setStoreId(store.getStoreId());
        bill.setStoreName(store.getStoreName());
        bill.setStartTime(this.getBillStartTime(store));
        bill.setEndTime(new Date());
        bill.setCashAmount(zero);//订单表获取
        bill.setIntegralCashAmount(zero);//订单货品表获取
        bill.setSettleAmount(zero);//自行计算
        bill.setState(BillConst.STATE_1);
        bill.setCreateTime(new Date());
        bill.setUpdateTime(new Date());

        for (IntegralOrder order : billOrderList) {
            //构造订单结算绑定关系
            IntegralBillOrderBind billOrderBind = new IntegralBillOrderBind();
            billOrderBind.setBillSn(billSn);
            billOrderBind.setOrderSn(order.getOrderSn());
            billOrderBind.setCashAmount(order.getOrderAmount());
            billOrderBind.setIntegral(order.getIntegral());
            billOrderBind.setIntegralCashAmount(order.getIntegralCashAmount());
            billOrderBind.setCreateTime(order.getCreateTime());
            billOrderBind.setFinishTime(order.getFinishTime());

            bill.setCashAmount(bill.getCashAmount().add(order.getOrderAmount()));
            bill.setIntegral(order.getIntegral());
            bill.setIntegralCashAmount(bill.getIntegralCashAmount().add(order.getIntegralCashAmount()));

            //-保存订单结算绑定关系
            int insert = integralBillOrderBindWriteMapper.insert(billOrderBind);
            AssertUtil.isTrue(insert == 0, "结算单与订单绑定保存失败！");
        }

        //计算结算费用:订单金额+积分抵现金额
        bill.setSettleAmount(bill.getCashAmount().add(bill.getIntegralCashAmount()));

        this.saveIntegralBill(bill);
        return bill;
    }

    /**
     * 获取店铺的结算开始时间
     *
     * @param store
     * @return
     */
    private Date getBillStartTime(Store store) {
        IntegralBillExample billExample = new IntegralBillExample();
        billExample.setStoreId(store.getStoreId());
        List<IntegralBill> billList = integralBillReadMapper.listByExample(billExample);
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
    private void saveBillLog(String billSn) {
        IntegralBillLog billLog = new IntegralBillLog();
        billLog.setBillSn(billSn);
        billLog.setOperatorId(0L);
        billLog.setOperatorName("system");
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_1);
        billLog.setContent("生成结算单");
        billLog.setCreateTime(new Date());
        int insert = integralBillLogWriteMapper.insert(billLog);
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
    public boolean billExport(HttpServletRequest request, HttpServletResponse response, IntegralBillExample example) {
        List<IntegralBill> billList = integralBillReadMapper.listByExample(example);
        List<IntegralBillExportVO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(billList)) {
            billList.forEach(bill -> {
                list.add(new IntegralBillExportVO(bill));
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
        String titles = "ID,店铺名称,开始时间,结束时间,现金使用金额,积分使用数量,积分抵扣金额,应结金额,结算状态";
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
                    List<IntegralBillExportVO> exportDatas = new ArrayList<>();
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