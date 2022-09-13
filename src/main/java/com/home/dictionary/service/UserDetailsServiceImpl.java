package com.home.dictionary.service;

import com.home.dictionary.model.user.Authority;
import com.home.dictionary.model.user.AuthorityType;
import com.home.dictionary.repository.ApiUserRepository;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ApiUserRepository apiUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var apiUser = apiUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with name " + username));

        return new org.springframework.security.core.userdetails.User(
                apiUser.getUsername(),
                apiUser.getPassword(),
                apiUser.isEnabled(),
                true,
                true,
                true,
                StreamEx.of(apiUser.getAuthorities())
                        .map(Authority::getType)
                        .map(this::toGrantedAuthority)
                        .toList()
        );
    }

    private SimpleGrantedAuthority toGrantedAuthority(AuthorityType type) {
        return new SimpleGrantedAuthority(type.name());
    }

}
