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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

}
