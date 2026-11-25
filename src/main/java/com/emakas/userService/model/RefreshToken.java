package com.emakas.userService.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends Token {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_login")
    private UserLogin userLogin;

}
