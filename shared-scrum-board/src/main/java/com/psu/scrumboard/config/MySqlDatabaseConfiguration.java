package com.psu.scrumboard.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.psu.scrumboard.data.repository")
@Profile(DBType.MYSQL_DRIVER)
public class MySqlDatabaseConfiguration {

	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create()
				.url(String.format(
						"jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true",
						"localhost", "3306", "scrumtestdb"))
				.driverClassName("com.mysql.cj.jdbc.Driver").username("scrum").password("ScrumBoard123").build();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
		return transactionManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("com.psu.scrumboard.data.table");

		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	private final Properties additionalProperties() {
		final Properties p = new Properties();
		p.setProperty("hibernate.hbm2ddl.auto", "update"); // update / create-drop
		p.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		p.put("spring.jpa.show_sql", "true");
		p.put("spring.jpa.open-in-view", "false");
		p.put("hibernate.generate_statistics", "true");
		return p;
	}

}
