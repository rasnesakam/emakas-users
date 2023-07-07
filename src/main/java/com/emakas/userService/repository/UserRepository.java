package com.emakas.userService.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.emakas.userService.model.User;

import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {
	@Query("SELECT u FROM User u WHERE u.uname = ?1")
	User getByUserName(String user);
}
