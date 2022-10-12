package org.lukos.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Websecurity Configuration
 * <p>
 * This configuration ensures that all requests are authenticated if desired, by offering a means to login through
 * redirects with Google authentication.
 *
 * @author Marco Pleket (1295713)
 * @since 26-02-22
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * Overrides the configure function in WebSecurityConfigurerAdapter to enable personalized authentication.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(a -> a.antMatchers("/api/**").authenticated() // all API requests should be authenticated
                        .antMatchers("/**").permitAll() // all other resources are freely accessible
                ).logout(l -> l.logoutUrl("/api/logout") // custom logout endpoint
                ).oauth2Login().defaultSuccessUrl("/", true).and().csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
}
