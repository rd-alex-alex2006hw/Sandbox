package demo;

import java.security.KeyPair;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import st.malike.auth.server.service.security.UserAuthProviderService;

@SpringBootApplication
@Controller
@SessionAttributes("authorizationRequest")
@EnableResourceServer
@EnableMongoRepositories("st.malike.auth.server.repository")
@ComponentScan(basePackages = {"st.malike.auth.server.service.security","org.springframework.security.oauth2.client"})
@EnableAuthorizationServer
public class AuthserverApplication extends WebMvcConfigurerAdapter {

	@RequestMapping("/user")
	@ResponseBody
	public Principal user(Principal user) {
		return user;
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/oauth/confirm_access").setViewName("authorize");
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthserverApplication.class, args);
	}

	@Configuration
	@Order(-20)
	protected static class LoginConfig extends WebSecurityConfigurerAdapter {

		@Autowired
                private UserAuthProviderService authProvider;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
				.formLogin().loginPage("/login").permitAll()
			.and()
				.requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access")
			.and()
				.authorizeRequests().anyRequest().authenticated();
			// @formatter:on
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(authProvider);
		}
	}

//	@Configuration
//	@EnableAuthorizationServer
//	protected static class OAuth2AuthorizationConfig extends
//			AuthorizationServerConfigurerAdapter {
////
////		@Autowired
////		private AuthenticationManager authenticationManager;
//
////		@Bean
////		public JwtAccessTokenConverter jwtAccessTokenConverter() {
////			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
////			KeyPair keyPair = new KeyStoreKeyFactory(
////					new ClassPathResource("keystore.jks"), "foobar".toCharArray())
////					.getKeyPair("test");
////			converter.setKeyPair(keyPair);
////			return converter;
////		}
//
//		@Override
//		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//			clients.inMemory()
//					.withClient("acme")
//					.secret("acmesecret")
//					.authorizedGrantTypes("authorization_code", "refresh_token",
//							"password").scopes("openid");
//		}
////
////		@Override
////		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
////				throws Exception {
////			endpoints.authenticationManager(authenticationManager).accessTokenConverter(
////					jwtAccessTokenConverter());
////		}
////
////		@Override
////		public void configure(AuthorizationServerSecurityConfigurer oauthServer)
////				throws Exception {
////			oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess(
////					"isAuthenticated()");
////		}
//
//	}
}
