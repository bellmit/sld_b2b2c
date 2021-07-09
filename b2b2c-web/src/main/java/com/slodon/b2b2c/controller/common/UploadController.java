package com.slodon.b2b2c.controller.common;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.UploadConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.ShortUUID;
import com.slodon.b2b2c.enums.UploadSourceEnum;
import com.slodon.b2b2c.upload.ALiYunUpload;
import com.slodon.b2b2c.upload.MinioUpload;
import com.slodon.b2b2c.upload.QiNiuYunUpload;
import com.slodon.b2b2c.upload.TencentCloudUpload;
import com.slodon.b2b2c.upload.base.Upload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 文件上传接口
 */
@Api(tags = "通用上传文件")
@RestController
@RequestMapping("v3/oss/common")
public class UploadController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件", paramType = "formData", required = true),
            @ApiImplicitParam(name = "source",
                    value = "资源类型：sellerBrand==商家申请品牌;friendPartner==合作伙伴图片;headImg==头像;" +
                            "goods==商品图片;video==商品视频;appeal==申诉图片;complain==投诉图片;" +
                            "setting==系统设置图片;sellerDeco==商户装修图片;adminBrand==品牌图片;" +
                            "sellerApply==入驻图片;logo==商家logo;adminDeco==装修图片;afterSale==售后申请图片;" +
                            "evaluate==评价图片", paramType = "formData", required = true)
    })
    @PostMapping(value = "upload", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public JsonResult uploadImg(MultipartFile file, String source) throws Exception {
        if (StringUtils.isEmpty(source)) {
            return SldResponse.badArgumentValue();
        }
        UploadSourceEnum uploadSourceEnum = UploadSourceEnum.sourceMap.get(source);
        String path = uploadSourceEnum.getPath();//图片存储中间路径
        String bucketName = uploadSourceEnum.getBucketName();//图片存储桶名称（前缀路径）
        //校验文件有效性,获取文件扩展名
        String extend = this.checkFile(file, bucketName);

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
        //生成完整的文件存储路径
        String fileName = "/" + bucketName + path + ShortUUID.generate() + extend;
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("path", fileName);//相对地址

        //根据系统配置的上传方式，获取上传对象
        Upload upload;
        switch (DomainUrlUtil.UPLOAD_IMAGE_TYPE) {
            case 1:
                //minio上传
                upload = new MinioUpload(stringRedisTemplate);
                break;
            case 2:
                //七牛云上传
                upload = new QiNiuYunUpload(stringRedisTemplate);
                break;
            case 3:
                //腾讯云上传文件
                upload = new TencentCloudUpload(stringRedisTemplate);
                break;
            case 4:
                //阿里云上传图片
                upload = new ALiYunUpload(stringRedisTemplate);
                break;
            default:
                throw new MallException("上传文件失败");
        }

        //上传文件
        resultMap.put("url", upload.upload(file, fileName));//绝对地址

        if (bucketName.equals(UploadConst.BUCKET_NAME_IMAGE)) {
            //图片获取宽高
            BufferedImage image = ImageIO.read(file.getInputStream());
            resultMap.put("width", image.getWidth());
            resultMap.put("height", image.getHeight());
        }
        return SldResponse.success(resultMap);
    }

    /**
     * 校验文件有效性
     *
     * @param file       文件
     * @param bucketName 文件类型,{@link UploadConst#BUCKET_NAME_IMAGE}
     * @return 文件扩展名，如 .jpg
     * @throws IOException
     */
    private String checkFile(MultipartFile file, String bucketName) throws IOException {
        if (file == null) {
            throw new MallException("请选择要上传的文件");
        }
        if (StringUtils.isEmpty(file.getOriginalFilename()) || !file.getOriginalFilename().contains(".")) {
            throw new MallException("文件格式有误");
        }
        //处理文件流
        //扩展名，如 .jpg
        String extend = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."))
                .toLowerCase();

        //根据桶名判断对文件类型判断
        switch (bucketName) {
            case UploadConst.BUCKET_NAME_IMAGE:
                //图片
                if (!isImg(extend, file.getInputStream())) {
                    throw new MallException("图片格式有误");
                }
                break;
            case UploadConst.BUCKET_NAME_VIDEO:
                //视频
                if (!isVideo(extend)) {
                    throw new MallException("视频格式有误");
                }
                break;
            case UploadConst.BUCKET_NAME_FILE:
                //doc文件
                if (!isDoc(extend)) {
                    throw new MallException("文档格式有误");
                }
                break;
            default:
                throw new MallException("文件类型有误");
        }

        //文件大小限制
        if (file.isEmpty() || file.getSize() == 0L) {
            throw new MallException("文件无效");
        }
        if (file.getSize() > 20 * 1024 * 1024L) {
            //最大20M
            throw new MallException("上传文件不能超过20M");
        }
        return extend;
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
     * 是否是视频
     *
     * @param extend
     * @return
     */
    private static boolean isVideo(String extend) {
        if (StringUtils.isEmpty(extend)) {
            return false;
        }
        List<String> list = new ArrayList<>();
        list.add(".avi");
        list.add(".mov");
        list.add(".rmvb");
        list.add(".rm");
        list.add(".flv");
        list.add(".mp4");
        list.add(".3gp");
        return list.contains(extend.toLowerCase());
    }

    /**
     * 是否是doc文档
     *
     * @param extend
     * @return
     */
    private static boolean isDoc(String extend) {
        if (StringUtils.isEmpty(extend)) {
            return false;
        }
        List<String> list = new ArrayList<>();
        list.add(".doc");
        list.add(".docx");
        return list.contains(extend.toLowerCase());
    }
}
