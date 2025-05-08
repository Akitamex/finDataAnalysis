package com.ubm.ubmweb.services;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Article;
import com.ubm.ubmweb.entities.ArticleGroup;
import com.ubm.ubmweb.entities.BankAccount;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.entities.Counterparty;
import com.ubm.ubmweb.entities.CounterpartyGroup;
import com.ubm.ubmweb.entities.LegalEntity;
import com.ubm.ubmweb.entities.Operation;
import com.ubm.ubmweb.entities.Project;
import com.ubm.ubmweb.entities.ProjectDirection;
import com.ubm.ubmweb.models.User;
import com.ubm.ubmweb.entities.UserCompanyRelationship;
import com.ubm.ubmweb.repository.ArticleGroupRepository;
import com.ubm.ubmweb.repository.ArticleRepository;
import com.ubm.ubmweb.repository.BankAccountRepository;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.CounterpartyGroupRepository;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.ProjectDirectionRepository;
import com.ubm.ubmweb.repository.ProjectRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;
import com.ubm.ubmweb.repository.UserRepository;
import com.ubm.ubmweb.graphql.dto.CompanyInput;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import java.util.List;
// import java.util.Optional;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final ArticleGroupRepository articleGroupRepository;
    private final ArticleRepository articleRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final CounterpartyGroupRepository counterpartyGroupRepository;
    private final LegalEntityRepository legalEntityRepository;
    private final OperationsRepository operationsRepository;
    private final ProjectDirectionRepository projectDirectionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserCompanyService userCompanyService;

    
    @Transactional(readOnly = true)
    public List<Company> findCompaniesByUserId(Long userId) {
        List<UserCompanyRelationship> relationships = userCompanyRelationshipRepository.findByUserId(userId);
        List<Company> companies = relationships.stream()
                .map(UserCompanyRelationship::getCompany)
                .collect(Collectors.toList());
        return companies;
    }

    @Transactional(readOnly = true)
    public Company getCompanyByIdIfUserAssociated(Long companyId, Long userId) {
        // Check if there's an association between the user and the company
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to this company.");
        }
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
        return company;
    }

    @Transactional
    public Company createCompany(Long userId, CompanyInput data) {
        Company company = new Company();
        company = CompanyInput.companyInputToCompany(data, company);
        
        if (company.getName() == null || company.getShortUrl() == null || company.getBrandingReports() == null || company.getBrandingEmails() == null) {
            throw new IllegalArgumentException("Company cannot have name, short url, branding reports and branding emails values as null.");
        }

        Company savedCompany = companyRepository.save(company);

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        userCompanyService.create(company, user, "owner");

        return savedCompany;
    }

    @Transactional
    public Company updateCompany(Long companyId, Long userId, CompanyInput data) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
        
        company = CompanyInput.companyInputToCompany(data, company);

        return companyRepository.save(company);
    }

    @Transactional
    public void deleteCompany(Long companyId, Long userId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to delete this company.");
        }
        List<Article> articles = articleRepository.findByCompanyId(companyId);
        if(!articles.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated articles.");
        }
        List<ArticleGroup> articleGroups = articleGroupRepository.findByCompanyId(companyId);
        if(!articleGroups.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated articleGroups.");
        }
        List<Project> projects = projectRepository.findByCompanyId(companyId);
        if(!projects.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated projects.");
        }
        List<ProjectDirection> projectDirections = projectDirectionRepository.findByCompanyId(companyId);
        if(!projectDirections.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated projectDirections.");
        }
        List<Counterparty> counterparties = counterpartyRepository.findByCompanyId(companyId);
        if(!counterparties.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated counterparties.");
        }
        List<CounterpartyGroup> counterpartyGroups = counterpartyGroupRepository.findByCompanyId(companyId);
        if(!counterpartyGroups.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated counterpartyGroups.");
        }
        List<Operation> operations = operationsRepository.findByCompanyId(companyId);
        if(!operations.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated operations.");
        }
        List<BankAccount> bankAccounts = bankAccountRepository.findByCompanyId(companyId);
        if(!bankAccounts.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated bankAccounts.");
        }
        List<LegalEntity> legalEntities = legalEntityRepository.findByCompanyId(companyId);
        if(!legalEntities.isEmpty()){
            throw new IllegalStateException("Cannot delete Company as it has associated legalEntities.");
        }

        userCompanyRelationshipRepository.deleteAllByCompanyId(companyId);
        companyRepository.deleteById(companyId);
    }
    

}