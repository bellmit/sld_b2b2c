package com.slodon.b2b2c.starter.redisson;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.StarterConfigConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义配置
 * 部分字段设置有默认值，也可以自定义配置
 * 各个微服务引用此starter时，url字段必须配置
 */
@ConfigurationProperties(prefix = StarterConfigConst.REDISSON_PREFIX)
@Data
public class SlodonRedissonProperties {
    /**
     * 使用的redis数据库编号
     */
    private Integer database = 1;

    /**
     * redis host
     */
    private String host = "redis://" + DomainUrlUtil.SLD_REDIS_HOST;

    /**
     * redis密码
     */
    private String password = DomainUrlUtil.SLD_REDIS_PASSWORD;

    /**
     * redis端口号
     */
    private int port = DomainUrlUtil.SLD_REDIS_PORT;

}
