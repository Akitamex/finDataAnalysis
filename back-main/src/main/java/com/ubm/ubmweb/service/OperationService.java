package com.ubm.ubmweb.service;
import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.repository.ArticleGroupRepository;
import com.ubm.ubmweb.repository.ArticleRepository;
import com.ubm.ubmweb.repository.BankAccountRepository;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.CounterpartyGroupRepository;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.ExpenseOperationRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.ProjectDirectionRepository;
import com.ubm.ubmweb.repository.IncomeOperationRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.LoanPaymentRepository;
import com.ubm.ubmweb.repository.LoanRepository;
import com.ubm.ubmweb.repository.ProjectRepository;
import com.ubm.ubmweb.repository.TransferOperationRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;
import org.springframework.data.jpa.domain.Specification;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ubm.ubmweb.graphql.dto.*;
import com.ubm.ubmweb.model.Article;
import com.ubm.ubmweb.model.ArticleGroup;
import com.ubm.ubmweb.model.BankAccount;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.Counterparty;
import com.ubm.ubmweb.model.CounterpartyGroup;
import com.ubm.ubmweb.model.ExpenseOperation;
import com.ubm.ubmweb.model.IncomeOperation;
import com.ubm.ubmweb.model.LegalEntity;
import com.ubm.ubmweb.model.Loan;
import com.ubm.ubmweb.model.LoanPayment;
import com.ubm.ubmweb.model.Operation;
import com.ubm.ubmweb.model.Project;
import com.ubm.ubmweb.model.ProjectDirection;
import com.ubm.ubmweb.model.TransferOperation;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationsRepository operationRepository;
    private final IncomeOperationRepository incomeOperationRepository;
    private final ExpenseOperationRepository expenseOperationRepository;
    private final TransferOperationRepository transferOperationRepository;
    
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final CompanyRepository companyRepository;

    private final ArticleRepository articleRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ProjectRepository projectRepository;
    private final CounterpartyRepository counterpartyRepository;

    private final ArticleGroupRepository articleGroupRepository;
    private final CounterpartyGroupRepository counterpartyGroupRepository;
    private final ProjectDirectionRepository projectDirectionRepository;
    private final LegalEntityRepository legalEntityRepository;

    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanRepository loanRepository;

    @Transactional
    public IncomeOperation createIncomeOperation(UUID userId, CreateIncomeOperationInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if(input.getBalance().compareTo(BigDecimal.ZERO) < 1){
            throw new IllegalArgumentException("Balance can not be set to less than 0");
        }
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        Article article = null;
        if (input.getArticleId() != null) {
            article = articleRepository.findById(input.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + input.getArticleId()));
            if (!article.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
            }
            if(!article.getType().equals("INCOME")){
                throw new UnauthorizedAccessException("Requested Article does not match the operation type.");
            }
        }
        BankAccount bankAccount = bankAccountRepository.findById(input.getBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + input.getBankAccountId()));
        
        if (!bankAccount.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested BankAccount does not belong to the provided company.");
        }
        Project project = null;
        if (input.getProjectId() != null) {
            project = projectRepository.findById(input.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + input.getProjectId()));
            if (!project.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Project does not belong to the provided company.");
            }
        }
        Counterparty counterparty = null;
        if (input.getCounterpartyId() != null) {
            counterparty = counterpartyRepository.findById(input.getCounterpartyId())
                .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
            if (!counterparty.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
            }
        }
        
        IncomeOperation operation = new IncomeOperation();
        operation.setCompany(company);
        operation.setBalance(input.getBalance());
        LocalDate date = LocalDate.parse(input.getDate());
        operation.setDate(date);
        operation.setDescription(input.getDescription());
        operation.setArticle(article);
        operation.setBankAccount(bankAccount);
        operation.setProject(project);
        operation.setCounterparty(counterparty);
        operation.setType("INCOME");
        operation.setIsObligation(input.getIsObligation());

        if(counterparty == null && input.getIsObligation()){
            throw new IllegalArgumentException("An obligation must be tied to a counterparty");
        }

        if(article != null)
            article.addOperation(operation);
        bankAccount.addOperation(operation);
        if(project != null){
            project.addOperation(operation);
            if(project.getType().equals("DEAL")){
                if(counterparty == null){
                    throw new IllegalArgumentException("Counterparty must not be null when a project.type = DEAL");
                }
                operation.setIsObligation(true);
            }
        }
        if(counterparty != null){
            counterparty.addOperation(operation);
            if(input.getIsObligation()){
                counterparty.setDebt(counterparty.getDebt().subtract(input.getBalance()));
            }
        }
        company.addOperation(operation);
        bankAccount.addBalance(operation.getBalance());

        return incomeOperationRepository.save(operation);
    }

    @Transactional
    public ExpenseOperation createExpenseOperation(UUID userId, CreateExpenseOperationInput input){
        if(input.getBalance().compareTo(BigDecimal.ZERO) < 1){
            throw new IllegalArgumentException("Balance can not be set to less than 0");
        }
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        Article article = null;
        if (input.getArticleId() != null) {
            article = articleRepository.findById(input.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + input.getArticleId()));
            if (!article.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
            }
            if(!article.getType().equals("EXPENSE")){
                throw new UnauthorizedAccessException("Requested Article does not match the operation type.");
            }
        }
        BankAccount bankAccount = bankAccountRepository.findById(input.getBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + input.getBankAccountId()));
        
        if (!bankAccount.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested BankAccount does not belong to the provided company.");
        }
        Project project = null;
        if (input.getProjectId() != null) {
            project = projectRepository.findById(input.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + input.getProjectId()));
            if (!project.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Project does not belong to the provided company.");
            }
        }
        Counterparty counterparty = null;
        if (input.getCounterpartyId() != null) {
            counterparty = counterpartyRepository.findById(input.getCounterpartyId())
                .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
            if (!counterparty.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
            }
        }
        
        ExpenseOperation operation = new ExpenseOperation();
        operation.setCompany(company);
        operation.setBalance(input.getBalance());
        LocalDate date = LocalDate.parse(input.getDate());
        operation.setDate(date);
        operation.setDescription(input.getDescription());
        operation.setArticle(article);
        operation.setBankAccount(bankAccount);
        operation.setProject(project);
        operation.setCounterparty(counterparty);
        operation.setType("EXPENSE");
        operation.setIsObligation(input.getIsObligation());

        if(counterparty == null && input.getIsObligation()){
            throw new IllegalArgumentException("An obligation must be tied to a counterparty");
        }

        if(project == null && input.getProjectDirectionId() != null){
            ProjectDirection projectDirection = null;
            projectDirection = projectDirectionRepository.findById(input.getProjectDirectionId())
            .orElseThrow(() -> new IllegalArgumentException("project direction not found for the id:" + input.getProjectDirectionId()));
            if(projectDirection.getCompany().getId() != input.getCompanyId()){
                throw new UnauthorizedAccessException("User does not have access to the Project Direction");
            }
            operation.setProjectDirection(projectDirection);
            projectDirection.addOperation(operation);
        }

        if(article != null)
            article.addOperation(operation);
        bankAccount.addOperation(operation);
        if(project != null)
            project.addOperation(operation);
        if(counterparty != null){
            counterparty.addOperation(operation);
            if(input.getIsObligation()){
                counterparty.setDebt(counterparty.getDebt().add(input.getBalance()));
            }
        }
        company.addOperation(operation);
        bankAccount.substractBalance(operation.getBalance());

        return expenseOperationRepository.save(operation);
    }

    @Transactional
    public TransferOperation createTransferOperation(UUID userId, CreateTransferOperationInput input){
        if(input.getBalance().compareTo(BigDecimal.ZERO) < 1){
            throw new IllegalArgumentException("Balance can not be set to less than 0");
        }
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        Article article = null;
        if (input.getArticleId() != null) {
            article = articleRepository.findById(input.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + input.getArticleId()));
            if (!article.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
            }
            if(!article.getType().equals("TRANSFER")){
                throw new UnauthorizedAccessException("Requested Article does not match the operation type.");
            }
        }
        BankAccount toBankAccount = bankAccountRepository.findById(input.getToBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + input.getToBankAccountId()));
        
        if (!toBankAccount.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested toBankAccount does not belong to the provided company.");
        }
        BankAccount fromBankAccount = bankAccountRepository.findById(input.getFromBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + input.getFromBankAccountId()));
        
        if (!fromBankAccount.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested fromBankAccount does not belong to the provided company.");
        }
        
        TransferOperation operation = new TransferOperation();
        operation.setCompany(company);
        operation.setBalance(input.getBalance());
        LocalDate date = LocalDate.parse(input.getDate());
        operation.setDate(date);
        operation.setDescription(input.getDescription());
        operation.setArticle(article);
        operation.setToBankAccount(toBankAccount);
        operation.setFromBankAccount(fromBankAccount);
        operation.setType("TRANSFER");
        operation.setIsObligation(false);


        toBankAccount.addOperation(operation);
        fromBankAccount.addOperation(operation);
        if(article != null)
            article.addOperation(operation);
        company.addOperation(operation);

        toBankAccount.addBalance(operation.getBalance());
        fromBankAccount.substractBalance(operation.getBalance());

        return transferOperationRepository.save(operation);
    }

    @Transactional(readOnly = true)
    public Operation getOperationByIdAndCompanyId(UUID userId, UUID id, UUID companyId){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Operation operation = operationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Operation not found for the given id: " + id));
        if (!operation.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Operation does not belong to the provided company.");
        }

        return operation;
    }
    

    @Transactional(readOnly = true)
    public List<Operation> findOperations(UUID userId, UUID companyId, DateRangeInput dateRange, List<UUID> articleIds, 
                                           List<UUID> articleGroupIds, List<UUID> counterpartyIds, 
                                           List<UUID> counterpartyGroupIds, List<UUID> projectIds, 
                                           List<UUID> projectDirectionIds, List<String> operationTypes, 
                                           List<UUID> legalEntityIds, List<UUID> bankAccountIds, String description) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        if (articleGroupIds != null && !articleGroupIds.isEmpty()) {
            List<ArticleGroup> validGroups = articleGroupRepository.findByIdInAndCompanyId(articleGroupIds, companyId);
            if (validGroups.size() != articleGroupIds.size()) {
                throw new IllegalArgumentException("One or more ArticleGroups do not belong to the specified company.");
            }
        }

        if (articleIds != null && !articleIds.isEmpty()) {
            List<Article> valid = articleRepository.findByIdInAndCompanyId(articleIds, companyId);
            if (valid.size() != articleIds.size()) {
                throw new IllegalArgumentException("One or more Article do not belong to the specified company.");
            }
        }

        if (counterpartyIds != null && !counterpartyIds.isEmpty()) {
            List<Counterparty> valid = counterpartyRepository.findByIdInAndCompanyId(counterpartyIds, companyId);
            if (valid.size() != counterpartyIds.size()) {
                throw new IllegalArgumentException("One or more Counterparties do not belong to the specified company.");
            }
        }

        if (counterpartyGroupIds != null && !counterpartyGroupIds.isEmpty()) {
            List<CounterpartyGroup> groups = counterpartyGroupRepository.findByIdInAndCompanyId(counterpartyGroupIds, companyId);
            if (groups.size() != counterpartyGroupIds.size()) {
                throw new IllegalArgumentException("One or more CounterpartyGroups do not belong to the specified company.");
            }
        }

        if (projectIds != null && !projectIds.isEmpty()) {
            List<Project> valid = projectRepository.findByIdInAndCompanyId(projectIds, companyId);
            if (valid.size() != projectIds.size()) {
                throw new IllegalArgumentException("One or more Projects do not belong to the specified company.");
            }
        }

        if (projectDirectionIds != null && !projectDirectionIds.isEmpty()) {
            List<ProjectDirection> validDirections = projectDirectionRepository.findByIdInAndCompanyId(projectDirectionIds, companyId);
            if (validDirections.size() != projectDirectionIds.size()) {
                throw new IllegalArgumentException("One or more ProjectDirections do not belong to the specified company.");
            }
        }

        if (legalEntityIds != null && !legalEntityIds.isEmpty()) {
            List<LegalEntity> valid = legalEntityRepository.findByIdInAndCompanyId(legalEntityIds, companyId);
            if (valid.size() != legalEntityIds.size()) {
                throw new IllegalArgumentException("One or more LegalEntities do not belong to the specified company.");
            }
        }

        if (bankAccountIds != null && !bankAccountIds.isEmpty()) {
            List<BankAccount> valid = bankAccountRepository.findByIdInAndCompanyId(bankAccountIds, companyId);
            if (valid.size() != bankAccountIds.size()) {
                throw new IllegalArgumentException("One or more BankAccounts do not belong to the specified company.");
            }
        }

        Specification<Operation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("company").get("id"), companyId));

            if (dateRange != null) {
                LocalDate startDate = dateRange.getStartDate();
                LocalDate endDate = dateRange.getEndDate();
            
                if (startDate != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
                }
                if (endDate != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
                }
            }
            

            if (articleIds != null && !articleIds.isEmpty()) {
                predicates.add(root.get("article").get("id").in(articleIds));
            }

            if (articleGroupIds != null && !articleGroupIds.isEmpty()) {
                predicates.add(root.join("article").get("articleGroup").get("id").in(articleGroupIds));
            }
    
            if (counterpartyIds != null && !counterpartyIds.isEmpty()) {
                predicates.add(root.get("counterparty").get("id").in(counterpartyIds));
            }
    
            if (counterpartyGroupIds != null && !counterpartyGroupIds.isEmpty()) {
                predicates.add(root.join("counterparty").get("counterpartyGroup").get("id").in(counterpartyGroupIds));
            }
    
            if (projectIds != null && !projectIds.isEmpty()) {
                predicates.add(root.get("project").get("id").in(projectIds));
            }
    
            if (projectDirectionIds != null && !projectDirectionIds.isEmpty()) {
                predicates.add(root.join("project").get("projectDirection").get("id").in(projectDirectionIds));
            }
    
            if (operationTypes != null && !operationTypes.isEmpty()) {
                predicates.add(root.get("type").in(operationTypes));
            }
    
            if (legalEntityIds != null && !legalEntityIds.isEmpty()) {
                predicates.add(root.get("legalEntity").get("id").in(legalEntityIds));
            }
    
            if (bankAccountIds != null && !bankAccountIds.isEmpty()) {
                predicates.add(root.get("bankAccount").get("id").in(bankAccountIds));
            }

            if (description != null && !description.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return operationRepository.findAll(spec);
    }

    @Transactional
    public IncomeOperation updateIncomeOperation(UUID userId, UpdateIncomeOperationInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        IncomeOperation operation = incomeOperationRepository.findById(input.getId())
        .orElseThrow(() -> new IllegalArgumentException("Operation not found for the given id: " + input.getId()));
        if(operation.getCompany().getId() != input.getCompanyId()){
            throw new UnauthorizedAccessException("Requested operation does not belong to the provided company");
        }

        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        Article article = null;
        if (input.getArticleId() != null) {
            article = articleRepository.findById(input.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + input.getArticleId()));
            if (!article.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
            }
            if(!article.getType().equals("INCOME")){
                throw new UnauthorizedAccessException("Requested Article does not match the operation type.");
            }
        }
        Article oldArticle = operation.getArticle();
        BankAccount newBankAccount = bankAccountRepository.findById(input.getBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + input.getBankAccountId()));
        
        if (!newBankAccount.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested BankAccount does not belong to the provided company.");
        }
        BankAccount oldBankAccount = operation.getBankAccount();
        Project project = null;
        if (input.getProjectId() != null) {
            project = projectRepository.findById(input.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + input.getProjectId()));
            if (!project.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Project does not belong to the provided company.");
            }
        }
        Project oldProject = operation.getProject();
        Counterparty counterparty = null;
        if (input.getCounterpartyId() != null) {
            counterparty = counterpartyRepository.findById(input.getCounterpartyId())
                .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
            if (!counterparty.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
            }
        }
        Counterparty oldCounterparty = operation.getCounterparty();
                
        oldBankAccount.substractBalance(operation.getBalance());

        if(oldArticle != null)
            oldArticle.removeOperation(operation);
        if(oldCounterparty != null){
            oldCounterparty.removeOperation(operation);
            if(operation.getIsObligation()){
                oldCounterparty.setDebt(oldCounterparty.getDebt().add(operation.getBalance()));
            }
        }
        if(oldBankAccount != null)
            oldBankAccount.removeOperation(operation);
        if(oldProject != null)
            oldProject.removeOperation(operation);

        
        operation.setBalance(input.getBalance());
        LocalDate date = LocalDate.parse(input.getDate());
        operation.setDate(date);
        operation.setDescription(input.getDescription());
        operation.setArticle(article);
        operation.setBankAccount(newBankAccount);
        operation.setProject(project);
        operation.setCounterparty(counterparty);
        operation.setIsObligation(input.getIsObligation());

        if(article != null)
            article.addOperation(operation);
        newBankAccount.addOperation(operation);
        if(project != null)
        {
            if(project.getType().equals("DEAL") && (!input.getIsObligation() || counterparty == null)){
                throw new IllegalArgumentException("When project.type == DEAL, operation must be counted in Obligations and have a counterparty");
            }
            project.addOperation(operation);
        }
        if(counterparty != null){
            counterparty.addOperation(operation);
            if(input.getIsObligation()){
                counterparty.setDebt(counterparty.getDebt().subtract(input.getBalance())); //subt
            }
        }
        newBankAccount.addBalance(operation.getBalance());

        return incomeOperationRepository.save(operation);
    }

    @Transactional
    public ExpenseOperation updateExpenseOperation(UUID userId, UpdateExpenseOperationInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        ExpenseOperation operation = expenseOperationRepository.findById(input.getId())
        .orElseThrow(() -> new IllegalArgumentException("Operation not found for the given id: " + input.getId()));
        if(operation.getCompany().getId() != input.getCompanyId()){
            throw new UnauthorizedAccessException("Requested operation does not belong to the provided company");
        }

        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        Article article = null;
        if (input.getArticleId() != null) {
            article = articleRepository.findById(input.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + input.getArticleId()));
            if (!article.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
            }
            if(!article.getType().equals("EXPENSE")){
                throw new UnauthorizedAccessException("Requested Article does not match the operation type.");
            }
        }
        Article oldArticle = operation.getArticle();
        BankAccount newBankAccount = bankAccountRepository.findById(input.getBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + input.getBankAccountId()));
        
        if (!newBankAccount.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested BankAccount does not belong to the provided company.");
        }
        BankAccount oldBankAccount = operation.getBankAccount();
        Project project = null;
        if (input.getProjectId() != null) {
            project = projectRepository.findById(input.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + input.getProjectId()));
            if (!project.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Project does not belong to the provided company.");
            }
        }
        Project oldProject = operation.getProject();
        Counterparty counterparty = null;
        if (input.getCounterpartyId() != null) {
            counterparty = counterpartyRepository.findById(input.getCounterpartyId())
                .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
            if (!counterparty.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
            }
        }
        Counterparty oldCounterparty = operation.getCounterparty();

        ProjectDirection oldProjectDirection = operation.getProjectDirection();
        oldProjectDirection.removeOperation(operation);
                
        oldBankAccount.addBalance(operation.getBalance());

        if(oldArticle != null)
            oldArticle.removeOperation(operation);
        if(oldCounterparty != null){
            oldCounterparty.removeOperation(operation);
            if(operation.getIsObligation()){
                oldCounterparty.setDebt(oldCounterparty.getDebt().subtract(operation.getBalance()));
            }
        }
        if(oldBankAccount != null)
            oldBankAccount.removeOperation(operation);
        if(oldProject != null)
            oldProject.removeOperation(operation);

        LoanPayment loanPayment = operation.getLoanPayment();
        if (loanPayment != null) {
            BigDecimal amount = operation.getBalance();
            BigDecimal newAmountPaid = loanPayment.getAmountPaid().subtract(amount);

            loanPayment.setAmountPaid(newAmountPaid);
            if (newAmountPaid.compareTo(loanPayment.getTotalPayment()) < 0) {
                loanPayment.setIsPaid(false);
            }

            Loan loan = loanRepository.findById(loanPayment.getLoan().getId())
                .orElseThrow(() -> new IllegalArgumentException("THIS SHOULD NEVER HAPPEN"));
            BigDecimal checkFull = loan.getAmountPaid().subtract(amount);
            if (checkFull.compareTo(BigDecimal.ZERO) < 0) {
                loan.setAmountPaid(BigDecimal.ZERO);
            } else {
                loan.setAmountPaid(checkFull);
            }

            loanPaymentRepository.save(loanPayment);
            loanRepository.save(loan);

            // Unlink operation from loan payment
            operation.setLoanPayment(null);
        }
        
        operation.setBalance(input.getBalance());
        LocalDate date = LocalDate.parse(input.getDate());
        operation.setDate(date);
        operation.setDescription(input.getDescription());
        operation.setArticle(article);
        operation.setBankAccount(newBankAccount);
        operation.setProject(project);
        operation.setCounterparty(counterparty);
        operation.setIsObligation(input.getIsObligation());

        if(project == null && input.getProjectDirectionId() != null){
            ProjectDirection projectDirection = null;
            projectDirection = projectDirectionRepository.findById(input.getProjectDirectionId())
            .orElseThrow(() -> new IllegalArgumentException("project direction not found for the id:" + input.getProjectDirectionId()));
            if(projectDirection.getCompany().getId() != input.getCompanyId()){
                throw new UnauthorizedAccessException("User does not have access to the Project Direction");
            }
            operation.setProjectDirection(projectDirection);
            projectDirection.addOperation(operation);
        }

        if(article != null)
            article.addOperation(operation);
        newBankAccount.addOperation(operation);
        if(project != null)
        project.addOperation(operation);
        if(counterparty != null){
            counterparty.addOperation(operation);
            if(input.getIsObligation()){
                counterparty.setDebt(counterparty.getDebt().add(input.getBalance()));
            }
        }
        newBankAccount.substractBalance(operation.getBalance());

        return expenseOperationRepository.save(operation);
    }

    @Transactional
    public TransferOperation updateTransferOperation(UUID userId, UpdateTransferOperationInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        TransferOperation operation = transferOperationRepository.findById(input.getId())
        .orElseThrow(() -> new IllegalArgumentException("Operation not found for the given id: " + input.getId()));
        if(operation.getCompany().getId() != input.getCompanyId()){
            throw new UnauthorizedAccessException("Requested operation does not belong to the provided company");
        }

        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        Article article = null;
        if (input.getArticleId() != null) {
            article = articleRepository.findById(input.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + input.getArticleId()));
            if (!article.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
            }
            if(!article.getType().equals("INCOME")){
                throw new UnauthorizedAccessException("Requested Article does not match the operation type.");
            }
        }
        Article oldArticle = operation.getArticle();
        BankAccount newToBankAccount = bankAccountRepository.findById(input.getToBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + input.getToBankAccountId()));
        
        if (!newToBankAccount.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested BankAccount does not belong to the provided company.");
        }
        BankAccount newFromBankAccount = bankAccountRepository.findById(input.getFromBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + input.getFromBankAccountId()));
        if (!newFromBankAccount.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested BankAccount does not belong to the provided company.");
        }
        BankAccount oldToBankAccount = operation.getToBankAccount();
        BankAccount oldFromBankAccount = operation.getFromBankAccount();
                
        oldToBankAccount.substractBalance(operation.getBalance());
        oldFromBankAccount.addBalance(operation.getBalance());

        if(oldArticle != null)
            oldArticle.removeOperation(operation);
        if(oldToBankAccount != null)
            oldToBankAccount.removeOperation(operation);
        if(oldFromBankAccount != null)
            oldFromBankAccount.removeOperation(operation);
        
        operation.setBalance(input.getBalance());
        LocalDate date = LocalDate.parse(input.getDate());
        operation.setDate(date);
        operation.setDescription(input.getDescription());
        operation.setArticle(article);
        operation.setToBankAccount(newToBankAccount);
        operation.setFromBankAccount(newFromBankAccount);

        if(article != null)
            article.addOperation(operation);
        newToBankAccount.addOperation(operation);
        newFromBankAccount.addOperation(operation);

        newToBankAccount.addBalance(operation.getBalance());
        newFromBankAccount.substractBalance(operation.getBalance());

        return transferOperationRepository.save(operation);
    }

    @Transactional
    public void deleteOperation(UUID id, UUID userId, UUID companyId){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Operation operation = operationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Operation not found for the given id: " + id));
        if (!operation.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Operation does not belong to the provided company.");
        }
        
        if (operation instanceof IncomeOperation) {
            BankAccount bankAccount = operation.getBankAccount();
            bankAccount.substractBalance(operation.getBalance());
        } else if (operation instanceof ExpenseOperation) {
            BankAccount bankAccount = operation.getBankAccount();
            bankAccount.addBalance(operation.getBalance());
        } else if (operation instanceof TransferOperation transferOperation) {
            BankAccount fromBankAccount = transferOperation.getFromBankAccount();
            BankAccount toBankAccount = transferOperation.getToBankAccount();
            fromBankAccount.addBalance(operation.getBalance());
            toBankAccount.substractBalance(operation.getBalance());
        } else {
            throw new IllegalArgumentException("Requested Operation has invalid type.");
        }
        LoanPayment loanPayment = operation.getLoanPayment();
        if (loanPayment != null) {
            BigDecimal amount = operation.getBalance();
            BigDecimal newAmountPaid = loanPayment.getAmountPaid().subtract(amount);

            loanPayment.setAmountPaid(newAmountPaid);
            if (newAmountPaid.compareTo(loanPayment.getTotalPayment()) < 0) {
                loanPayment.setIsPaid(false);
            }

            Loan loan = loanRepository.findById(loanPayment.getLoan().getId())
                .orElseThrow(() -> new IllegalArgumentException("THIS SHOULD NEVER HAPPEN"));
            BigDecimal checkFull = loan.getAmountPaid().subtract(amount);
            if (checkFull.compareTo(BigDecimal.ZERO) < 0) {
                loan.setAmountPaid(BigDecimal.ZERO);
            } else {
                loan.setAmountPaid(checkFull);
            }

            loanPaymentRepository.save(loanPayment);
            loanRepository.save(loan);

            if(operation.getIsObligation() && operation.getCounterparty() != null){
                Counterparty counterparty = operation.getCounterparty();
                if(operation.getType().equals("INCOME")){
                    counterparty.setDebt(counterparty.getDebt().add(operation.getBalance()));
                }
                else{
                    counterparty.setDebt(counterparty.getDebt().subtract(operation.getBalance()));
                }
            }
        }

        // Unlink operation from loan payment
        operation.setLoanPayment(null);

        
        if(operation.getArticle() != null)
            operation.getArticle().getOperations().remove(operation);
        operation.getBankAccount().getOperations().remove(operation);
        if(operation.getProject() != null)
            operation.getProject().getOperations().remove(operation);
        if(operation.getCounterparty() != null)
            operation.getCounterparty().getOperations().remove(operation);
        operation.getCompany().getOperations().remove(operation);

        operationRepository.deleteById(id);
    }
}
