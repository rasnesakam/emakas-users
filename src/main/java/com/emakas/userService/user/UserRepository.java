package com.emakas.userService.user;

import com.emakas.userService.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {
}
