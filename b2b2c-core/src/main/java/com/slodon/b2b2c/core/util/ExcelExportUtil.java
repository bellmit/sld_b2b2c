package com.slodon.b2b2c.core.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: slodon
 * @description: 导出工具类
 * @author: wuxy
 * @create: 2019-07-12 17:58
 **/
@Slf4j
public class ExcelExportUtil {

    public static SXSSFWorkbook exportToExcel(String[] titles, List<Map<String, Object>> exportDatas) {

        SXSSFWorkbook book = new SXSSFWorkbook();//创建excel表
        Sheet sheet = book.createSheet("sheet1");
        sheet.setDefaultColumnWidth(20);//设置默认行宽

        //表头样式（加粗，水平居中，垂直居中）
        CellStyle cellStyle = book.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中

        Font fontStyle = book.createFont();
        fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        cellStyle.setFont(fontStyle);

        //标题样式（加粗，垂直居中）
        CellStyle cellStyle2 = book.createCellStyle();
        cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        cellStyle2.setFont(fontStyle);

        //设置边框样式
        cellStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle2.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle2.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        //字段样式（垂直居中）
        CellStyle cellStyle3 = book.createCellStyle();
        cellStyle3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中

        //设置边框样式
        cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        Row sheet1Row = sheet.createRow(0);
        sheet1Row.setHeightInPoints(20);//行高

        /**
         * 设置列名
         */
        Cell cell = null;
        for (int i = 0; i < titles.length; i++) {
            cell = sheet1Row.createCell(i);
            cell.setCellValue(matchTitle(titles[i]));
            cell.setCellStyle(cellStyle);
        }

        /**
         * 设置列值
         */
        int rows = 1;
        for (Map<String, Object> data : exportDatas) {
            Row row = sheet.createRow(rows++);
            row.setHeightInPoints(20);//行高

            int initCellNo = 0;
            int titleSize = titles.length;
            for (int i = 0; i < titleSize; i++) {
                String key = titles[i];
                Object object = data.get(key);
                if (object == null) {
                    row.createCell(initCellNo).setCellValue("");
                } else {
                    row.createCell(initCellNo).setCellValue(String.valueOf(object));
                }
                initCellNo++;
            }
        }
        return book;
    }

