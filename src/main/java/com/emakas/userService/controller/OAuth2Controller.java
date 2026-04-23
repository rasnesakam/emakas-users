package com.emakas.userService.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emakas.userService.dto.AuthorizeDto;
import com.emakas.userService.service.AuthorizeFlowService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/oauth2")
public class OAuth2Controller {

    private final AuthorizeFlowService authorizeFlowService;

    @Autowired
    public OAuth2Controller(AuthorizeFlowService authorizeFlowService) {
        this.authorizeFlowService = authorizeFlowService;
    }

    // TODO: Daha düzgün bir kod olmalı. Bu şeilde doğru bir aktarım yapılamıyor.
    // AuthorizeFlowService Optional dönmesin. try catch ile hata yönetimi yapılsın
    // ve bu hataya göre error sayfasına yönlendirilsin.
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam(name = "response_type", required = false) String responseType,
            @RequestParam(name = "client_id", required = false) UUID clientId,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri,
            @RequestParam(name = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(name = "code_challenge", required = false) String codeChallenge,
            @RequestParam(name = "code_challenge_method", required = false) String codeChallengeMethodString,
            @RequestParam(name = "session_id", required = false) UUID sessionId,
            HttpServletRequest request) {
        return authorizeFlowService.handleAuthorizationFlow(new AuthorizeDto(responseType, clientId, redirectUri, scope,
                state, codeChallenge, codeChallengeMethodString, sessionId, request));
    }
}
