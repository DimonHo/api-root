package com.wd.cloud.bse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "schoolEntityManagerFactory",
        transactionManagerRef = "schoolTransactionManager",
        basePackages = {"com.wd.cloud.bse.repository.school"}) //设置Repository所在位置
public class SchoolDataSourceConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    @Qualifier("schoolDataSource")
    private DataSource schoolDataSource;


    /**
     * 我们通过LocalContainerEntityManagerFactoryBean来获取EntityManagerFactory实例
     *
     * @return
     */
    @Bean(name = "schoolEntityManagerFactoryBean")
    //@Primary
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(schoolDataSource)
                .properties(getVendorProperties(schoolDataSource))
                .packages("com.wd.cloud.bse.entity.school") //设置实体类所在位置
                .persistenceUnit("schoolPersistenceUnit")
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
    @Bean(name = "schoolEntityManagerFactory")
    public EntityManagerFactory userEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return this.userEntityManagerFactoryBean(builder).getObject();
    }

    /**
     * 配置事物管理器
     *
     * @return
     */
    @Bean(name = "schoolTransactionManager")
    public PlatformTransactionManager writeTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(userEntityManagerFactory(builder));
    }

}
