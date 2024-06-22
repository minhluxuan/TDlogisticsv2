package project.tdlogistics.tasks.configurations;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

//Java program for spring datasource configuration
@Configuration
@ComponentScan("project.tdlogistics.tasks")
public class SpringJdbcConfig {
	@Bean public DataSource mysqlDataSource()
	{
		DriverManagerDataSource dataSource
			= new DriverManagerDataSource();
		dataSource.setDriverClassName(
			"com.mysql.jdbc.Driver");
		dataSource.setUrl(
			"jdbc:mysql://localhost:3306/tdlogistics_task?useSSL=false");
		dataSource.setUsername("root");
		dataSource.setPassword("new_password");

		return dataSource;
	}
}