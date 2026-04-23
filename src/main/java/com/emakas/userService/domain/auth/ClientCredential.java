package com.emakas.userService.domain.auth;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClientCredential {
    private UUID clientId;
    private String clientSecret;
    private ClientType clientType;
    private String clientName;
}
