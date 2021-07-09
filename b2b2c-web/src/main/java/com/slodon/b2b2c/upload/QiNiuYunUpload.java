package com.slodon.b2b2c.upload;

import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.upload.base.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 七牛云上传文件
 */
@Slf4j
public class QiNiuYunUpload extends Upload {

    public QiNiuYunUpload(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    /**
     * 上传文件
     *
     * @param file     要上传的文件
     * @param fileName 文件名，必须以'/'开头，如 /images/seller/goods/0Kwf1FTdCzZo5J7Siph.png
     */
    @Override
    public String upload(MultipartFile file, String fileName) {
        String qiNiuTokenKey = "qiniu_token";
        Configuration cfg = new Configuration();
        UploadManager uploadManager = new UploadManager(cfg);
        //获取七牛云认证token
        String token = stringRedisTemplate.opsForValue().get(qiNiuTokenKey);
        if (token == null) {
            //token过期，重新生成token并存入redis，有效期1小时
            //获取七牛云配置
            String secretKey = stringRedisTemplate.opsForValue().get("qiniu_secretKey");
            String accessKey = stringRedisTemplate.opsForValue().get("qiniu_accessKey");
            String bucketName = stringRedisTemplate.opsForValue().get("qiniu_bucketName");
            Auth auth = Auth.create(accessKey, secretKey);
            //生成一个1小时5秒的token
            token = auth.uploadToken(bucketName, null, 3600 + 5, null);
            stringRedisTemplate.opsForValue().set(qiNiuTokenKey, token, 3600, TimeUnit.SECONDS);
        }

        try {
            //上传
            Response response = uploadManager.put(file.getInputStream(), fileName, token, null, file.getContentType());
            AssertUtil.isTrue(!response.isOK(), "上传文件失败");
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new MallException("上传文件失败");
        }
        return DomainUrlUtil.QIUNIUYUN_IMAGE_RESOURCES + "/@" + fileName;
    }
}
