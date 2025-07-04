package com.emakas.userService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emakas.userService.model.Token;

import java.util.UUID;

@Repository
public interface UserTokenRepository extends JpaRepository<Token, UUID> {
}
