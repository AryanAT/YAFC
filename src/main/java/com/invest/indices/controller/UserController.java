package com.invest.indices.controller;

import com.invest.indices.domain.model.Users;
import com.invest.indices.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @PostMapping("/signin")
    public String signIn(@RequestBody Users user) {
        return userService.verify(user);
    }

    @PostMapping("/signup")
    public Users signUp(@RequestBody Users user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userService.register(user);
    }

    @GetMapping("/allUsers")
    public List<Users> signUp() {
        return userService.getAllUsers();
    }
}
