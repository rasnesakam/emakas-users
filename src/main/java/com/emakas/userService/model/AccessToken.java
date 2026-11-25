package com.emakas.userService.model;

import com.emakas.userService.shared.enums.PrincipalType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "access_tokens")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccessToken extends Token {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "access_token_audiences", joinColumns = @JoinColumn(name = "token_id"))
    @Column(name = "audience")
    private Set<String> aud;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "access_token_scopes", joinColumns = @JoinColumn(name = "token_id"))
    @Column(name = "scope")
    private Set<String> scope;

    @Column
    private String sub;

    @Enumerated(EnumType.STRING)
    private PrincipalType principalType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Application client;
}
