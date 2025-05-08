// package com.ubm.ubmweb.rest;

// import com.ubm.ubmweb.security.jwt.JwtTokenProvider;
// import com.ubm.ubmweb.service.CompanyService;
// import com.ubm.ubmweb.service.UserCompanyService;
// import com.ubm.ubmweb.repository.UserRepository;
// import com.ubm.ubmweb.repository.CompanyRepository;
// import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

// import com.ubm.ubmweb.compositeKey.UserCompanyId;
// import com.ubm.ubmweb.model.Company;
// import com.ubm.ubmweb.model.User;
// import com.ubm.ubmweb.model.UserCompanyRelationship;

// import jakarta.servlet.ServletRequest;
// import lombok.RequiredArgsConstructor;

// import java.util.Map;
// import java.util.UUID;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping(value = "/api/user-companies/")
// @RequiredArgsConstructor
// public class CompanyRestController {    

//     private final UserCompanyService userCompanyService;
    
//     private final UserRepository userRepository;
    
//     private final CompanyRepository companyRepository;

//     private final CompanyService companyService;

//     private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    
//     private final JwtTokenProvider jwtTokenProvider;

//     private UUID userIdFromRequest(ServletRequest servletRequest) {
//         String token = jwtTokenProvider.resolveToken((jakarta.servlet.http.HttpServletRequest) servletRequest);
//         if (token==null) throw new IllegalArgumentException("No Token");
//         token = jwtTokenProvider.decryptToken(token);
//         if(token==null|| !jwtTokenProvider.validateToken(token)){
//             throw new IllegalArgumentException("Invalid or expired token");
//         }
//         UUID userId = jwtTokenProvider.getUserId(token);
//         return userId;
//     }
//     /* Debug function */
//     @GetMapping("/allUserCompanyRelationships")
//     public ResponseEntity<?> getAllUserCompanyRelationships() {
//         return ResponseEntity.ok(userCompanyService.findAll());
//     }
//     /* Debug function */

//     @GetMapping("/{id}/members")
//     public ResponseEntity<?> getAllUserCompanyRelationshipsByCompanyId(@PathVariable(name = "id") UUID companyId, ServletRequest servletRequest) {
//         UUID userId = userIdFromRequest(servletRequest);
//         Company company = companyRepository.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
//         userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//         userCompanyRelationshipRepository.findById(new UserCompanyId(companyId, userId)).orElseThrow(() -> new IllegalArgumentException("User Company relationship not found with user id: " + userId + " and company id: " + companyId));
//         return ResponseEntity.ok(userCompanyService.findAllByCompany(company));
//     }

//     @GetMapping
//     public ResponseEntity<?> getAllUserCompanyRelationshipsByUserId(ServletRequest servletRequest){
//         UUID userId = userIdFromRequest(servletRequest);
//         return ResponseEntity.ok(companyService.findCompaniesByUserId(userId));
//     }

//     @PostMapping("/{id}/members")
//     public ResponseEntity<?> createUserCompanyRelationship(@PathVariable(name = "id") UUID companyId, ServletRequest servletRequest, @RequestBody Map<String, Object> request) {
//         UUID ownerId = userIdFromRequest(servletRequest);
//         UUID employeeId = (UUID) request.get("employeeId");
//         String role = (String) request.get("role");
//         role = role.toLowerCase();

//         if (!(role.equals("admin") || role.equals("user"))) throw new IllegalArgumentException("Role '" + role + "' doesn't exist"); 
        
//         userRepository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + ownerId));
//         Company company = companyRepository.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
//         User employee = userRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + employeeId));

        
//         UserCompanyRelationship userCompanyRelationship = userCompanyRelationshipRepository.findById(new UserCompanyId(companyId, ownerId)).orElseThrow(() -> new IllegalArgumentException("User Company relationship not found with user id: " + ownerId + " and company id: " + companyId));
//         if (!userCompanyRelationship.getRole().equals("owner")) throw new IllegalArgumentException("User with id: " + ownerId + " does not own the company with id: " + companyId);
//         return ResponseEntity.ok(userCompanyService.create(company, employee, role));
//     }

//     @DeleteMapping("/{company_id}/members/{employee_id}")
//     public ResponseEntity<?> deleteUserCompanyRelationship(@PathVariable(name = "company_id") UUID companyId, @PathVariable(name = "employee_id") UUID employeeId, ServletRequest servletRequest) {
//         UUID ownerId = userIdFromRequest(servletRequest);

//         userRepository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + ownerId));
//         Company company = companyRepository.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
//         User employee = userRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + employeeId));

        
//         UserCompanyRelationship userCompanyRelationship = userCompanyRelationshipRepository.findById(new UserCompanyId(companyId, ownerId)).orElseThrow(() -> new IllegalArgumentException("User Company relationship not found with user id: " + ownerId + " and company id: " + companyId));
//         if (!userCompanyRelationship.getRole().equals("owner")) throw new IllegalArgumentException("User with id: " + ownerId + " does not own the company with id: " + companyId);
//         return ResponseEntity.ok(userCompanyService.delete(company, employee));
//     }
// }
