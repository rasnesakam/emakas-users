package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@EqualsAndHashCode
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private LocalDateTime createdTime;

    public BaseEntity(UUID id, LocalDateTime createdTime) {
        this.id = id;
		this.createdTime = createdTime;
	}
    
    public BaseEntity(LocalDateTime createdTime) {
		this(UUID.randomUUID(),createdTime);
	}


	public BaseEntity() {
    	this(LocalDateTime.now());
	}


}