    /**
     * 获取表头单元格样式
     * 居中，粗体
     * @param book
     * @return
     */
    public static CellStyle getTitleStyle(SXSSFWorkbook book){
        Font fontStyle = book.createFont();
        fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        CellStyle cellStyle = book.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        cellStyle.setFont(fontStyle);
        return cellStyle;
    }
    /**
     * 合并单元格
     *
     * @param titles
     * @param exportDatas
     * @return
     */
    public static SXSSFWorkbook exportToExcel2(String[] titles, List<Map<String, Object>> exportDatas) {

        SXSSFWorkbook book = new SXSSFWorkbook();//创建excel表
        Sheet sheet = book.createSheet("sheet1");
        sheet.setDefaultColumnWidth(20);//设置默认行宽

        //表头样式（加粗，水平居中，垂直居中）
        CellStyle cellStyle = book.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中

        Font fontStyle = book.createFont();
        fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        cellStyle.setFont(fontStyle);

        //标题样式（加粗，垂直居中）
        CellStyle cellStyle2 = book.createCellStyle();
        cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        cellStyle2.setFont(fontStyle);

        //设置边框样式
        cellStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle2.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle2.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        //字段样式（垂直居中）
        CellStyle cellStyle3 = book.createCellStyle();
        cellStyle3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中

        //设置边框样式
        cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        Row sheet1Row = sheet.createRow(0);
        sheet1Row.setHeightInPoints(20);//行高
        //需要合并的列
        int mergeNum = 0;

        /**
         * 设置列名
         */
        Cell cell = null;
        for (int i = 0; i < titles.length; i++) {
            cell = sheet1Row.createCell(i);
            cell.setCellValue(matchTitle(titles[i]));
            cell.setCellStyle(cellStyle);
            if (isMatchTitle(titles[i])) {
                log.info("需要合并的列===============================================" + titles[i]);
                mergeNum++;
            }
        }
        log.info("需要合并的列数===============================================" + mergeNum);
        int[] mergeIndex = new int[mergeNum];
        /*遍历该数据集合*/
        List<PoiModel> poiModels = Lists.newArrayList();
        int index = 1;/*这里1是从excel的第二行开始，第一行已经塞入标题了*/
        /**
         * 设置列值
         */
        int rows = 1;
        for (Map<String, Object> data : exportDatas) {
            Row row = sheet.createRow(rows++);
            row.setHeightInPoints(20);//行高

            int initCellNo = 0;
            int titleSize = titles.length;
            for (int i = 0; i < titleSize; i++) {
                String key = titles[i];
                Object object = data.get(key);

                String old = "";
                /*old存的是上一行统一位置的单元的值，第一行是最上一行了，所以从第二行开始记*/
                if (index > 1) {
                    old = poiModels.get(i) == null ? "" : poiModels.get(i).getContent();
                }
                /*循环需要合并的列*/
                for (int j = 0; j < mergeIndex.length; j++) {
                    /* 因为标题行前还有1行  所以index从1开始    也就是第二行*/
                    if (index == 1) {
                        /*记录第一行的开始行和开始列*/
                        PoiModel poiModel = new PoiModel();
                        poiModel.setOldContent(String.valueOf(object));
                        poiModel.setContent(String.valueOf(object));
                        poiModel.setRowIndex(1);
                        poiModel.setCellIndex(i);
                        poiModels.add(poiModel);
                        break;
                    } else if (i > 0 && mergeIndex[j] == i) {
                        /*这边i>0也是因为第一列已经是最前一列了，只能从第二列开始*/
                        /*当前同一列的内容与上一行同一列不同时，把那以上的合并, 或者在当前元素一样的情况下，前一列的元素并不一样，这种情况也合并*/
                        /*如果不需要考虑当前行与上一行内容相同，但是它们的前一列内容不一样则不合并的情况，把下面条件中||poiModels.get(i).getContent().equals(map.get(title[i])) && !poiModels.get(i - 1).getOldContent().equals(map.get(title[i-1]))去掉就行*/
                        String key2 = titles[i - 1];
                        Object object2 = data.get(key2);
                        if (!poiModels.get(i).getContent().equals(String.valueOf(object)) || poiModels.get(i).getContent().equals(String.valueOf(object))
                                && !poiModels.get(i - 1).getOldContent().equals(String.valueOf(object2))) {
                            /*当前行的当前列与上一行的当前列的内容不一致时，则把当前行以上的合并*/
                            CellRangeAddress cra = new CellRangeAddress(poiModels.get(i).getRowIndex()/*从第二行开始*/, index - 1/*到第几行*/,
                                    poiModels.get(i).getCellIndex()/*从某一列开始*/, poiModels.get(i).getCellIndex()/*到第几列*/);
                            //在sheet里增加合并单元格
                            sheet.addMergedRegion(cra);
                            /*重新记录该列的内容为当前内容，行标记改为当前行标记，列标记则为当前列*/
                            poiModels.get(i).setContent(String.valueOf(object));
                            poiModels.get(i).setRowIndex(index);
                            poiModels.get(i).setCellIndex(i);
                        }
                    }
                    /*处理第一列的情况*/
                    if (mergeIndex[j] == i && i == 0 && !poiModels.get(i).getContent().equals(String.valueOf(object))) {
                        /*当前行的当前列与上一行的当前列的内容不一致时，则把当前行以上的合并*/
                        CellRangeAddress cra = new CellRangeAddress(poiModels.get(i).getRowIndex()/*从第二行开始*/, index - 1/*到第几行*/,
                                poiModels.get(i).getCellIndex()/*从某一列开始*/, poiModels.get(i).getCellIndex()/*到第几列*/);
                        //在sheet里增加合并单元格
                        sheet.addMergedRegion(cra);
                        /*重新记录该列的内容为当前内容，行标记改为当前行标记*/
                        poiModels.get(i).setContent(String.valueOf(object));
                        poiModels.get(i).setRowIndex(index);
                        poiModels.get(i).setCellIndex(i);
                    }

                    /*最后一行没有后续的行与之比较，所有当到最后一行时则直接合并对应列的相同内容  加1是因为标题行前面还有1行*/
                    if (mergeIndex[j] == i && index == exportDatas.size() + 1) {
                        CellRangeAddress cra = new CellRangeAddress(poiModels.get(i).getRowIndex()/*从第二行开始*/, index/*到第几行*/,
                                poiModels.get(i).getCellIndex()/*从某一列开始*/, poiModels.get(i).getCellIndex()/*到第几列*/);
                        //在sheet里增加合并单元格
                        sheet.addMergedRegion(cra);
                    }
                }

                if (object == null) {
                    row.createCell(initCellNo).setCellValue("");
                } else {
                    row.createCell(initCellNo).setCellValue(String.valueOf(object));
                }
                /*在每一个单元格处理完成后，把这个单元格内容设置为old内容*/
                poiModels.get(i).setOldContent(old);
                initCellNo++;
            }
            index++;
        }
        return book;
    }

