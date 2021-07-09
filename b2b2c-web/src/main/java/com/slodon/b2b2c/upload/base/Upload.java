package com.slodon.b2b2c.upload.base;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传基类
 */
public abstract class Upload {

    /**
     * redis读取工具
     */
    protected StringRedisTemplate stringRedisTemplate;

    public Upload(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 上传文件
     *
     * @param file     要上传的文件
     * @param fileName 文件名，必须以'/'开头，如 /images/seller/goods/0Kwf1FTdCzZo5J7Siph.png
     * @return 文件绝对路径
     */
    public abstract String upload(MultipartFile file, String fileName);

}
