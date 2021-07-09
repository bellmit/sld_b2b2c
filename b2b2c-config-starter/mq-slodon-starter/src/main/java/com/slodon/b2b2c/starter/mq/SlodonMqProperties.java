package com.slodon.b2b2c.starter.mq;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.StarterConfigConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义配置
 * 部分字段设置有默认值，也可以自定义配置
 */
@ConfigurationProperties(prefix = StarterConfigConst.MQ_PREFIX)
@Data
public class SlodonMqProperties {
    /**
     * mq host
     */
    private String host = DomainUrlUtil.SLD_MQ_HOST;

    /**
     * mq 端口号
     */
    private Integer port = DomainUrlUtil.SLD_MQ_PORT;
}
