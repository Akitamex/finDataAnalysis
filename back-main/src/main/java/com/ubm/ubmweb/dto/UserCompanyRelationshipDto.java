package com.ubm.ubmweb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ubm.ubmweb.repository.UserRepository;
import com.ubm.ubmweb.model.UserCompanyRelationship;
import com.ubm.ubmweb.repository.CompanyRepository;

import lombok.Data;


import org.springframework.beans.factory.annotation.Autowired;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCompanyRelationshipDto {

    @Autowired
    @JsonIgnore
    private UserRepository userRepository;
    @Autowired
    @JsonIgnore
    private CompanyRepository companyRepository;

    private String user;
    private String company;
    private String role;

    public static UserCompanyRelationshipDto fromRelationship(UserCompanyRelationship userCompanyRelationship){
        UserCompanyRelationshipDto userDto = new UserCompanyRelationshipDto();
        userDto.setUser(userCompanyRelationship.getUser().getPhone());
        userDto.setCompany(userCompanyRelationship.getCompany().getName());
        userDto.setRole(userCompanyRelationship.getRole());
        return userDto;
    }
}