    /**
     * 是否是图片
     *
     * @param title
     * @return
     */
    private static boolean isMatchTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            return false;
        }
        List<String> list = new ArrayList<>();
        list.add("orderSn");
        list.add("orderState");
        list.add("orderAmount");
        list.add("expressFee");
        list.add("memberId");
        list.add("memberName");
        list.add("storeId");
        list.add("storeName");
        list.add("createTime");
        list.add("payTime");
        list.add("paymentName");
        list.add("finishTime");
        list.add("receiverName");
        list.add("receiverMobile");
        list.add("receiverAreaInfo");
        list.add("expressNumber");
        list.add("deliverName");
        list.add("deliverMobile");
        list.add("deliverAreaInfo");
        list.add("invoice");
        return list.contains(title);
    }

    private static String matchTitle(String column) {
        if ("orderSn".equalsIgnoreCase(column)) return "订单号";
        if ("orderState".equalsIgnoreCase(column)) return "订单状态";
        if ("orderAmount".equalsIgnoreCase(column)) return "订单总额";
        if ("expressFee".equalsIgnoreCase(column)) return "运费";
        if ("memberId".equalsIgnoreCase(column)) return "买家id";
        if ("memberName".equalsIgnoreCase(column)) return "买家名称";
        if ("storeId".equalsIgnoreCase(column)) return "店铺id";
        if ("storeName".equalsIgnoreCase(column)) return "店铺名称";
        if ("createTime".equalsIgnoreCase(column)) return "下单时间";
        if ("payTime".equalsIgnoreCase(column)) return "支付时间";
        if ("paymentName".equalsIgnoreCase(column)) return "支付方式";
        if ("finishTime".equalsIgnoreCase(column)) return "完成时间";
        if ("receiverName".equalsIgnoreCase(column)) return "收货人姓名";
        if ("receiverMobile".equalsIgnoreCase(column)) return "收货人电话";
        if ("receiverAreaInfo".equalsIgnoreCase(column)) return "收货人地址";
        if ("expressNumber".equalsIgnoreCase(column)) return "发货单号";
        if ("deliverName".equalsIgnoreCase(column)) return "发货人姓名";
        if ("deliverMobile".equalsIgnoreCase(column)) return "发货人电话";
        if ("deliverAreaInfo".equalsIgnoreCase(column)) return "发货人地址";
        if ("invoice".equalsIgnoreCase(column)) return "发票信息";
        if ("goodsName".equalsIgnoreCase(column)) return "商品名称";
        if ("specValues".equalsIgnoreCase(column)) return "商品规格";
        if ("goodsNum".equalsIgnoreCase(column)) return "商品数量";
        if ("goodsPrice".equalsIgnoreCase(column)) return "商品单价(元)";

        if ("deliverTime".equalsIgnoreCase(column)) return "发货时间";
        if ("expressName".equalsIgnoreCase(column)) return "物流公司";
        if ("moneyDiscount".equalsIgnoreCase(column)) return "优惠总金额";
        if ("balanceAmount".equalsIgnoreCase(column)) return "余额账户支付总金额";
        if ("cashAmount".equalsIgnoreCase(column)) return "现金支付金额";
        if ("voucherAmount".equalsIgnoreCase(column)) return "优惠券优惠金额";
        if ("moneyPromotionFull".equalsIgnoreCase(column)) return "订单满减金额";
        if ("integralCashAmount".equalsIgnoreCase(column)) return "积分换算金额";
        if ("refundAmount".equalsIgnoreCase(column)) return "退款的金额";
        if ("orderGoods".equalsIgnoreCase(column)) return "订单商品";
        if ("memberNickName".equalsIgnoreCase(column)) return "会员昵称";
        if ("memberTrueName".equalsIgnoreCase(column)) return "真实姓名";
        if ("memberEmail".equalsIgnoreCase(column)) return "会员邮箱";
        if ("memberMobile".equalsIgnoreCase(column)) return "手机号";
        if ("memberAvatar".equalsIgnoreCase(column)) return "会员头像";
        if ("gender".equalsIgnoreCase(column)) return "会员性别";
        if ("registerTime".equalsIgnoreCase(column)) return "会员注册时间";
        if ("memberIntegral".equalsIgnoreCase(column)) return "会员积分";
        if ("balanceAvailable".equalsIgnoreCase(column)) return "预存款可用金额";
        if ("balanceFrozen".equalsIgnoreCase(column)) return "预存款冻结金额";
        if ("isAllowBuy".equalsIgnoreCase(column)) return "会员是否有购买权限 开启/关闭";
        if ("experienceValue".equalsIgnoreCase(column)) return "会员经验值";
        return column;
    }

    /**
     * 日期转化为字符串,格式为yyyy-MM-dd-HH-mm-ss
     *
     * @param date
     * @return
     */
    public static String getCnDate(Date date) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (null != date) {
            return sdf.format(date);
        }
        return null;
    }

    /**
     * 日期转化为字符串,格式为yyyy-MM-dd-HH-mm-ss
     *
     * @return
     */
    public static String getSystemDate() {
        //得到long类型当前时间
        long l = System.currentTimeMillis();
        //new日期对
        Date date = new Date(l);
        String format = "yyyy-MM-dd-HH-mm-ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
