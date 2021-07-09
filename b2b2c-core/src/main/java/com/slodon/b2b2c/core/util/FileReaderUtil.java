package com.slodon.b2b2c.core.util;


import com.slodon.b2b2c.core.exception.MallException;
import io.minio.MinioClient;
import io.minio.policy.PolicyType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: slodon
 * @description: csv等导入工具类
 * @author: spp
 **/
public class FileReaderUtil {

    /**
     * 读取csv文件
     *
     * @param file
     * @return
     */
    public static List<List<String>> readCsv(MultipartFile file) {

        //文件大小限制
        if (file.isEmpty() || file.getSize() == 0L){
            throw new MallException("文件无效");
        }
        if (file.getSize() > 2 * 1024 * 1024L){
            //最大2M
            throw new MallException("上传文件不能超过2M,请通过excel软件编辑拆成多个文件再导入");
        }
        //扩展名，如 https://jbbcimgdev.slodon.cn/files/seller/csvFiles/%E5%95%86%E5%93%81csv%E5%AF%BC%E5%85%A5.csv
        String extend = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."))
                .toLowerCase();
        AssertUtil.isTrue(!isCsv(extend),"文档格式有误");

        List<List<String>> dataList = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            //一般如果生产文件乱码，windows下用gbk，linux用UTF-8
            inputStream = file.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
            String line = "";
            //跳过表头
            reader.readLine();
            // 逐条读取记录，直至读完
            while ((line = reader.readLine()) != null) {
                // 读一整行
                if (StringUtil.isEmpty(line)) {
                    continue;
                }
                dataList.add(Arrays.asList(line.split(",")));
            }
        } catch (Exception e) {
            throw new MallException("csv文件读取异常:" + e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream.close();
                } catch (Exception e) {
                    throw new MallException("csv文件读取异常:" + e);
                }
            }
        }
        return dataList;
    }

    /**
     * csv上传文件
     *
     * @param image       要上传的图片,如 https://jbbcimgdev.slodon.cn/images/seller/goods/BYfSNJBE2TNiDR3or9A.jpg
     * @param url         minio地址,图片服务器url
     * @param port        minio端口号,图片服务器端口
     * @param accessKey   minio账号
     * @param secretKey   minio秘钥
     * @param path        bucketName+path图片服务器相对路径,即图片要上传的位置
     * @param bucketName  bucketName+path图片服务器相对路径,即图片要上传的位置
     * @param contentType 类型,     目前只校验图片,字符串=text/html,图片=image/jpeg,视频=video/mp4,文件=application/octet-stream
     * @return
     */
    public static String uploadFile(String image, String url, int port, String accessKey, String secretKey, String path, String bucketName, String contentType) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(image.getBytes());
        //获取图片扩展名，如 .jpg
        String extend = image.substring(image.lastIndexOf("."), image.length()).toLowerCase();

        //处理path,前后都加上 /
        if (StringUtils.isEmpty(path)) {
            path = "/";
        } else {
            if (!path.endsWith("/")) {
                path += "/";
            }
            if (path.indexOf("/") != 0) {
                path = "/" + path;
            }
        }
        //生成新的文件名
        String objectName = path + ShortUUID.generate() + extend;

        MinioClient minioClient = new MinioClient(url + ":" + port, accessKey, secretKey);
        //查询桶是否已存在，不存在则创建桶
        boolean bucketExists = minioClient.bucketExists(bucketName);
        if (!bucketExists) {
            minioClient.makeBucket(bucketName);
            //创建安全级别
            minioClient.setBucketPolicy(bucketName, "*", PolicyType.READ_ONLY);
        }
        //上传文件
        minioClient.putObject(bucketName, objectName, inputStream, contentType);

        return "/" + bucketName + objectName;
    }

    /**
     * 是否是图片
     *
     * @param extend
     * @return
     */
    private static boolean isImg(String extend, InputStream image) {
        if (StringUtils.isEmpty(extend)) {
            return false;
        }
        List<String> list = new ArrayList<>();
        list.add(".jpg");
        list.add(".jpeg");
        list.add(".bmp");
        list.add(".gif");
        list.add(".png");
        list.add(".tif");
        boolean isImg = list.contains(extend.toLowerCase());
        if (isImg) {
            //后缀是图片，校验是否为修改后缀的其他文件类型
            try {
                ImageIO.read(image);
            } catch (IOException e) {
                //无法读取图片信息
                isImg = false;
            }
        }
        return isImg;
    }

    /**
     * 是否是csv文档
     * @param extend
     * @return
     */
    private static boolean isCsv(String extend) {
        if (StringUtils.isEmpty(extend)){
            return false;
        }
        List<String> list = new ArrayList<>();
        list.add(".csv");
        return list.contains(extend.toLowerCase());
    }

}
