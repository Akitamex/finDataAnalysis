package com.ubm.ubmweb.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubm.ubmweb.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import com.ubm.ubmweb.models.User;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.entities.UserCompanyRelationship;
import com.ubm.ubmweb.compositeKey.UserCompanyId;

import com.ubm.ubmweb.dto.UserCompanyRelationshipDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCompanyService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    @Transactional
    public String create(Company company, User user, String role) {

        UserCompanyId userCompanyId = new UserCompanyId(company.getId(), user.getId());
        if (userCompanyRelationshipRepository.existsById(userCompanyId)) throw new IllegalArgumentException("User already has this role in the company");
        UserCompanyRelationship userCompanyRelationship = new UserCompanyRelationship();
        userCompanyRelationship.setId(userCompanyId);
        userCompanyRelationship.setUser(user);
        userCompanyRelationship.setCompany(company);
        userCompanyRelationship.setRole(role);

        user.addUserCompany(userCompanyRelationship);
        company.addUserCompany(userCompanyRelationship);

        // Save user and company to persist the relationship
        userRepository.save(user);
        companyRepository.save(company);

        return user.getFirstName() + " " + user.getLastName() + " is now a " + role + " in " + company.getName() + " company.";
    }

    @Transactional(readOnly = true)
    public List<UserCompanyRelationshipDto> findAll() {
        List<UserCompanyRelationship> userCompanyRelationships = userCompanyRelationshipRepository.findAll();
        List<UserCompanyRelationshipDto> relationshipDtos = new ArrayList<UserCompanyRelationshipDto>();
        for (UserCompanyRelationship relationship : userCompanyRelationships) {
            relationshipDtos.add(UserCompanyRelationshipDto.fromRelationship(relationship));
        }
        return relationshipDtos;
    }

    @Transactional(readOnly = true)
    public List<UserCompanyRelationshipDto> findAllByCompany(Company company) {
        List<UserCompanyRelationship> userCompanyRelationships = userCompanyRelationshipRepository.findByCompany(company);
        List<UserCompanyRelationshipDto> relationshipDtos = new ArrayList<UserCompanyRelationshipDto>();
        for (UserCompanyRelationship relationship : userCompanyRelationships) {
            relationshipDtos.add(UserCompanyRelationshipDto.fromRelationship(relationship));
        }
        return relationshipDtos;
    }

    @Transactional
    public String delete(Company company, User user) {
        UserCompanyId userCompanyId = new UserCompanyId(company.getId(), user.getId());

        UserCompanyRelationship userCompanyRelationship = userCompanyRelationshipRepository.findById(userCompanyId).orElseThrow(() -> new IllegalArgumentException("User Company relationship not found with user id: " + user.getId() + " and company id: " + company.getId()));

        user.removeUserCompany(userCompanyRelationship);
        company.removeUserCompany(userCompanyRelationship);

        // Save user and company to persist the relationship
        userRepository.save(user);
        companyRepository.save(company);
        userCompanyRelationshipRepository.delete(userCompanyRelationship);
        
        return user.getFirstName() + " " + user.getLastName() + " is now no longer an employee at " + company.getName() + " company.";
    }

    @Transactional(readOnly = true)
    public List<UserCompanyRelationship> findByRole(String role) {
        return userCompanyRelationshipRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public List<UserCompanyRelationship> findByRoleAndCompanyId(String role, Long companyId) {
        return userCompanyRelationshipRepository.findByRoleAndCompanyId(role, companyId);
    }
}