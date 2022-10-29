package com.emakas.userService.repository;

import com.emakas.userService.entity.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface EntityRepository<E extends Entity, ID> extends JpaRepository<E, ID> {
}
