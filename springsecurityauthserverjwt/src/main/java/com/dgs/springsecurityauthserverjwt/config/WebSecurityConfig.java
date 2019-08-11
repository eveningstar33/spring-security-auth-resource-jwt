package com.dgs.springsecurityauthserverjwt.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
//	@Autowired
//	private DataSource dataSource;
	
	@Bean(name="primaryDataSource")
	@ConfigurationProperties(prefix="spring.datasource")
	public DataSource primaryDataSource() {
	    return DataSourceBuilder.create().build();
	}
	
	@Bean(name="secondaryDataSource")
	@ConfigurationProperties(prefix="spring.second-datasource")
	public DataSource secondaryDataSource() {
	    return DataSourceBuilder.create().build();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	// The Authorization Server manages the users. So it manages clients in order to be able to access
	// the users, but then the main purpose is that the clients are able to access the users that are
	// managed by the Authorization Server. So we really need to have users. And the interface that 
	// defines users in Spring Security is UserDetails. And the interface that represents the manager
	// of users is UserDetailsManager that in the end inherits UserDetailsService. UserDetailsService is
	// actually that contract that represents that small part of the UserDetailsManager that simply only 
	// loads the user and tells us if a user exist and we can get then the password that will be checked
	// by the PasswordEncoder. 

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
//	public UserDetailsService userDetailsService() {
//		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//		
//		UserDetails user = User.withUsername("john")
//							   .password("$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K")
//							   .authorities("ADMIN")
//							   .build();
//		
//		manager.createUser(user);
//		
//		return manager;
//	}
	
//	@Bean
//	public UserDetailsService userDetailsService() {
//		JdbcUserDetailsManager service = new JdbcUserDetailsManager(primaryDataSource());
//		return service;
//	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		JdbcDaoImpl service = new JdbcDaoImpl();
		service.setDataSource(primaryDataSource());
        service.setUsersByUsernameQuery("select username, password , true from user where username = ? ");
        service.setAuthoritiesByUsernameQuery("select user.username , role.name " 
                + " from users_roles "
                + " inner join role on role.id = users_roles.role_id "
                + " inner join user on user.id = users_roles.user_id " 
                + " where user.username = ?");
        
		return service;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}
}
