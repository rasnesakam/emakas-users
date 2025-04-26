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
    @CollectionTable(name = "authorized_audiences", joinColumns = @JoinColumn(name = "authorized_audience_id"))
    @Column
    private Set<String> authorizedAudiences;


    @ElementCollection(targetClass = Scope.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "authorized_scopes", joinColumns = @JoinColumn(name = "authorized_scopes_id"))
    @Column
    private Set<Scope> authorizedScopes;

    @Column
    private UUID authorizationGrant;

    @Column
    private long expirationDateInSeconds;

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


    public UserLogin(User loggedUser, Set<String> authorizedAudiences, Set<Scope> authorizedScopes) {
        this.loggedUser = loggedUser;
        this.authorizedAudiences = authorizedAudiences;
        this.authorizedScopes = authorizedScopes;
    }
}
