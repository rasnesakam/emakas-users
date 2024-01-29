package com.emakas.userService.model;

import com.emakas.userService.shared.enums.Scope;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
public class UserLogin extends BaseEntity{

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "logged_user_id")
    private User loggedUser;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "authorized_audiences", joinColumns = @JoinColumn(name = "authorized_audience_id"))
    @Column
    private Set<String> authorizedAudiences;

    @Column
    private Set<Scope> authorizedScopes;

    @Column
    @GeneratedValue
    private UUID authorizationGrant;

    @Column
    private long expirationDateInSeconds;

    /**
     * Prepersist function will be executed before object being persisted.
     * Set expirationDate 10 minutes after current date
     */
    @PrePersist
    public void prePersist(){
        if (this.expirationDateInSeconds == 0)
            this.expirationDateInSeconds = Instant.now().plus( 10, ChronoUnit.MINUTES).getEpochSecond();
    }

    public UserLogin() {
    }

    public UserLogin(User loggedUser, Set<String> authorizedAudiences, Set<Scope> authorizedScopes) {
        this.loggedUser = loggedUser;
        this.authorizedAudiences = authorizedAudiences;
        this.authorizedScopes = authorizedScopes;
    }
}
