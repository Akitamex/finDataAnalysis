package com.ubm.ubmweb.dto;


import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ubm.ubmweb.model.User;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private UUID id;
    private String phone;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private String country;
    private String timezone;
    private String biography;

    private String password;


    public User toUser(){
        User user = new User();
        user.setId(id);
        user.setPhone(phone);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoto(photo);
        user.setCountry(country);
        user.setTimezone(timezone);
        user.setBiography(biography);
        return user;
    }

    public static UserDto fromUser(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setPhone(user.getPhone());
        userDto.setPassword(user.getPassword());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoto(user.getPhoto());
        userDto.setCountry(user.getCountry());
        userDto.setTimezone(user.getTimezone());
        userDto.setBiography(user.getBiography());
        return userDto;
    }
}
