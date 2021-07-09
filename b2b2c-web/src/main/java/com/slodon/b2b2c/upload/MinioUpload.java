package com.slodon.b2b2c.upload;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.upload.base.Upload;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.policy.PolicyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * minio上传文件
 */
@Slf4j
public class MinioUpload extends Upload {

    public MinioUpload(StringRedisTemplate stringRedisTemplate) {
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
        //获取配置
        String minioUrl = stringRedisTemplate.opsForValue().get("minio_url");//minio地址
        int port = Integer.parseInt(stringRedisTemplate.opsForValue().get("minio_port"));//minio端口
        String accessKey = stringRedisTemplate.opsForValue().get("minio_access_key");
        String secretKey = stringRedisTemplate.opsForValue().get("minio_secret_key");

        //从fileName中获取bucketName和objectName
        String reg = "/(\\S*?)(/\\S*)";
        String bucketName = fileName.replaceFirst(reg, "$1");
        String objectName = fileName.replaceFirst(reg, "$2");

        //调用api上传文件
        MinioClient minioClient = null;
        try {
            minioClient = new MinioClient(minioUrl + ":" + port, accessKey, secretKey);
            //查询桶是否已存在，不存在则创建桶
            boolean bucketExists = minioClient.bucketExists(bucketName);
            if (!bucketExists) {
                minioClient.makeBucket(bucketName);
                //创建安全级别
                minioClient.setBucketPolicy(bucketName, "*", PolicyType.READ_ONLY);
            }
            //上传文件
            minioClient.putObject(bucketName, objectName, file.getInputStream(), file.getContentType());
        } catch (InvalidEndpointException | InvalidPortException | InvalidObjectPrefixException |
                InvalidKeyException | NoSuchAlgorithmException | NoResponseException |
                XmlPullParserException | InvalidBucketNameException | InvalidArgumentException |
                RegionConflictException | InsufficientDataException | ErrorResponseException |
                InternalException | IOException e) {
            log.error("上传文件失败", e);
            throw new MallException("上传文件失败");
        }
        return DomainUrlUtil.SLD_IMAGE_RESOURCES + fileName;
    }
}
