package com.slodon.b2b2c.core.sms;

import com.google.gson.Gson;
import com.slodon.b2b2c.core.constant.SMSConst;
import com.slodon.b2b2c.core.random.RandomUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信验证码发送服务
 */
public class YunPianSmsSender {

    /**
     * 短信请求url（智能匹配模板发送接口的http地址）
     */
    private static final String URL = "https://sms.yunpian.com/v2/sms/single_send.json";

    /**
     * 短信内容
     */
    private String content = "";

    /**
     * 验证码
     */
    private String verifyCode;
    private String mobile;
    private String apiKey;


    public YunPianSmsSender(Map map, int type){
        this.mobile = map.get("mobile").toString();
        this.apiKey = map.get("apiKey").toString();

        if (type == SMSConst.NOTIFICATION_SMS) { //通知类短信
            this.content = map.get("tplContent").toString();
        }else if (type == SMSConst.VERIFY_SMS) {  //验证码类短信
            this.verifyCode = RandomUtil.randomNumber(6);
            this.content = map.get("tplContent").toString().replace("#verifyCode#", this.verifyCode)
                    .replace("#app#", map.get("app").toString());
        }
    }

    /**
     * 调用云片接口发送短信方法
     *
     * @return
     */
    public YunPianJsonResult sendSMS() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response;
        Map<String, String> sendSMSParamMap = setSendSMSParamMap();
        try {

            HttpPost httpPost = new HttpPost(URL);

            // 设置请求的header
            httpPost.addHeader("Accept", "application/json;charset=utf-8;");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            // 设置请求的的参数
            List<NameValuePair> nvps = new ArrayList<>();

            for (Map.Entry<String, String> param : sendSMSParamMap.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                nvps.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            // 执行请求
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, "utf-8");
            }

            Gson gson = new Gson();
            YunPianJsonResult vcr = gson.fromJson(responseText, YunPianJsonResult.class);
            if (this.verifyCode != null) {
                vcr.setVerifyCode(this.verifyCode);
            }
            return vcr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 短信发送需要的参数,对应短信模板的替换变量,短信发送子类需实现此方法
     *
     * @return
     */
    private Map<String, String> setSendSMSParamMap(){
        Map<String, String> param = new HashMap<>();
        param.put("apikey", apiKey);
        param.put("text", content);
        param.put("mobile", mobile);
        return param;
    }
}
