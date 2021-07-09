package com.slodon.b2b2c.core.util;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import org.springframework.util.StringUtils;

/**
 * 图片绝对地址工具
 */
public class FileUrlUtil {

    /**
     * 根据图片相对路径，获取绝对路径
     *
     * @param filePath
     * @param imageSize
     * @return
     */
    public static String getFileUrl(String filePath, ImageSizeEnum imageSize) {
        if (StringUtils.isEmpty(filePath)) {
            //相对地址为空
            return null;
        }
        if (imageSize == null || StringUtils.isEmpty(imageSize.getHeight())) {
            //默认大图
            return DomainUrlUtil.SLD_IMAGE_RESOURCES + filePath;
        }
        //裁剪图片
        return DomainUrlUtil.SLD_IMAGE_RESOURCES + "/unsafe/"
                + imageSize.getHeight() + "x" + imageSize.getWidth() + "/"
                + DomainUrlUtil.SLD_IMAGE_RESOURCES + filePath;
    }
}
