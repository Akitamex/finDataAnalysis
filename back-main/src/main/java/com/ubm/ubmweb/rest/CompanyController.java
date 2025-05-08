package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CompanyInput;
import com.ubm.ubmweb.helper.JwtUtil;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.model.UserCompanyRelationship;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;
import com.ubm.ubmweb.repository.UserRepository;
// import com.ubm.ubmweb.security.jwt.JwtTokenProvider;
import com.ubm.ubmweb.service.CompanyService;
import com.ubm.ubmweb.service.UserCompanyService;
import com.ubm.ubmweb.service.UserService;

import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Validated
public class CompanyController {

    private final CompanyService companyService;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final UserCompanyService userCompanyService;    
    private final UserService userService;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JwtUtil jwtUtil;


    @GetMapping("/user/getcompanies/{userId}")  //Вытащить компании пренадлежащие определенному юзеру. 
    public ResponseEntity<List<Company>> getCompaniesByUserId(@PathVariable @NotNull UUID userId,
                                                              ServletRequest servletRequest) {
        UUID isPermitted = jwtUtil.userIdFromRequest(servletRequest);
        if(!isPermitted.equals(userId)){
            throw new UnauthorizedAccessException("Can't view the companies of other users");
        }
        return ResponseEntity.ok(companyService.findCompaniesByUserId(userId));
    }

    @GetMapping("/{companyId}") //Вытащить компанию по её id. Валидация делается внутри сервиса
    public ResponseEntity<Company> getCompanyById(@PathVariable @NotNull UUID companyId, 
                                                  ServletRequest servletRequest) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(companyService.getCompanyByIdIfUserAssociated(companyId, userId));
    }

    @PostMapping("/create") //Создать компанию
    public ResponseEntity<Company> createCompany(ServletRequest servletRequest, 
                                                 @Valid @RequestBody CompanyInput data) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(companyService.createCompany(userId, data));
    }

    @PutMapping("/update/{companyId}") //Обновить компанию
    public ResponseEntity<Company> updateCompany(@PathVariable @NotNull UUID companyId, 
                                                 ServletRequest servletRequest, 
                                                 @Valid @RequestBody CompanyInput data) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        userService.setCompanyId(userId, companyId);
        return ResponseEntity.ok(companyService.updateCompany(companyId, userId, data));
    }

    @DeleteMapping("/delete/{companyId}")   //Удалить компанию
    public ResponseEntity<Void> deleteCompany(@PathVariable @NotNull UUID companyId, 
                                              ServletRequest servletRequest) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        companyService.deleteCompany(companyId, userId);
        return ResponseEntity.noContent().build();
    }

    /* Debug function */
    @GetMapping("/allUserCompanyRelationships")
    public ResponseEntity<?> getAllUserCompanyRelationships() {
        return ResponseEntity.ok(userCompanyService.findAll());
    }
    /* Debug function */

    @GetMapping("/{id}/members")// Показать юзеров которые пренадлежат компании
    public ResponseEntity<?> getAllUserCompanyRelationshipsByCompanyId(@PathVariable(name = "id") UUID companyId, ServletRequest servletRequest) { 
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        userCompanyRelationshipRepository.findById(new UserCompanyId(companyId, userId)).orElseThrow(() -> new IllegalArgumentException("User Company relationship not found with user id: " + userId + " and company id: " + companyId));
        return ResponseEntity.ok(userCompanyService.findAllByCompany(company));
    }

    @GetMapping //Показать все компании к которым принадлежит юзер
    public ResponseEntity<?> getAllUserCompanyRelationshipsByUserId(ServletRequest servletRequest){  
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(companyService.findCompaniesByUserId(userId));
    }

    @PostMapping("/{id}/members")//Добавить челикса в компанию
    public ResponseEntity<?> createUserCompanyRelationship(@PathVariable(name = "id") UUID companyId, ServletRequest servletRequest, @RequestBody Map<String, Object> request) {
        UUID ownerId = jwtUtil.userIdFromRequest(servletRequest);
        UUID employeeId = (UUID) request.get("employeeId");
        String role = (String) request.get("role");
        role = role.toLowerCase();

        if (!(role.equals("admin") || role.equals("user"))) throw new IllegalArgumentException("Role '" + role + "' doesn't exist"); 
        
        userRepository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + ownerId));
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
        User employee = userRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + employeeId));

        
        UserCompanyRelationship userCompanyRelationship = userCompanyRelationshipRepository.findById(new UserCompanyId(companyId, ownerId)).orElseThrow(() -> new IllegalArgumentException("User Company relationship not found with user id: " + ownerId + " and company id: " + companyId));
        if (!userCompanyRelationship.getRole().equals("owner")) throw new IllegalArgumentException("User with id: " + ownerId + " does not own the company with id: " + companyId);
        return ResponseEntity.ok(userCompanyService.create(company, employee, role));
    }

    @DeleteMapping("/{company_id}/members/{employee_id}") //Убрать челикса из компании
    public ResponseEntity<?> deleteUserCompanyRelationship(@PathVariable(name = "company_id") UUID companyId, @PathVariable(name = "employee_id") UUID employeeId, ServletRequest servletRequest) {
        UUID ownerId = jwtUtil.userIdFromRequest(servletRequest);

        userRepository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + ownerId));
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
        User employee = userRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + employeeId));

        
        UserCompanyRelationship userCompanyRelationship = userCompanyRelationshipRepository.findById(new UserCompanyId(companyId, ownerId)).orElseThrow(() -> new IllegalArgumentException("User Company relationship not found with user id: " + ownerId + " and company id: " + companyId));
        if (!userCompanyRelationship.getRole().equals("owner")) throw new IllegalArgumentException("User with id: " + ownerId + " does not own the company with id: " + companyId);
        return ResponseEntity.ok(userCompanyService.delete(company, employee));
    }
}
