package com.emakas.userService.controller;

import com.emakas.userService.dto.UserReadDto;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.mappers.UserDtoMapper;
import com.emakas.userService.model.User;
import com.emakas.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RequestMapping("/profile")
    public UserReadDto profile() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = userService.getByUserName(securityContext.getAuthentication().getName());
        UserReadDto userReadDto = UserDtoMapper.getInstance().userToUserReadDto(user);

        return userReadDto;
    }

    @PostMapping
    @RequestMapping("/profile/save")
    public UserReadDto save(@RequestBody UserWriteDto userWriteDto) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDtoMapper userDtoMapper = UserDtoMapper.getInstance();
        User user = userService.getByUserName(securityContext.getAuthentication().getName());
        userDtoMapper.updateUserFromUserWriteDto(userWriteDto, user);
        userService.save(user);
        return userDtoMapper.userToUserReadDto(user);
    }
}
