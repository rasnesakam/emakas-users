package com.emakas.userService.domain.auth;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClientPrincipal {
    private UUID clientId;
    private ClientType clientType;
    private String clientName;
}
