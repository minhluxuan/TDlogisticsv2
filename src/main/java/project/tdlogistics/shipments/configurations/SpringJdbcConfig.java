package project.tdlogistics.shipments.configurations;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

//Java program for spring datasource configuration
@Configuration
@ComponentScan("project.tdlogistics.shipments")
public class SpringJdbcConfig {
	@Bean public DataSource mysqlDataSource()
	{
		DriverManagerDataSource dataSource
			= new DriverManagerDataSource();
		dataSource.setDriverClassName(
			"com.mysql.jdbc.Driver");
		dataSource.setUrl(
			"jdbc:mysql://127.0.0.1:3308/tdlogisticsv2?useSSL=false");
		dataSource.setUsername("root");
		dataSource.setPassword("Mysql@3306");

		return dataSource;
	}
}