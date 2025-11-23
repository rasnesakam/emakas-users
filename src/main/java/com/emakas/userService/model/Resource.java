package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "resources")
public class Resource extends BaseEntity {
    @Column
    private String name;

    @Column
    private String description;

    @Column(unique = true)
    private String uri;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Tenant tenant;

    @Column(name = "resource_secret")
    private String resourceSecret;
}
