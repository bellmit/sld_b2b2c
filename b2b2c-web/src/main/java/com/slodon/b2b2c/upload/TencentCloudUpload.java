package com.slodon.b2b2c.upload;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.upload.base.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 腾讯云上传文件
 */
@Slf4j
public class TencentCloudUpload extends Upload {

    public TencentCloudUpload(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    /**
     * 上传文件
     *
     * @param file     要上传的文件
     * @param fileName 文件名，必须以'/'开头，如 /images/seller/goods/0Kwf1FTdCzZo5J7Siph.png
     * @return 文件绝对路径
     */
    @Override
    public String upload(MultipartFile file, String fileName) {
        //配置信息
        String secretId = stringRedisTemplate.opsForValue().get("tencent_secretId");
        String secretKey = stringRedisTemplate.opsForValue().get("tencent_secretKey");
        String apCity = stringRedisTemplate.opsForValue().get("tencent_apCity");
        String bucketName = stringRedisTemplate.opsForValue().get("tencent_bucketName");

        // 1.初始化用户身份信息(secretId,secretKey)
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域
        Region region = new Region(apCity);
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);

        //上传
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        try {
            cosClient.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            log.error("上传文件失败",e);
            throw new MallException("上传文件失败");
        }finally {
            //关闭连接
            cosClient.shutdown();
        }

        // 返回上传之后的oss存储路径
        return DomainUrlUtil.TENCENTCLOUD_IMAGE_RESOURCES + fileName;
    }
}
