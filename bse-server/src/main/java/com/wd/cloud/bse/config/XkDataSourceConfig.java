package com.wd.cloud.bse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "xkEntityManagerFactory",
        transactionManagerRef = "xkTransactionManager",
        basePackages = {"com.wd.cloud.bse.repository.xk"}) //设置Repository所在位置
public class XkDataSourceConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    @Qualifier("xkDataSource")
    private DataSource xkDataSource;


    /**
     * 我们通过LocalContainerEntityManagerFactoryBean来获取EntityManagerFactory实例
     *
     * @return
     */
    @Bean(name = "xkEntityManagerFactoryBean")
    //@Primary
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(xkDataSource)
                .properties(getVendorProperties(xkDataSource))
                .packages("com.wd.cloud.bse.entity.xk") //设置实体类所在位置
                .persistenceUnit("xkPersistenceUnit")
                .build();
        //.getObject();//不要在这里直接获取EntityManagerFactory
    }

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getProperties();
    }

    /**
     * EntityManagerFactory类似于Hibernate的SessionFactory,mybatis的SqlSessionFactory
     * 总之,在执行操作之前,我们总要获取一个EntityManager,这就类似于Hibernate的Session,
     * mybatis的sqlSession.
     *
     * @param builder
     * @return
     */
    @Bean(name = "xkEntityManagerFactory")
    @Primary
    public EntityManagerFactory userEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return this.userEntityManagerFactoryBean(builder).getObject();
    }

    /**
     * 配置事物管理器
     *
     * @return
     */
    @Bean(name = "xkTransactionManager")
    @Primary
    public PlatformTransactionManager writeTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(userEntityManagerFactory(builder));
    }

}
