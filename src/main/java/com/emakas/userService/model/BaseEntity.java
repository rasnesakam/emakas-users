package com.emakas.userService.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


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


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
