package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Entity
@Table(name = "login_session")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY) // Eager olmasına gerek yok, sadece id yetebilir
    private Application requestedClient;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "login_session_scopes")
    private Set<String> requestedScopes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "login_session_audiences")
    private Set<String> intendedAudiences;

    @Column(nullable = false)
    private String redirectUri;

    @Column
    private String state; // Client'tan gelen CSRF koruma anahtarı

    @Column
    private String responseType; // "code" vb.

    // PKCE Desteği (Şiddetle öneririm)
    @Column(name = "code_challenge")
    private String codeChallenge;

    @Column(name = "code_challenge_method")
    private String codeChallengeMethod;

    @Column
    private long expireDate;

    @PrePersist
    protected void onCreate() {
        // 2 dakika (120 sn) makul bir süre, login süreci için yeterli.
        this.expireDate = Instant.now().plus(2, ChronoUnit.MINUTES).getEpochSecond();
    }
}