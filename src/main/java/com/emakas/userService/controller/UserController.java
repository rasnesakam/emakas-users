package com.emakas.userService.controller;

import com.emakas.userService.dto.UserReadDto;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.mappers.UserDtoMapper;
import com.emakas.userService.model.User;
import com.emakas.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RequestMapping("/profile")
    public ResponseEntity<UserReadDto> profile() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Optional<User> user = userService.getByUserName(securityContext.getAuthentication().getName());
        if (user.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        UserReadDto userReadDto = UserDtoMapper.getInstance().userToUserReadDto(user.get());

        return new ResponseEntity<>(userReadDto, HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/profile/save")
    public ResponseEntity<UserReadDto> save(@RequestBody UserWriteDto userWriteDto) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDtoMapper userDtoMapper = UserDtoMapper.getInstance();
        Optional<User> user = userService.getByUserName(securityContext.getAuthentication().getName());
        if (user.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        userDtoMapper.updateUserFromUserWriteDto(userWriteDto, user.get());
        userService.save(user.get());
        return new ResponseEntity<>(userDtoMapper.userToUserReadDto(user.get()), HttpStatus.OK);
    }
}
