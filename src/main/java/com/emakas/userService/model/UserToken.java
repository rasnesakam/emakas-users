package com.emakas.userService.model;

import com.emakas.userService.shared.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="user_tokens")
public class UserToken {


    /**
     * Represents <b>jti</b> claim for the JWT
     */
    @Id
    private String jti;

    /**
     * Represents <b>iss</b> claim for the JWT
     */
    @Column
    private String iss; // Provider of token


    /**
     * Represents <b>iss</b> claim for the JWT
     */
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_token_audiences", joinColumns = @JoinColumn(name = "user_token_id"))
    @Column
    private Set<String> aud; // Audience of token


    /**
     * Represents <b>scope</b> claim for the JWT
     */
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_token_scopes", joinColumns = @JoinColumn(name = "user_token_id"))
    @Column
    private Set<String> scope; // Audience of token


    /**
     * Represents <b>sub</b> claim for the JWT
     */
    @Column
    private String sub; // Subject of token


    /**
     * Represents <b>exp</b> claim for the JWT
     */
    @Column(columnDefinition = "BIGINT")
    private long exp; // Expiration date


    /**
     * Represents <b>iat</b> claim for the JWT
     */
    @Column(columnDefinition = "BIGINT")
    private long iat; // Date of the creation of token

    @Column
    private TokenType tokenType;

    @Column(length = 2048)
    private String serializedToken;


    @PrePersist
    public void prePersist() {
        if (this.jti == null || this.jti.isBlank()) {
            this.jti = UUID.randomUUID().toString();
        }
    }
}
