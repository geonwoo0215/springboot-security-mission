package org.prgrms.devcourse.springbootsecuritymission.configures;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {



		auth.inMemoryAuthentication()
			.withUser("user")
			.password("{noop}user123")
			.roles("USER")
			.and()
			.withUser("admin")
			.password("{noop}admin123")
			.roles("ADMIN");

	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/assets/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/me").hasAnyRole("USER", "ADMIN")
			.antMatchers("/admin").access("hasRole('ADMIN') and isFullyAuthenticated()")
			.anyRequest().permitAll()

			.and()

			.formLogin()
			.defaultSuccessUrl("/")
			.permitAll()

			.and()

			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.invalidateHttpSession(true)
			.clearAuthentication(true)

			.and()

			.rememberMe()
			.rememberMeParameter("remember-me")
			.tokenValiditySeconds(300)

			.and()

			.requiresChannel()
			.antMatchers().requiresSecure()

			.and()

			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler())

		;

	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, e) -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication != null ? authentication.getPrincipal() : null;
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("text/plain");
			response.getWriter().write("ACCESS DENIED ##");
			response.getWriter().flush();
			response.getWriter().close();

		};

	}


}
