package com.home.dictionary.config;

import com.home.dictionary.model.user.AuthorityType;
import com.home.dictionary.repository.ApiUserRepository;
import com.home.dictionary.security.JwtAuthenticationFilter;
import com.home.dictionary.security.WebSecurityCorsFilter;
import com.home.dictionary.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j

//@EnableWebSecurity(debug = true)
@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

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
     * permitAll() - ?????????????????? ??????
     * ???????? ???? ???????? ???? ?????????????? ?????????????????????? ???????????? ???????????? ?? ?????? ???????? ???????? ?????????? ???????????????????? ???????????????? ???????????????????? ?? ????????????????????????,
     * ???? ?????????? ?????????? ?????????????????????? ?????????????????????????? ?????????????????? permitAll() ?? ?????????????????????? ???????????? ?? ?????????????? 403
     *
     * permitAll() ????????????????????, ?????? ?????????? authenticated user, ???????????? ?????? ?????????? ?? ?????????????????? ???????????????????
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/ping").anonymous()
                .antMatchers(HttpMethod.OPTIONS, "/ping").anonymous()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/api/v1/admin/**").anonymous()
                .antMatchers("/api/v1/admin/**").hasAuthority(AuthorityType.ADMIN.name())
                .antMatchers( HttpMethod.OPTIONS, "/api/v1/**").anonymous()
                .antMatchers( "/api/v1/**").hasAuthority(AuthorityType.USER.name())
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // because spring does not provide cors headers on error response! WTF???
        http.addFilterBefore(new WebSecurityCorsFilter(), ChannelProcessingFilter.class);

        // spring ?????????????? ???????????? ?????? ???????????????????????????? ??????????????????????????, ???????????????? HttpOnly=true Secured=false cookie
        // ?????????? ?????????? ???????????????????????? ?? ?????????? ?????????? ?????????? ?????????????????? ?????? ??????????????????????
        // ?????????????? ????????????????
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

}
