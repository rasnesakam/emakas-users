package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "id_tokens")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdToken extends Token {

    @Column(nullable = false)
    private String sub;

    @Column(nullable = false)
    private UUID clientId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "id_token_audiences",
            joinColumns = @JoinColumn(name = "token_id")
    )
    @Column(name = "audience")
    private Set<String> aud;

    @Column
    private String nonce;

    @Column
    private Long authTime;
}