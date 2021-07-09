package com.slodon.b2b2c.business.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * excel导出字段枚举
 */
public enum ExportEnum {
    DEFAULT("default","默认",10,false),
    ORDER_SN("orderSn","订单号",15,false),
    ORDER_STATE("orderState","订单状态",10,false),
    ORDER_AMOUNT("orderAmount","订单金额",10,false),
    EXPRESS_FEE("expressFee","物流费用",10,false),
    MEMBER_ID("memberId","买家id",10,false),
    MEMBER_NAME("memberName","买家名称",15,false),
    STORE_ID("storeId","店铺id",10,false),
    STORE_NAME("storeName","店铺名称",21,false),
    CREATE_TIME("createTime","下单时间",21,false),
    PAY_TIME("payTime","支付时间",21,false),
    PAYMENT_NAME("paymentName","支付方式(名称)",15,false),
    FINISH_TIME("finishTime","订单完成时间",21,false),
    RECEIVER_NAME("receiverName","收货人姓名",12,false),
    RECEIVER_MOBILE("receiverMobile","收货人电话",13,false),
    RECEIVER_AREA_INFO("receiverAreaInfo","收货地址",50,false),
    EXPRESS_NUMBER("expressNumber","发货单号",10,false),
    DELIVER_NAME("deliverName","发货人姓名",12,false),
    DELIVER_MOBILE("deliverMobile","发货人电话",13,false),
    DELIVER_AREA_INFO("deliverAreaInfo","发货地址",50,false),
    INVOICE("invoice","发票",50,false),
    GOODS_NAME("goodsName","商品名称",21,true),
    SPEC_VALUES("specValues","商品规格",21,true),
    GOODS_NUM("goodsNum","商品数量",10,true),
    GOODS_PRICE("goodsPrice","商品单价(元)",10,true);

    private static Map<String/*fieldName*/,ExportEnum> enumMap = new HashMap<>();
    static {
        ExportEnum[] values = ExportEnum.values();
        for (ExportEnum value : values) {
            enumMap.put(value.getFieldName(),value);
        }
    }

    ExportEnum(String fieldName, String des, Integer columnWidth, Boolean childField) {
        this.fieldName = fieldName;
        this.des = des;
        this.columnWidth = columnWidth;
        this.childField = childField;
    }

    /**
     * 根据字段名称获取导出配置信息
     * @param fieldName 字段名称
     * @return
     */
    public static ExportEnum getInfoByFieldName(String fieldName){
        if (!enumMap.containsKey(fieldName)){
            return of(fieldName);
        }
        return enumMap.get(fieldName);
    }

    /**
     * 自定义导出字段
     * @param fieldName
     * @return
     */
    private static ExportEnum of(String fieldName){
        ExportEnum aDefault = ExportEnum.valueOf("DEFAULT");
        aDefault.setChildField(false);
        aDefault.setColumnWidth(10);
        aDefault.setDes(fieldName);
        aDefault.setFieldName(fieldName);
        return aDefault;
    }

    private String fieldName;//导出字段名
    private String des;//导出字段中文描述
    private Integer columnWidth;//字段列宽
    private Boolean childField;//是否子字段，true==是，比如订单导出时，订单货品字段为子字段,子字段不需要合并单元格，非子字段根据业务需要合并单元格

    public String getFieldName() {
        return fieldName;
    }

    public String getDes() {
        return des;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public Boolean getChildField() {
        return childField;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
    }

    public void setChildField(Boolean childField) {
        this.childField = childField;
    }
}
