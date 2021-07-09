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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 数据库工厂bean配置
 */
@Configuration
//数据源对象dao所在包-write
@EnableConfigurationProperties(WriteDataSourceProperties.class)
@MapperScan(basePackages = StarterConfigConst.DATASOURCE_MAPPER_SCAN_BASE_PACKAGE_WRITE,sqlSessionFactoryRef = "sqlSessionFactoryWrite")
public class WriteDataSourceAutoConfiguration {

    @Autowired
    private WriteDataSourceProperties writeDataSourceProperties;

    /*-------------多数据源配置开始，配合properties文件配置数据源------------------*/

    /**
     * write数据源，默认使用此数据源 {@link Primary}
     * @return
     */
    @Bean(value = "dataSourceWrite")
    @Primary
    public DataSource dataSource0() {
        return DataSourceBuilder.create()
                .driverClassName(writeDataSourceProperties.getDriverClassName())
                .url(writeDataSourceProperties.getUrl())
                .username(writeDataSourceProperties.getUsername())
                .password(writeDataSourceProperties.getPassword())
                .build();
    }

    @Bean(value = "sqlSessionFactoryWrite")
    @Primary
    public SqlSessionFactory sqlSessionFactory0(@Qualifier("dataSourceWrite") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(StarterConfigConst.DATASOURCE_MAPPER_XML_LOCATIONS_WRITE));
        bean.setTypeAliasesPackage(StarterConfigConst.DATASOURCE_TYPE_ALIASES_PACKAGE_WRITE);
        return bean.getObject();
    }
}
