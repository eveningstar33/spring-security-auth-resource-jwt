package com.dgs.springsecurityauthserverjwt.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	@Qualifier("secondaryDataSource")
	private DataSource dataSource;
	
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}
	
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey("12345");
		return converter;
	}

	// In Oauth we need to define the clients and the clients represents the apps that will be able
	// to benefit of the Authorization Server that we develop here
	
//	@Override
//	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		clients.inMemory()
//				.withClient("clientId")
//				.secret("$2a$10$ed0s1YSHr3v0wkxqQuoeZutC.kAHlCSm/T1DA0bqgqAZRzLKRIuVK")
//				.authorizedGrantTypes("password", "authorization_token", "refresh_token")
//				.scopes("write");
//	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager)
				 .tokenStore(tokenStore())
				 .accessTokenConverter(jwtAccessTokenConverter());
	}

}
