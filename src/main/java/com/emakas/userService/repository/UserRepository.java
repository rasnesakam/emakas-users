package com.emakas.userService.repository;

import org.springframework.stereotype.Repository;

import com.emakas.userService.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CoreRepository<User, UUID> {

	Optional<User> getByUserName(String user);
	Optional<User> findById(UUID id);
	boolean existsUserByEmailOrUserName(String email, String userName);

}
