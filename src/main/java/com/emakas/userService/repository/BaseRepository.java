package com.emakas.userService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emakas.userService.model.BaseEntity;

import java.io.Serializable;

public interface BaseRepository<ENTITY extends BaseEntity, ID extends Serializable> extends JpaRepository<ENTITY, ID> {
}
