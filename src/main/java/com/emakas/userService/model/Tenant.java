package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenants")
public class Tenant extends BaseEntity{
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String slug;
    @Column(name = "owner_id")
    private UUID ownerId;
}
