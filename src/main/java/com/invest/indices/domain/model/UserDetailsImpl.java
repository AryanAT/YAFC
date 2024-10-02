package com.invest.indices.domain.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final Users users;

    public UserDetailsImpl(Users users) {
        this.users = users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("Users"));
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Always true just to save time
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Always true just to save time
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Always true just to save time
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Always true just to save time
        return true;
    }
}
