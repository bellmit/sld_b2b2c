package com.slodon.b2b2c.core.constant;

public class UploadConst {

    /**
     * 文件流类型 ContentType
     */
    public static final String CONTENT_TYPE_IMAGE = "image/jpeg";
    public static final String CONTENT_TYPE_WORD = "application/msword";

    /**
     * minio桶名称
     */
    public static final String BUCKET_NAME_IMAGE = "images";
    public static final String BUCKET_NAME_VIDEO = "videos";
    public static final String BUCKET_NAME_FILE = "files";

    /**
     * 商品csv导入时根据path等生成minio的objectName
     */
    public static final String PATH_NAME_IMAGE = "/seller/goods/";

    /**
     * 商品csv文件分隔符
     */
    public static final char SEPARATOR = ',';

    /**
     * 商品csv文件模板下载地址
     */
    public static final String CSV_TEMPLATE_DOWNLOAD_PATH= "/files/seller/csvFiles/商品csv导入.csv";
}
