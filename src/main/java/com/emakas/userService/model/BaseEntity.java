package com.emakas.userService.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@Entity
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

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
