package com.home.dictionary.security;

import com.home.dictionary.config.SecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {

        var uri = httpServletRequest.getRequestURI();
        var method = httpServletRequest.getMethod();
        if (uri.startsWith(SecurityConfig.AUTH_ROOT_URL) || HttpMethod.GET.name().equals(method)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        String auth = httpServletRequest.getHeader("Authorization");
        if (auth == null || auth.isBlank()) {
            httpServletResponse.sendError(403, "forbidden");
            return;
        }

        try {
            if (auth.startsWith("Bearer ")) {
                String jwt = auth.substring(7);
                if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                    String username = jwtProvider.getUsernameFromJwt(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (RuntimeException e) {
            log.error("Error while validating token", e);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
