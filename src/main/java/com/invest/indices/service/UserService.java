package com.invest.indices.service;

import com.invest.indices.domain.model.Users;

import java.util.List;

public interface UserService {
    Users register(Users user);

    String verify(Users user);

    List<Users> getAllUsers();
}
