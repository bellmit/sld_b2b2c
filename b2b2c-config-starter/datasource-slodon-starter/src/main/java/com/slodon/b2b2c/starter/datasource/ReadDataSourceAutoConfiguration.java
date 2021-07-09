package com.slodon.b2b2c.starter.datasource;

import com.slodon.b2b2c.core.constant.StarterConfigConst;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 数据库工厂bean配置
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ReadDataSourceProperties.class)
//数据源对象dao所在包-read
@MapperScan(basePackages = StarterConfigConst.DATASOURCE_MAPPER_SCAN_BASE_PACKAGE_READ,sqlSessionFactoryRef = "sqlSessionFactoryRead")
public class ReadDataSourceAutoConfiguration {

    @Autowired
    private ReadDataSourceProperties readDataSourceProperties;

    /*-------------多数据源配置开始，配合properties文件配置数据源------------------*/

    /**
     * read数据源
     * @return
     */
    @Bean(value = "dataSourceRead")
    public DataSource dataSource0() {
        return DataSourceBuilder.create()
                .driverClassName(readDataSourceProperties.getDriverClassName())
                .url(readDataSourceProperties.getUrl())
                .username(readDataSourceProperties.getUsername())
                .password(readDataSourceProperties.getPassword())
                .build();
    }

    @Bean(value = "sqlSessionFactoryRead")
    public SqlSessionFactory sqlSessionFactory0(@Qualifier("dataSourceRead") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(StarterConfigConst.DATASOURCE_MAPPER_XML_LOCATIONS_READ));
        bean.setTypeAliasesPackage(StarterConfigConst.DATASOURCE_TYPE_ALIASES_PACKAGE_READ);
        return bean.getObject();
    }
}
