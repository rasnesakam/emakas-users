package com.emakas.userService.service;

import com.emakas.userService.model.User;
import com.emakas.userService.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends BaseService<User, UUID>  implements UserDetailsService{
	private UserRepository repository;
    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
        this.repository = repository;
    }
    
    public User getByUserName(String userName) {
    	return repository.getByUserName(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.getByUserName(username);
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUname())
                .password(user.getPassword())
                .roles(roles.toArray(new String[0]))
                .build();
    }
}
