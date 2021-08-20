package com.slodon.b2b2c.starter.datasource;

import com.slodon.b2b2c.core.constant.StarterConfigConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义配置
 * 部分字段设置有默认值，也可以自定义配置
 * 各个微服务引用此starter时，url字段必须配置
 */
@ConfigurationProperties(prefix = StarterConfigConst.DATASOURCE_PREFIX_WRITE)
@Data
public class WriteDataSourceProperties {
    /**
     * jdbc驱动
     */
    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    /**
     * 数据库连接url
     */
    private String url;

    /**
     * 数据库连接用户名
     */
    private String username = "root";

    /**
     * 数据库连接密码
     */
    private String password = "110";
}
