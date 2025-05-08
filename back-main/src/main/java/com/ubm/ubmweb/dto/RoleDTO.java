package com.ubm.ubmweb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ubm.ubmweb.model.Role;
import com.ubm.ubmweb.model.User;

import lombok.Data;

import java.util.List;
import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDTO { 
    private String name;
    private List<String> userPhones;

    
    public Role toRole() {
        Role role = new Role();
        role.setName(name);
        return role;
    }

    public RoleDTO fromRole(Role role) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName(role.getName());
        List<String> userPhones = new ArrayList<String>();
        for (User user : role.getUsers()) userPhones.add(user.getPhone());
        roleDTO.setUserPhones(userPhones);
        return roleDTO;
    }

}
