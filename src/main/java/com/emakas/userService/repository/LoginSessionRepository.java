package com.emakas.userService.repository;

import com.emakas.userService.model.LoginSession;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginSessionRepository extends CoreRepository<LoginSession, UUID> {
}
