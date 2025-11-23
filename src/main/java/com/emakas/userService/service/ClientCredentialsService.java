package com.emakas.userService.service;

import com.emakas.userService.domain.auth.ClientCredential;
import com.emakas.userService.domain.auth.ClientPrincipal;
import com.emakas.userService.mappers.ClientCredentialMapper;
import com.emakas.userService.mappers.ClientPrincipalMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientCredentialsService {
    private final ApplicationService applicationService;
    private final ResourceService resourceService;
    private final PasswordEncoder passwordEncoder;
    private final ClientCredentialMapper clientCredentialMapper;
    private final ClientPrincipalMapper clientPrincipalMapper;

    public ClientCredentialsService(ApplicationService applicationService, ResourceService resourceService, PasswordEncoder passwordEncoder, ClientCredentialMapper clientCredentialMapper, ClientPrincipalMapper clientPrincipalMapper) {
        this.applicationService = applicationService;
        this.resourceService = resourceService;
        this.passwordEncoder = passwordEncoder;
        this.clientCredentialMapper = clientCredentialMapper;
        this.clientPrincipalMapper = clientPrincipalMapper;
    }

    public Optional<ClientPrincipal> validateClient(@NotNull UUID clientId, @NotNull String clientSecret) {
        return this.getClientCredentials(clientId).flatMap(credentials -> validateClientCredentials(credentials, clientSecret));
    }

    public Optional<ClientPrincipal> validateClientCredentials(@NotNull ClientCredential clientCredential, @NotNull String clientSecret) {
        if (passwordEncoder.matches(clientSecret, clientCredential.getClientSecret())) {
            return Optional.of(clientPrincipalMapper.fromClientCredential(clientCredential));
        }
        return Optional.empty();
    }

    public Optional<ClientCredential> getClientCredentials(UUID clientId) {
        return applicationService.getById(clientId)
                .map(clientCredentialMapper::fromApplication)
                .or(() -> resourceService.getById(clientId).map(clientCredentialMapper::fromResource));
    }

}
