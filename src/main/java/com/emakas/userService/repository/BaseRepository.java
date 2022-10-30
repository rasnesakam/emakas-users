package com.emakas.userService.repository;

import com.emakas.userService.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface BaseRepository<ENTITY extends BaseEntity, ID extends Serializable> extends JpaRepository<ENTITY, ID> {
}
