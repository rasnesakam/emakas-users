package com.emakas.userService.model;

import com.emakas.userService.shared.enums.Scope;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="user_tokens")
public class UserToken extends BaseEntity {


    /**
     * Represents <b>iss</b> claim for the JWT
     */
    @Column
    private String iss; // Provider of token


    /**
     * Represents <b>iss</b> claim for the JWT
     */
    @Column
    private String aud; // Audience of token


    /**
     * Represents <b>scope</b> claim for the JWT
     */
    @ElementCollection(targetClass = Scope.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_token_scopes", joinColumns = @JoinColumn(name = "user_token_id"))
    @Enumerated(EnumType.STRING)
    @Column
    private Set<Scope> scope; // Audience of token


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
    private String serializedToken;

    public UserToken(String iss, String aud, String sub, long exp, String serializedToken) {
        this.iss = iss;
        this.aud = aud;
        this.sub = sub;
        this.exp = exp;
        this.serializedToken = serializedToken;
    }

    public UserToken(){}

}
