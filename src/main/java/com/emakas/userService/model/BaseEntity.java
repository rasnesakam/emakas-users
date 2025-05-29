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

    @Column
    private LocalDateTime updatedTime;

    public BaseEntity(UUID id, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
		this.createdTime = createdTime;
        this.updatedTime = updatedTime;
	}
    
    public BaseEntity(LocalDateTime createdTime, LocalDateTime updatedTime) {
		this(UUID.randomUUID(),createdTime, updatedTime);
	}


	public BaseEntity() {
    	this(LocalDateTime.now(), LocalDateTime.now());
	}


}
