package com.emakas.userService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class Token {

    @Id
    private UUID jti;

    @Column(nullable = false)
    private String iss;

    @Column(nullable = false)
    private long exp;

    @Column(nullable = false)
    private long iat;

    @PrePersist
    public void prePersist() {
        if (jti == null) {
            jti = UUID.randomUUID();
        }
    }
}