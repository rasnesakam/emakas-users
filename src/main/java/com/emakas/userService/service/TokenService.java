package com.emakas.userService.service;

import com.emakas.userService.model.Token;
import com.emakas.userService.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class TokenService {
    private final UserTokenRepository userTokenRepository;
    @Autowired
    public TokenService(UserTokenRepository repository) {
        this.userTokenRepository = repository;
    }
    public Token save(Token token) {
        return userTokenRepository.save(token);
    }
    public Collection<Token> saveBatch(Token... tokens) {
        return userTokenRepository.saveAll(Arrays.stream(tokens).toList());
    }
}
