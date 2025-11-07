package com.emakas.userService.model;

import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.StringUtils;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "username", unique = true, length = 30)
    private String userName;
    
    @Column(unique = true, length = 30)
    private String email;
    
    @Column(length = 64)
    private String password;
    
    @Column(length = 60)
    private String name;
    
    @Column(length = 30)
    private String surname;

    @Column(length = 90)
    private String fullName;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Tenant tenant;

    @PrePersist
    private void prePersist() {
        if (StringUtils.isNullOrEmpty(fullName) && !StringUtils.isNullOrEmpty(name) && !StringUtils.isNullOrEmpty(surname)) {
            fullName = name.concat(Constants.ONE_SPACE).concat(surname);
        }
    }
}
