package com.slodon.b2b2c.util;

import com.slodon.b2b2c.business.dto.OrderExportDTO;
import com.slodon.b2b2c.business.enums.ExportEnum;
import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.ExcelExportUtil;
import com.slodon.b2b2c.model.business.OrderModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订单导出工具类
 */
@Data
@Slf4j
public class OrderExportUtil {
    private Map<String/*文件名称*/, SXSSFWorkbook/*excel文件流*/> excelMap = new HashMap<>();
    private OrderModel orderModel;
    private OrderExample orderExample;
    private String orderCode;//要导出的字段
    private final int perSize = 2000;//每页订单数
    private final int cpu = 4;//服务器cpu数

    public OrderExportUtil(OrderModel orderModel, OrderExample orderExample, String orderCode) {
        this.orderModel = orderModel;
        this.orderExample = orderExample;
        this.orderCode = orderCode;
    }

    /**
     * 执行订单导出
     * @return 导出的excel集合
     * @throws InterruptedException
     */
    public Map<String/*文件名称*/, SXSSFWorkbook/*excel文件流*/> doExport() throws InterruptedException {
        //查询订单总数
        Integer orderCount = orderModel.getOrderCount(orderExample);
        AssertUtil.isTrue(orderCount == 0,"请选择要导出的订单");

        List<OrderExportDTO> exportOrderList = orderModel.getExportOrderList(orderExample);
        int pageSize = orderCount / perSize;//生成的excel总数
        if (orderCount % perSize != 0){
            //导出订单数不是每页订单数的整数倍
            pageSize ++;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(cpu);
        for (int i = 0; i < pageSize; i++) {
            int start = perSize * i;//每页的起始订单列表下标
            int end = (i == pageSize-1) //是否为最后一页
                    ? orderCount
                    : perSize * (i+1);
            //excel文件名
            String fileName = pageSize > 1 //总共导出大于1页，需要压缩
                    ? "导出订单-" + (i+1) + ".xls"
                    : "导出订单-" + ExcelExportUtil.getSystemDate() + ".xls";
            //放入线程池执行，生成excel
            ExportThread exportThread = new ExportThread(exportOrderList.subList(start,end),fileName);
            executorService.execute(exportThread);
        }

        while (excelMap.size() != pageSize){
            //阻塞等待订单导出完毕
            Thread.sleep(200);
        }
        return excelMap;
    }

    /**
     * 导出一个excel的线程
     */
    public class ExportThread implements Runnable{

        private List<OrderExportDTO> subList;//本页的订单列表
        private String fileName;//文件名

        public ExportThread(List<OrderExportDTO> subList, String fileName) {
            this.subList = subList;
            this.fileName = fileName;
        }

        @Override
        public void run() {

            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
            Sheet sheet = sxssfWorkbook.createSheet();
            int rowIndex = 0;
            Row titleRow = sheet.createRow(rowIndex++);
            List<ExportEnum> exportEnumList = new ArrayList<>();//将导出字段转化为枚举信息
            //写表头
            String[] titleArray = orderCode.split(",");
            for (int i = 0; i < titleArray.length; i++) {
                String fieldName = titleArray[i];
                //获取字段导出信息
                ExportEnum exportEnum = ExportEnum.getInfoByFieldName(fieldName);
                exportEnumList.add(exportEnum);
                //设置列宽
                sheet.setColumnWidth(i,256*exportEnum.getColumnWidth()+184);
                Cell cell = titleRow.createCell(i);
                cell.setCellStyle(ExcelExportUtil.getTitleStyle(sxssfWorkbook));
                cell.setCellValue(exportEnum.getDes());
            }

            //写订单数据
            for (OrderExportDTO orderExportDTO : subList) {
                int orderProductNum = orderExportDTO.getProductList().size();//订单货品数量
                if (orderProductNum < 1){
                    //订单有问题，跳过
                    log.error("订单：{}无订单货品",orderExportDTO.getOrderSn());
                } else {
                    if (orderProductNum > 1) {
                        //多订单货品，校验是否导出订单货品字段，
                        boolean hasChildFields = false;
                        for (ExportEnum exportEnum : exportEnumList) {
                            if (exportEnum.getChildField()) {
                                //只要包含一个订单货品字段，即需要合并订单公共信息
                                hasChildFields = true;
                                break;
                            }
                        }

                        if (hasChildFields){
                            // 订单公共信息合并单元格
                            for (int i = 0; i < exportEnumList.size(); i++) {
                                ExportEnum exportEnum = exportEnumList.get(i);
                                if (!exportEnum.getChildField()) {
                                    //非子字段，将订单公共信息合并展示
                                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + orderProductNum - 1, i, i));
                                }
                            }
                        }
                    }
                    //每个订单货品一行数据
                    for (int r = 0; r < orderExportDTO.getProductList().size(); r++) {
                        Row row = sheet.createRow(rowIndex++);
                        for (int i = 0; i < exportEnumList.size(); i++) {
                            ExportEnum exportEnum = exportEnumList.get(i);
                            if (r != 0 && !exportEnum.getChildField()){
                                //订单公共信息，只在第一行写，其他跳过
                                continue;
                            }
                            Object value;
                            if (exportEnum.getChildField()){
                                //订单货品字段
                                value = getGetValue(exportEnum.getFieldName(),orderExportDTO.getProductList().get(r));
                            }else {
                                //订单的字段
                                value = getGetValue(exportEnum.getFieldName(),orderExportDTO);
                            }
                            //写入单元格
                            row.createCell(i).setCellValue(value == null ? null : value.toString());
                        }
                    }
                }
            }

            excelMap.put(fileName,sxssfWorkbook);
        }

        /**
         * 获取一个对象中，某个字段的值
         *
         * @param key 字段名称
         * @param o 对象
         * @return 字段值
         */
        private Object getGetValue(String key, Object o) {
            Class clazz = o.getClass();
            try {
                Field f = clazz.getDeclaredField(key);
                f.setAccessible(true);
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(f.getName(), clazz);
                Method method = propertyDescriptor.getReadMethod();
                return method.invoke(o);
            } catch (Exception e) {
                log.error("获取字段：{}的值出错",key,e);
            }
            return null;
        }
    }
}
