package com.emakas.userService.repository;

import org.springframework.stereotype.Repository;

import com.emakas.userService.model.User;

import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {
}
