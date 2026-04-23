package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application")
public class Application extends BaseEntity {
    @Column
    private String name;
    @Column
    private String description;
    @Column(unique = true)
    private String uri;
    @Column(name = "redirect_uri")
    private String redirectUri;
    @Column(name = "client_secret")
    private String clientSecret;
    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Tenant tenant;
}
