package com.invest.indices.service.impl;

import com.invest.indices.domain.model.UserDetailsImpl;
import com.invest.indices.domain.model.Users;
import com.invest.indices.infra.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users users = userDetailsRepository.findByUsername(userName);

        if (users == null) {
            System.out.println("User not found by username");
            throw new UsernameNotFoundException("User not found by username");
        }
        return new UserDetailsImpl(users);
    }
}
