package com.home.dictionary.config;

import com.home.dictionary.model.user.AuthorityType;
import com.home.dictionary.repository.ApiUserRepository;
import com.home.dictionary.security.JwtAuthenticationFilter;
import com.home.dictionary.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j

//@EnableWebSecurity(debug = true)
@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    public static final String AUTH_ROOT_URL = "/api/v1/auth";
    public static final String AUTH_MATCH_URL = "/api/v1/auth/**";

    @Bean
    public UserDetailsService userDetailsService(ApiUserRepository apiUserRepository) {
        return new UserDetailsServiceImpl(apiUserRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService
    ) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    /**
     * permitAll() - разрешить все
     * Если на один из методов контроллера придет запрос и при этом этот метод попытается получить информацию о пользователе,
     * то такой метод контроллера проигнорирует настройку permitAll() и заблокирует доступ с ошибкой 403
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers(AUTH_MATCH_URL).permitAll()
                .antMatchers("/api/v1/admin/**").hasRole(AuthorityType.ADMIN.name())
                .antMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
