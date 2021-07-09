package com.slodon.b2b2c.upload;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.upload.base.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 阿里云上传文件
 */
@Slf4j
public class ALiYunUpload extends Upload {

    public ALiYunUpload(StringRedisTemplate stringRedisTemplate) {
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
        // 地域节点
        String endpoint = stringRedisTemplate.opsForValue().get("aliyun_endPoint");
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = stringRedisTemplate.opsForValue().get("aliyun_accessKey");
        String accessKeySecret = stringRedisTemplate.opsForValue().get("aliyun_secretKey");
        // BucketName
        String bucketName = stringRedisTemplate.opsForValue().get("aliyun_bucketName");

        // 创建OSSClient实例。
        OSS ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());//指定文件类型
        try {
            // 上传文件流
            ossClient.putObject(bucketName, fileName.substring(1), file.getInputStream(), metadata);
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new MallException("上传文件失败");
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return DomainUrlUtil.ALIYUN_IMAGE_RESOURCES + fileName;
    }
}
