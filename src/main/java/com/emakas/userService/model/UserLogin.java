package com.emakas.userService.model;

import com.emakas.userService.shared.enums.Scope;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserLogin extends BaseEntity{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "logged_user_id")
    private User loggedUser;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "authorized_audiences", joinColumns = @JoinColumn(name = "user_login_id"))
    @Column
    private Set<String> authorizedAudiences;


    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "authorized_scopes", joinColumns = @JoinColumn(name = "user_login_id"))
    @Column
    private Set<String> authorizedScopes;

    @Column
    private UUID authorizationGrant;

    @Column
    private long expirationDateInSeconds;

    @Column(name = "code_challenge")
    private String codeChallenge;

    @JoinColumn(name = "related_session")
    @OneToOne(fetch = FetchType.EAGER)
    private LoginSession relatedSession;

    /**
     * Prepersist function will be executed before object being persisted.
     * Set expirationDate 10 minutes after current date
     */
    @PrePersist
    public void prePersist(){
        if (this.getAuthorizationGrant() == null)
            this.setAuthorizationGrant(UUID.randomUUID());
        if (this.expirationDateInSeconds == 0)
            this.expirationDateInSeconds = Instant.now().plus( 10, ChronoUnit.MINUTES).getEpochSecond();
    }


    public UserLogin(User loggedUser, Set<String> authorizedAudiences, Set<String> authorizedScopes) {
        this.loggedUser = loggedUser;
        this.authorizedAudiences = authorizedAudiences;
        this.authorizedScopes = authorizedScopes;
    }

    public UserLogin(LoginSession loginSession) {
        this.relatedSession = loginSession;
        this.loggedUser = loginSession.getIntendedUser();
        this.authorizedScopes = loginSession.getRequestedScopes();
    }
}
