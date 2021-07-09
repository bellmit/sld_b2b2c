package com.slodon.b2b2c.starter.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.*;

/**
 * mq配置
 * 连接工厂配置
 * 监听器行为配置
 * 交换机、队列、路由初始化
 * <p>
 * 增加队列方法：
 * 1.在{@link com.slodon.b2b2c.core.constant.StarterConfigConst}中增加队列名称常量
 * 2.在此方法中增加队列名称、交换机-队列绑定路由的bean
 * 3.在相应模块增加队列监听器
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SlodonMqProperties.class)
public class SlodonMqAutoConfig {

    private CachingConnectionFactory connectionFactory;

    /**
     * 连接工厂使用自定义配置
     *
     * @param connectionFactory
     * @param slodonMqProperties
     */
    public SlodonMqAutoConfig(CachingConnectionFactory connectionFactory,
                              SlodonMqProperties slodonMqProperties) {
        connectionFactory.setHost(slodonMqProperties.getHost());
        connectionFactory.setPort(slodonMqProperties.getPort());
        connectionFactory.afterPropertiesSet();
        connectionFactory.setAddresses(slodonMqProperties.getHost() + ":" + slodonMqProperties.getPort());
        connectionFactory.setRequestedHeartBeat(0);
        this.connectionFactory = connectionFactory;
    }

    //region 交换机配置

    /**
     * 交换机
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(MQ_EXCHANGE_NAME, true, false);
    }
    //endregion

    //region 队列配置

    /**
     * 会员消息队列
     *
     * @return
     */
    @Bean
    public Queue memberMsgQueue() {
        return new Queue(MQ_QUEUE_NAME_MEMBER_MSG, true);
    }

    /**
     * 商户消息队列
     *
     * @return
     */
    @Bean
    public Queue sellerMsgQueue() {
        return new Queue(MQ_QUEUE_NAME_SELLER_MSG, true);
    }

    /**
     * admin操作日志队列
     *
     * @return
     */
    @Bean
    public Queue adminLogQueue() {
        return new Queue(MQ_QUEUE_NAME_ADMINLOG_MSG, true);
    }

    /**
     * vendor操作日志队列
     *
     * @return
     */
    @Bean
    public Queue vendorLogQueue() {
        return new Queue(MQ_QUEUE_NAME_SELLERLOG_MSG, true);
    }

    /**
     * vendor操作日志队列
     *
     * @return
     */
    @Bean
    public Queue orderSubmitQueue() {
        return new Queue(MQ_QUEUE_NAME_ORDER_SUBMIT, true);
    }

    /**
     * 会员积分队列
     *
     * @return
     */
    @Bean
    public Queue memberIntegralQueue() {
        return new Queue(MQ_QUEUE_NAME_MEMBER_INTEGRAL, true);
    }

    //endregion

    //region 路由配置

    /**
     * 会员消息队列路由
     *
     * @return
     */
    @Bean
    public Binding memberMsgBinding() {
        return BindingBuilder.bind(memberMsgQueue()).to(directExchange()).with(MQ_QUEUE_NAME_MEMBER_MSG);
    }

    /**
     * 商户消息队列路由
     *
     * @return
     */
    @Bean
    public Binding sellerMsgBinding() {
        return BindingBuilder.bind(sellerMsgQueue()).to(directExchange()).with(MQ_QUEUE_NAME_SELLER_MSG);
    }

    /**
     * admin操作日志队列路由
     *
     * @return
     */
    @Bean
    public Binding adminLogBinding() {
        return BindingBuilder.bind(adminLogQueue()).to(directExchange()).with(MQ_QUEUE_NAME_ADMINLOG_MSG);
    }

    /**
     * vendor操作日志队列路由
     *
     * @return
     */
    @Bean
    public Binding vendorLogBinding() {
        return BindingBuilder.bind(vendorLogQueue()).to(directExchange()).with(MQ_QUEUE_NAME_SELLERLOG_MSG);
    }

    /**
     * vendor操作日志队列路由
     *
     * @return
     */
    @Bean
    public Binding orderSubmitBinding() {
        return BindingBuilder.bind(orderSubmitQueue()).to(directExchange()).with(MQ_QUEUE_NAME_ORDER_SUBMIT);
    }

    /**
     * 会员积分队列路由
     *
     * @return
     */
    @Bean
    public Binding memberIntegralBinding() {
        return BindingBuilder.bind(memberIntegralQueue()).to(directExchange()).with(MQ_QUEUE_NAME_MEMBER_INTEGRAL);
    }

    //endregion


    //region 连接工厂配置

    /**
     * 连接工厂，单一消费者，发生异常丢弃消息
     *
     * @return
     */
    @Bean(MQ_FACTORY_NAME_SINGLE_PASS_ERR)
    public SimpleRabbitListenerContainerFactory slodonMqFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setBatchSize(1);
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);//跳过异常
        return factory;
    }
    //endregion

}
