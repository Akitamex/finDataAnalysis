package com.ubm.ubmweb.security;

import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.security.jwt.JwtUser;
import com.ubm.ubmweb.security.jwt.JwtUserFactory;
import com.ubm.ubmweb.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // This is a service class
@Slf4j // This is a logger class
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {


    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userService.findByPhone(phone);
        if(user == null){
            throw new UsernameNotFoundException("User with phone number: " + phone + " not found");
        }
        JwtUser jwtUser = JwtUserFactory.create(user);

        log.info("IN loadUserByUsername - user with phone number: {} successfully loaded", phone);


        return jwtUser;
    }
}
