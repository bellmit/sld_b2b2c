package com.slodon.b2b2c.core.facesheet;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 快递鸟生成面单接口返回值接受
 */
@Data
public class GenerateFaceSheetResult implements Serializable {

    private static final long serialVersionUID = -2330476570016806730L;
    private String order;

    private String eBusinessID;

    private String resultCode;

    private String reason;

    private String success;

    public GenerateFaceSheetResult() {
    }
    public GenerateFaceSheetResult(String str) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        this.order = jsonObject.getString("Order");
        this.eBusinessID = jsonObject.getString("EBusinessID");
        this.resultCode = jsonObject.getString("ResultCode");
        this.reason = jsonObject.getString("Reason");
        this.success = jsonObject.getString("Success");
    }
}
