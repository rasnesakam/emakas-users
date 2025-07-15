package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Entity
@Table(name = "login_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginSession extends BaseEntity{

    @ManyToOne(fetch = FetchType.EAGER)
    private User intendedUser;

    @ManyToOne
    private Application requestedClient;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = String.class)
    @CollectionTable(name = "login_session_requested_scopes", joinColumns = @JoinColumn(name = "login_session_id"))
    private Set<String> requestedScopes;

    @Column
    private String redirectUri;

    private long expireDate;

    @PrePersist
    protected void onCreate() {
        this.expireDate = Instant.now().plus(120, ChronoUnit.SECONDS).getEpochSecond();
    }
}
