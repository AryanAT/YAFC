package com.invest.indices.service.impl;

import com.invest.indices.domain.model.Users;
import com.invest.indices.infra.repository.UserDetailsRepository;
import com.invest.indices.service.JWTservice;
import com.invest.indices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;


    @Autowired
    private JWTservice jwTservice;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Users register(Users user) {
        return userDetailsRepository.save(user);
    }

    @Override
    public String verify(Users user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwTservice.generateToken(user.getUsername());
        }
        return "Bad Credentials";
    }

    @Override
    public List<Users> getAllUsers() {
        return userDetailsRepository.findAll();
    }
}
