package com.slodon.b2b2c.core.express;


import com.slodon.b2b2c.core.facesheet.GenerateFaceSheetRequest;
import com.slodon.b2b2c.core.facesheet.printFaceSheetRequest;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * @description: 快递鸟电子面单相关api
 */
public class FaceSheetAPI {

    /**
     * 封装请求打印接口参数信息
     *
     * @param ip
     * @param EBussinessID
     * @param AppKey
     * @param ordersSns
     * @return
     * @throws Exception
     */
    public static printFaceSheetRequest getPrintParam(String ip, String EBussinessID, String AppKey, String ordersSns) throws Exception {
        String[] orderSnArr = ordersSns.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        for (String orderSn : orderSnArr) {
            if (stringBuffer.length() == 1) {
                stringBuffer.append("{\"OrderCode\":" + orderSn + ",\"PortName\":\"GP\"}");
            } else {
                stringBuffer.append(",{\"OrderCode\":" + orderSn + ",\"PortName\":\"GP\"}");
            }
        }
        stringBuffer.append("]");
        String data = stringBuffer.toString();
        printFaceSheetRequest requestInfo = new printFaceSheetRequest();
        requestInfo.setEBusinessID(EBussinessID);
        requestInfo.setDataSign(encrpy(ip + data, AppKey));
        requestInfo.setRequestData(URLEncoder.encode(data, "UTF-8"));
        requestInfo.setIsPreview(1);
        return requestInfo;
    }

    private static String encrpy(String content, String key) throws UnsupportedEncodingException, Exception {
        String charset = "UTF-8";
        return Base64.encode(MD5(content + key, charset).getBytes(charset));
    }

    /**
     * MD5加密
     *
     * @param str     内容
     * @param charset 编码方式
     * @throws Exception
     */
    private static String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * base64编码
     *
     * @param str     内容
     * @param charset 编码方式
     * @throws UnsupportedEncodingException
     */
    private static String base64(String str, String charset) throws UnsupportedEncodingException {
        String encoded = Base64.encode(str.getBytes(charset));
        return encoded;
    }

    public static String urlEncoder(String str, String charset) throws UnsupportedEncodingException {
        String result = URLEncoder.encode(str, charset);
        return result;
    }

    /**
     * 电商Sign签名生成
     *
     * @param content  内容
     * @param keyValue Appkey
     * @param charset  编码方式
     * @return DataSign签名
     * @throws UnsupportedEncodingException ,Exception
     */
    public static String encrypt(String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception {
        if (keyValue != null) {
            return base64(MD5(content + keyValue, charset), charset);
        }
        return base64(MD5(content, charset), charset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url              发送请求的 URL
     * @param faceSheetRequest 请求的参数集合
     * @return 远程资源的响应结果
     */
    public static String sendPost(String url, GenerateFaceSheetRequest faceSheetRequest) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
//        GenerateFaceSheetResult faceSheetResult = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (faceSheetRequest != null) {
                out.write(faceSheetRequest.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
//            faceSheetResult = new GenerateFaceSheetResult(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
//        return faceSheetResult;
        return result.toString();
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws IOException
     */
    public final static String getIpAddress(HttpServletRequest request) throws IOException {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

}
