package com.emakas.userService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
}
