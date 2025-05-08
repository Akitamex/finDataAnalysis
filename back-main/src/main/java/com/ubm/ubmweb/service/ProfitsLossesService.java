package com.ubm.ubmweb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
// import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.model.ExpenseOperation;
import com.ubm.ubmweb.model.FixedAsset;
import com.ubm.ubmweb.model.Operation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.model.PLProjectAttributes;
import com.ubm.ubmweb.model.ProfitsLosses;
import com.ubm.ubmweb.model.ProfitsLossesArticles;
import com.ubm.ubmweb.model.ProfitsLossesProjects;
import com.ubm.ubmweb.repository.FixedAssetRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfitsLossesService{
    
    private final OperationsRepository operationsRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final FixedAssetRepository fixedAssetRepository;

    public ProfitsLosses profitsLosses(UUID companyId, UUID userId, String type, String timeframe, List<UUID> bankAccountIds, List<UUID> projectIds){
        Boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId,userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User does not have acces to company with id: " + companyId);
        }

        ProfitsLosses profitsLosses = new ProfitsLosses();
        DateRangeInput dateRange = new DateRangeInput("","");
        dateRange.parseTimeFrame(timeframe);
        profitsLosses.setTimeframe(timeframe);
        profitsLosses.setColumns(columns(dateRange));
        List<DateRangeInput> rangesFromCols = new ArrayList<DateRangeInput>();
        rangesFromCols = datesFromCols(dateRange);

        List<BigDecimal> columnLengthEmpty = new ArrayList<BigDecimal>();
        for(int i = 0;i <= profitsLosses.getColumns().size(); i++){
            columnLengthEmpty.add(BigDecimal.ZERO);
        }
        List<BigDecimal> revenueSum = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> directCostsSum = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> grossProfit = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> otherIncomeSum = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> indirectCostsSum = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> operatingProfits = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> taxesSum = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> depreciationSum = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> netProfit = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> withdrawalOfProfitsSum = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> retainedEarnings = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> directCostsVariablesSum = new ArrayList<BigDecimal>(columnLengthEmpty);
        List<BigDecimal> directCostsConstantsSum = new ArrayList<BigDecimal>(columnLengthEmpty);

        List<String> rows = new ArrayList<String>();
        rows.add("Выручка");
        rows.add("Прямые расходы");
        rows.add("Валовая прибыль");
        rows.add("Прочие доходы");
        rows.add("Косвенные расходы");
        rows.add("Налоги");
        rows.add("Операционная прибыль");
        rows.add("Амортизация");
        rows.add("Чистая прибыль");
        rows.add("Вывод прибыли из бизнеса");
        rows.add("Нераспределенная прибыль");
        profitsLosses.setRows(rows);

        
        //<-- ПРОЧИЕ ДОХОДЫ PL.[otherIncomeSum] PLA.[otherIncomeNames, otherIncome] 

        List<Operation> otherIncomeOperations = operationsRepository.findOtherIncome(companyId, dateRange.getStartDate(), dateRange.getEndDate());
        // List<Operation> otherIncomeOperationsCopy = new ArrayList<Operation>(otherIncomeOperations);
        HashSet<String> otherIncomeArticles = new HashSet<String>();
        List<List<BigDecimal>> otherIncome = new ArrayList<List<BigDecimal>>();

        Iterator<Operation> otherIncomeIterator =  otherIncomeOperations.iterator();

        while(otherIncomeIterator.hasNext()){
            Operation tempOperation = otherIncomeIterator.next();
            if(bankAccountIds != null){
                if(!bankAccountIds.contains(tempOperation.getBankAccount().getId())){
                    otherIncomeIterator.remove();
                    continue;
                }
            }
            
            if(projectIds != null){
                if(tempOperation.getProject() != null){
                    if(!projectIds.contains(tempOperation.getProject().getId())){
                        otherIncomeIterator.remove();
                        continue;
                    }
                }
            }

            String temp;
            if(tempOperation.getArticle() == null){
                temp = "Статья не указана";
            }
            else{
                temp = tempOperation.getArticle().getName();
            }
            otherIncomeArticles.add(temp);
        }

        List<String> otherIncomeNames = new ArrayList<String>();

        for(String i : otherIncomeArticles){
            otherIncomeNames.add(i);
            List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
            BigDecimal sum = BigDecimal.ZERO;
            for(int j = 0; j < profitsLosses.getColumns().size();j++){
                BigDecimal cellVal = BigDecimal.ZERO;
                for(int k = 0; k < otherIncomeOperations.size();k++){
                    Operation tempOperation = otherIncomeOperations.get(k); 
                    if(tempOperation.getArticle() != null){
                        if(tempOperation.getArticle().getName().equals(i)){
                            if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                            && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                cellVal = cellVal.add(tempOperation.getBalance());
                            }
                        }
                    }
                    else{
                        if(i.equals("Статья не указана")){
                            if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                            && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                cellVal = cellVal.add(tempOperation.getBalance());
                            }
                        }
                    }    
                }
                cellVals.add(cellVal);
                sum = sum.add(cellVal);
                otherIncomeSum.set(j, otherIncomeSum.get(j).add(cellVal));
            }
            cellVals.add(sum);
            otherIncome.add(cellVals);
            otherIncomeSum.set(otherIncomeSum.size() - 1, otherIncomeSum.get(otherIncomeSum.size() - 1).add(sum));
        }
        profitsLosses.setOtherIncome(otherIncome);
        profitsLosses.setOtherIncomeNames(otherIncomeNames);
        profitsLosses.setOtherIncomeSum(otherIncomeSum);

        // ПРОЧИЕ ДОХОДЫ--> ##################################################################################################################

        //<-- КОСВЕННЫЕ РАСХОДЫ PL.[indirectCostsSum] PLA.[indirectCostsNames, indirectCosts]

        List<Operation> indirectCostsOperations = operationsRepository.findIndirectCosts(companyId, dateRange.getStartDate(), dateRange.getEndDate());
        // List<Operation> indirectCostsOperationsCopy = new ArrayList<Operation>(indirectCostsOperations);
        
        HashSet<String> indirectCostsArticles = new HashSet<String>();
        List<List<BigDecimal>> indirectCosts = new ArrayList<List<BigDecimal>>();

        Iterator<Operation> indirectCostsIterator = indirectCostsOperations.iterator();

        while(indirectCostsIterator.hasNext()){
            Operation tempOperation = indirectCostsIterator.next();

            if(bankAccountIds != null){
                if(!bankAccountIds.contains(tempOperation.getBankAccount().getId())){
                    indirectCostsIterator.remove();
                    continue;
                }
            }
            
            if(projectIds != null){
                if(tempOperation.getProject() != null){
                    if(!projectIds.contains(tempOperation.getProject().getId())){
                        indirectCostsIterator.remove();
                        continue;
                    }
                }
            }

            String temp;
            if(tempOperation.getArticle() == null){
                temp = "Статья не указана";
            }
            else{
                temp = tempOperation.getArticle().getName();
            }
            indirectCostsArticles.add(temp);
        }

        List<String> indirectCostsNames = new ArrayList<String>();

        for(String i : indirectCostsArticles){
            indirectCostsNames.add(i);
            List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
            BigDecimal sum = BigDecimal.ZERO;
            for(int j = 0; j < profitsLosses.getColumns().size();j++){
                BigDecimal cellVal = BigDecimal.ZERO;
                for(int k = 0; k < indirectCostsOperations.size();k++){
                    Operation tempOperation = indirectCostsOperations.get(k); 
                    if(tempOperation.getArticle() != null){
                        if(tempOperation.getArticle().getName().equals(i)){
                            if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                            && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                cellVal = cellVal.add(tempOperation.getBalance());
                            }
                        }
                    }
                    else{
                        if(i.equals("Статья не указана")){
                            if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                            && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                cellVal = cellVal.add(tempOperation.getBalance());
                            }
                        }
                    }    
                }
                cellVals.add(cellVal);
                sum = sum.add(cellVal);
                indirectCostsSum.set(j, indirectCostsSum.get(j).add(cellVal));
            }
            cellVals.add(sum);
            indirectCosts.add(cellVals);
            indirectCostsSum.set(indirectCostsSum.size() - 1, indirectCostsSum.get(indirectCostsSum.size() - 1).add(sum));
        }
        profitsLosses.setIndirectCosts(indirectCosts);
        profitsLosses.setIndirectCostsNames(indirectCostsNames);
        profitsLosses.setIndirectCostsSum(indirectCostsSum);

        //КОСВЕННЫЕ РАСХОДЫ--> ##################################################################################################################
        
         //<-- АМОРТИЗАЦИЯ 

        List<FixedAsset> fixedAssets = fixedAssetRepository.findFixedAssetsByCompanyId(companyId);
        Map<String, List<BigDecimal>> depreciationByAsset = new HashMap<>();

        for (FixedAsset asset : fixedAssets) {
            if (!asset.getAmortise()) continue;
            
            BigDecimal monthlyDepreciation = asset.getTotalCost()
                .divide(BigDecimal.valueOf(asset.getServiceLifeMonths()), RoundingMode.HALF_UP);
        
            LocalDate assetPurchaseDate = asset.getPurchaseDate();
            
            LocalDate depreciationStartDate = assetPurchaseDate.plusMonths(1).withDayOfMonth(1);
            
            LocalDate assetEndDate = depreciationStartDate.plusMonths(asset.getServiceLifeMonths() - 1);
        
            List<BigDecimal> monthlyDepreciationList = new ArrayList<>(Collections.nCopies(rangesFromCols.size(), BigDecimal.ZERO));
        
            BigDecimal totalDepreciation = BigDecimal.ZERO;
        
            for (int i = 0; i < rangesFromCols.size(); i++) {
                DateRangeInput monthRange = rangesFromCols.get(i);
        
                if (!(assetEndDate.isBefore(monthRange.getStartDate()) || depreciationStartDate.isAfter(monthRange.getEndDate()))) {
                    monthlyDepreciationList.set(i, monthlyDepreciationList.get(i).add(monthlyDepreciation));
                    totalDepreciation = totalDepreciation.add(monthlyDepreciation);
                    depreciationSum.set(i, depreciationSum.get(i).add(monthlyDepreciation));
                }
            }
            monthlyDepreciationList.add(totalDepreciation);
            depreciationByAsset.put(asset.getName(), monthlyDepreciationList);
            depreciationSum.set(depreciationSum.size() - 1, depreciationSum.get(depreciationSum.size() - 1).add(totalDepreciation));
        }

        List<String> depreciationNames = new ArrayList<String>();             
        List<List<BigDecimal>> depreciation = new ArrayList<List<BigDecimal>>();

        for (Map.Entry<String, List<BigDecimal>> entry : depreciationByAsset.entrySet()){
             depreciationNames.add(entry.getKey());
             depreciation.add(entry.getValue());
        }
        profitsLosses.setDepreciation(depreciation);
        profitsLosses.setDepreciationNames(depreciationNames);
        profitsLosses.setDepreciationSum(depreciationSum);
         //АМОРТИЗАЦИЯ -->          ##################################################################################################################

         //<-- ВЫВОД ПРИБЫЛИ ИЗ БИЗНЕСА: PL.[withdrawalOfProfitsSum], PLA.[withdrawalOfProfitsNames, withdrawalOfProfits]

        List<Operation> withdrawalOfProfitsOperations = operationsRepository.findWithdrawalOfProfits(companyId, dateRange.getStartDate(), dateRange.getEndDate());
        // List<Operation> withdrawalOfProfitsOperationsCopy = new ArrayList<Operation>(withdrawalOfProfitsOperations);
        
        HashSet<String> withdrawalOfProfitsArticles = new HashSet<String>();

        Iterator<Operation> withdrawalOfProfitsIterator = withdrawalOfProfitsOperations.iterator();

        while(withdrawalOfProfitsIterator.hasNext()){
            Operation tempOperation = withdrawalOfProfitsIterator.next();

            if(bankAccountIds != null){
                if(!bankAccountIds.contains(tempOperation.getBankAccount().getId())){
                    withdrawalOfProfitsIterator.remove();
                    continue;
                }
            }
            if(projectIds != null){
                if(tempOperation.getProject() != null){
                    if(!projectIds.contains(tempOperation.getProject().getId())){
                        withdrawalOfProfitsIterator.remove();
                        continue;
                    }
                }
            }

            String temp;
            if(tempOperation.getArticle() == null){
                temp = "Статья не указана";
            }
            else{
                temp = tempOperation.getArticle().getName();
            }
            withdrawalOfProfitsArticles.add(temp);
        }

         List<String> withdrawalOfProfitsNames = new ArrayList<String>();
         List<List<BigDecimal>> withdrawalOfProfits = new ArrayList<List<BigDecimal>>();

         for(String i : withdrawalOfProfitsArticles){
             withdrawalOfProfitsNames.add(i);
             List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
             BigDecimal sum = BigDecimal.ZERO;
             for(int j = 0; j < profitsLosses.getColumns().size();j++){
                 BigDecimal cellVal = BigDecimal.ZERO;
                 for(int k = 0; k < withdrawalOfProfitsOperations.size();k++){
                     Operation tempOperation = withdrawalOfProfitsOperations.get(k); 
                     if(tempOperation.getArticle() != null){
                         if(tempOperation.getArticle().getName().equals(i)){
                             if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                             && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                 cellVal = cellVal.add(tempOperation.getBalance());
                             }
                         }
                     }
                     else{
                         if(i.equals("Статья не указана")){
                             if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                             && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                 cellVal = cellVal.add(tempOperation.getBalance());
                             }
                         }
                     }    
                 }
                 cellVals.add(cellVal);
                 sum = sum.add(cellVal);
                 withdrawalOfProfitsSum.set(j, withdrawalOfProfitsSum.get(j).add(cellVal));
             }
             cellVals.add(sum);
             withdrawalOfProfits.add(cellVals);
             withdrawalOfProfitsSum.set(withdrawalOfProfitsSum.size() - 1, withdrawalOfProfitsSum.get(withdrawalOfProfitsSum.size() - 1).add(sum));
         }
         profitsLosses.setWithdrawalOfProfits(withdrawalOfProfits);
         profitsLosses.setWithdrawalOfProfitsNames(withdrawalOfProfitsNames);
         profitsLosses.setWithdrawalOfProfitsSum(withdrawalOfProfitsSum);

         //ВЫВОД ПРИБЫЛИ ИЗ БИЗНЕСА --> ##################################################################################################################
        
        
        switch(type){
            case "article":
            {
                ProfitsLossesArticles profitsLossesArticles = new ProfitsLossesArticles();
                //<-- ВЫРУЧКА [revenueSum, revenueNames, revenue] ARTICLE_CATEGORY = 1, ProjectType = project
                //List<Operation> allIncomeOperations = operationQueryResolver.

                //[   THIS BLOCK IS OLD METHOD   ] REVENUE

                // List<String> revenueProjectTypes = new ArrayList<String>();
                // revenueProjectTypes.add("PROJECT");
                // List<String> revenueOperationTypes = new ArrayList<String>();
                // revenueOperationTypes.add("INCOME");

                // List<Article> revenueArticles = articleQueryResolver.articles(companyId, userId, null, null, null, null, 1);
                
                // List<Long> revenueArticleIds = revenueArticles.stream()
                //                        .map(Article::getId)
                //                        .collect(Collectors.toList());

                // List<Project> revenueProjects = projectQueryResolver.projects(companyId, userId, revenueProjectTypes, null, null, null, null);
                 
                // List<Long> revenueProjectIds = revenueProjects.stream()
                //                         .map(Project::getId)
                //                         .collect(Collectors.toList());

                // List<Operation> revenueOperations = operationQueryResolver.operations(companyId, userId, dateRange, revenueArticleIds, null, null, null, revenueProjectIds, null, revenueOperationTypes, null, null, null);
                // revenueOperations.addAll(operationsRepository.findRevenue(companyId));

                //[   OLD METHOD END   ] REVENUE

                List<Operation> revenueOperations = operationsRepository.findRevenue(companyId, dateRange.getStartDate(), dateRange.getEndDate());

                HashSet<String> revenueArticleNames = new HashSet<String>();
                Iterator<Operation> iterator = revenueOperations.iterator();
                while (iterator.hasNext()) {
                    Operation tempOperation = iterator.next();

                    if (bankAccountIds != null && !bankAccountIds.contains(tempOperation.getBankAccount().getId())) {
                        iterator.remove();
                        continue;
                    }
                    if (projectIds != null && tempOperation.getProject() != null && !projectIds.contains(tempOperation.getProject().getId())) {
                        iterator.remove();
                        continue;
                    }

                    String temp;
                    if (tempOperation.getArticle() == null) {
                        temp = "Статья не указана";
                    } else {
                        temp = tempOperation.getArticle().getName();
                    }
                    revenueArticleNames.add(temp);
                }
                // HashSet<String> revenueArticleNames = revenueOperations.stream()
                //                         .map(operation -> operation.getArticle().getName())
                //                         .collect(Collectors.toCollection(HashSet::new)); //doesn't include "статья не указана"
                List<String> revenueNames = new ArrayList<String>();
                List<List<BigDecimal>> revenue = new ArrayList<List<BigDecimal>>();

                for(String i : revenueArticleNames){
                    revenueNames.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int j = 0; j < profitsLosses.getColumns().size();j++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int k = 0; k < revenueOperations.size();k++){
                            Operation tempOperation = revenueOperations.get(k); 
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Статья не указана")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }    
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                        revenueSum.set(j, revenueSum.get(j).add(cellVal));
                    }
                    cellVals.add(sum);
                    revenue.add(cellVals);
                    revenueSum.set(revenueSum.size() - 1, revenueSum.get(revenueSum.size() - 1).add(sum));
                }
                profitsLossesArticles.setRevenue(revenue);
                profitsLossesArticles.setRevenueNames(revenueNames);
                profitsLosses.setRevenueSum(revenueSum);
                
                //ВЫРУЧКА -->

                //<-- ПРЯМЫЕ РАСХОДЫ [directCostsSum]:  Article.Category = 1 or Article == null
                //    ПРЯМЫЕ ПЕРЕМЕННЫЕ [directCostsVariablesNames, directCostsVariables]: HAS Project.type = PROJECT/DEAL
                //    ПРЯМЫЕ ПОСТОЯННЫЕ [directCostsConstantsNames, directCostsConstants]: HAS ProjectDirection set
                // List<String> direcCostsOperationTypes = new ArrayList<String>();
                // direcCostsOperationTypes.add("EXPENSE");

                // List<Article> directCostsVariablesArticles = articleQueryResolver.articles(companyId, userId, null, null, direcCostsOperationTypes, null, 1);
                // List<Long> variablesArticleIds = directCostsVariablesArticles.stream()
                //                 .map(Article::getId)
                //                 .collect(Collectors.toList());
                
                // List<Project> directCostsVariablesProjects = projectQueryResolver.projects(companyId, userId, null, null, null, null, null);
                // List<Long> variablesProjectIds = directCostsVariablesProjects.stream()
                //                 .map(Project::getId)
                //                 .collect(Collectors.toList());
                
                // if(directCostsVariablesProjects == null || directCostsVariablesProjects.isEmpty()){
                //     //set DirectCostsVariables to empty
                // } else{


                // List<Operation> directCostsVariablesOperations = operationQueryResolver.operations(companyId, userId, dateRange, variablesArticleIds, null,null ,null ,variablesProjectIds ,null , direcCostsOperationTypes, null, null, null);
                // directCostsVariablesOperations.addAll(operationsRepository.findDirectCostsVariable(companyId));

                List<Operation> directCostsVariablesOperations = operationsRepository.findDirectCostsVariable(companyId, dateRange.getStartDate(), dateRange.getEndDate());
                // List<Operation> directCostsVariablesOperationsCopy = new ArrayList<Operation>(directCostsVariablesOperations);

                HashSet<String> variableArticleNames = new HashSet<String>();
                List<String> directCostsVariablesNames = new ArrayList<String>();

                Iterator<Operation> directCostsVariablesIterator = directCostsVariablesOperations.iterator();

                while(directCostsVariablesIterator.hasNext()){
                    Operation tempOperation = directCostsVariablesIterator.next();

                    if(bankAccountIds != null){
                        if(!bankAccountIds.contains(tempOperation.getBankAccount().getId())){
                            directCostsVariablesIterator.remove();
                            continue;
                        }
                    }
                    if(projectIds != null){
                        if(tempOperation.getProject() != null){
                            if(!projectIds.contains(tempOperation.getProject().getId())){
                                directCostsVariablesIterator.remove();
                                continue;
                            }
                        }
                    }
                    
                    String temp;
                    if(tempOperation.getArticle() == null){
                        temp = "Статья не указана";
                    }
                    else{
                        temp = tempOperation.getArticle().getName();
                    }
                    variableArticleNames.add(temp);
                }

                List<List<BigDecimal>> directCostsVariables = new ArrayList<List<BigDecimal>>();
                directCostsVariablesSum.add(BigDecimal.ZERO);

                for(String i : variableArticleNames){
                    directCostsVariablesNames.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int j = 0; j < profitsLosses.getColumns().size();j++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int k = 0; k < directCostsVariablesOperations.size();k++){
                            Operation tempOperation = directCostsVariablesOperations.get(k); 
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Статья не указана")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }    
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                        directCostsSum.set(j, directCostsSum.get(j).add(cellVal));
                        directCostsVariablesSum.set(j, directCostsVariablesSum.get(j).add(cellVal));
                    }
                    cellVals.add(sum);
                    directCostsVariables.add(cellVals);
                    directCostsSum.set(directCostsSum.size() - 1, directCostsSum.get(directCostsSum.size() - 1).add(sum));
                    directCostsVariablesSum.set(directCostsVariablesSum.size() - 1, directCostsVariablesSum.get(directCostsVariablesSum.size() - 1).add(sum));
                }
                profitsLossesArticles.setDirectCostsVariablesNames(directCostsVariablesNames);
                profitsLossesArticles.setDirectCostsVariables(directCostsVariables);
                profitsLosses.setDirectCostsVariablesSum(directCostsVariablesSum);
            

                List<Operation> directCostsConstantOperations = operationsRepository.findDirectCostsConstant(companyId, dateRange.getStartDate(), dateRange.getEndDate());
                // List<Operation> directCostsConstantOperationsCopy = new ArrayList<Operation>(directCostsConstantOperations);
                HashSet<String> constantArticleNames = new HashSet<String>();
                List<String> directCostsConstantNames = new ArrayList<String>();

                Iterator<Operation> directCostsConstantIterator = directCostsConstantOperations.iterator();

                while(directCostsConstantIterator.hasNext()){
                    Operation tempOperation = directCostsConstantIterator.next();

                    if(bankAccountIds != null){
                        if(!bankAccountIds.contains(tempOperation.getBankAccount().getId())){
                            directCostsConstantIterator.remove();
                            continue;
                        }
                    }
                    if(projectIds != null){
                        if(tempOperation.getProject() != null){
                            if(!projectIds.contains(tempOperation.getProject().getId())){
                                directCostsConstantIterator.remove();
                                continue;
                            }
                        }
                    }

                    String temp;
                    if(tempOperation.getArticle() == null){
                        temp = "Статья не указана";
                    }
                    else{
                        temp = tempOperation.getArticle().getName();
                    }
                    constantArticleNames.add(temp);
                }
                
                List<List<BigDecimal>> directCostsConstants = new ArrayList<List<BigDecimal>>();

                for(String i : constantArticleNames){
                    directCostsConstantNames.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int j = 0; j < profitsLosses.getColumns().size();j++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int k = 0; k < directCostsConstantOperations.size();k++){
                            Operation tempOperation = directCostsConstantOperations.get(k); 
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Статья не указана")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }    
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                        directCostsSum.set(j, directCostsSum.get(j).add(cellVal));
                        directCostsConstantsSum.set(j, directCostsConstantsSum.get(j).add(cellVal));
                    }
                    cellVals.add(sum);
                    directCostsConstants.add(cellVals);
                    directCostsSum.set(directCostsSum.size() - 1, directCostsSum.get(directCostsSum.size() - 1).add(sum));
                    directCostsConstantsSum.set(directCostsConstantsSum.size() - 1, directCostsConstantsSum.get(directCostsConstantsSum.size() - 1).add(sum));
                }
                profitsLossesArticles.setDirectCostsConstants(directCostsConstants);
                profitsLossesArticles.setDirectCostsConstantsNames(directCostsConstantNames);
                profitsLosses.setDirectCostsConstantsSum(directCostsConstantsSum);
                profitsLosses.setDirectCostsSum(directCostsSum);

                //ПРЯМЫЕ РАСХОДЫ --> ##################################################################################################################

                //<-- ВАЛОВАЯ ПРИБЫЛЬ

                for(int i = 0;i < revenueSum.size();i++){
                    grossProfit.set(i, revenueSum.get(i).subtract(directCostsSum.get(i)));
                }
                profitsLosses.setGrossProfit(grossProfit);

                //ВАЛОВАЯ ПРИБЫЛЬ --> ##################################################################################################################


                // <-- ОПЕРАЦИОННАЯ ПРИБЫЛЬ

                for(int i = 0; i < operatingProfits.size();i++){
                    operatingProfits.set(i, otherIncomeSum.get(i).add(grossProfit.get(i)).subtract(indirectCostsSum.get(i)));
                }
                profitsLosses.setOperatingProfits(operatingProfits);

                // ОПЕРАЦИОННАЯ ПРИБЫЛЬ--> ##################################################################################################################

                // <-- НАЛОГИ PL.[taxesSum] PLA.[taxes, taxesNames]

                List<Operation> taxOperations = operationsRepository.findTaxes(companyId, dateRange.getStartDate(), dateRange.getEndDate());
                // List<Operation> taxOperationsCopy = new ArrayList<Operation>(taxOperations);
                HashSet<String> taxArticles = new HashSet<String>();

                Iterator<Operation> taxOperationIterator = taxOperations.iterator();

                while(taxOperationIterator.hasNext()){
                    Operation tempOperation = taxOperationIterator.next();

                    if(bankAccountIds != null){
                        if(!bankAccountIds.contains(tempOperation.getBankAccount().getId())){
                            taxOperationIterator.remove();
                            continue;
                        }
                    }
                    if(projectIds != null){
                        if(tempOperation.getProject() != null){
                            if(!projectIds.contains(tempOperation.getProject().getId())){
                                taxOperationIterator.remove();
                                continue;
                            }
                        }
                    }

                    String temp;
                    if(tempOperation.getArticle() == null){
                        temp = "Статья не указана";
                    }
                    else{
                        temp = tempOperation.getArticle().getName();
                    }
                    taxArticles.add(temp);
                }

                List<String> taxesNames = new ArrayList<String>();
                List<List<BigDecimal>> taxes = new ArrayList<List<BigDecimal>>();

                for(String i : taxArticles){
                    taxesNames.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int j = 0; j < profitsLosses.getColumns().size();j++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int k = 0; k < taxOperations.size();k++){
                            Operation tempOperation = taxOperations.get(k); 
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Статья не указана")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }    
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                        taxesSum.set(j, taxesSum.get(j).add(cellVal));
                    }
                    cellVals.add(sum);
                    taxes.add(cellVals);
                    taxesSum.set(taxesSum.size() - 1, taxesSum.get(taxesSum.size() - 1).add(sum));
                }
                profitsLosses.setTaxesSum(taxesSum);
                profitsLossesArticles.setTaxes(taxes);
                profitsLossesArticles.setTaxesNames(taxesNames);
                // НАЛОГИ -->              ##################################################################################################################

                //<--ЧИСТАЯ ПРИБЫЛЬ = depreciationSum + taxesSum + operatingProfits

                for(int i = 0; i < netProfit.size();i++){
                    netProfit.set(i, operatingProfits.get(i).subtract(taxesSum.get(i)).subtract(depreciationSum.get(i)));
                }
                profitsLosses.setNetProfit(netProfit);
                //  ЧИСТАЯ ПРИБЫЛЬ-->      ##################################################################################################################
                
                
                
                //<-- НЕРАСПРЕДЕЛЕННАЯ ПРИБЫЛЬ retainedEarnings = netProfit + withdrawalOfProfitsSum

                for(int i = 0; i < retainedEarnings.size();i++){
                    retainedEarnings.set(i, netProfit.get(i).subtract(withdrawalOfProfitsSum.get(i)));
                }
                profitsLosses.setRetainedEarnings(retainedEarnings);
                //НЕРАСПРЕДЕЛЕННАЯ ПРИБЫЛЬ -->  ##################################################################################################################
                profitsLosses.setProfitsLossesArticles(profitsLossesArticles);
                return profitsLosses;
            }
            case "project":
            {
                List<ProfitsLossesProjects> profitsLossesProjects = new ArrayList<ProfitsLossesProjects>();

                List<Operation> projectRevenueOperations = operationsRepository.findProjectRevenue(companyId, dateRange.getStartDate(), dateRange.getEndDate());
                // List<Operation> projectRevenueOperationsCopy = new ArrayList<Operation>(projectRevenueOperations);
                List<Operation> projectDirectCostsVariablesOperations = operationsRepository.findProjectVariable(companyId, dateRange.getStartDate(), dateRange.getEndDate());
                // List<Operation> projectDirectCostsVariablesOperationsCopy = new ArrayList<Operation>(projectDirectCostsVariablesOperations);
                List<Operation> projectDirectCostsConstantsOperations = operationsRepository.findProjectConstant(companyId, dateRange.getStartDate(), dateRange.getEndDate());
                // List<Operation> projectDirectCostsConstantsOperationsCopy = new ArrayList<Operation>(projectDirectCostsConstantsOperations);
                //ВАЛОВАЯ ПРИБЫЛЬ   ##################DONE
                //List<Operation> projectOtherIncomeOperations = operationsRepository.findOtherIncome(companyId);
                //ОПЕРАЦИОННАЯ ПРИБЫЛЬ ##################DONE
                //List<Operation> projectIndirectCostsOperations = operationsRepository.findIndirectCosts(companyId);
                List<Operation> projectTaxesOperations = operationsRepository.findProjectTaxes(companyId, dateRange.getStartDate(), dateRange.getEndDate());
                // List<Operation> projectTaxesOperationsCopy = new ArrayList<Operation>(projectTaxesOperations);
                //АМОРТИЗАЦИЯ #######################DONE
                //ЧИСТАЯ ПРИБЫЛЬ    ##################DONE
                //ВЫВОД ДЕНЕГ ИЗ БИЗНЕСА ##################DONE
                //НЕРАСПРЕДЕЛЕННАЯ ПРИБЫЛЬ ##################DONE
                

                Map<String, PLProjectAttributes> projectAttributes = new HashMap<>(); 

                Iterator<Operation> projectRevenueOperationsIterator = projectRevenueOperations.iterator();
                while(projectRevenueOperationsIterator.hasNext()){
                    Operation tempOperation = projectRevenueOperationsIterator.next();
                    if(bankAccountIds != null){
                        if(!bankAccountIds.contains(tempOperation.getBankAccount().getId())){
                            projectRevenueOperationsIterator.remove();
                            continue;
                        }
                    }
                    if(projectIds != null){
                        if(tempOperation.getProject() != null){
                            if(!projectIds.contains(tempOperation.getProject().getId())){
                                projectRevenueOperationsIterator.remove();
                                continue;
                            }
                        }
                    }
                    

                    String name = tempOperation.getProject().getName();
                    if (!projectAttributes.containsKey(name)) {
                        PLProjectAttributes revenue = new PLProjectAttributes();
                        revenue.setRevenue(true);
                        projectAttributes.put(name,revenue);   
                    }
                }

                Iterator<Operation> projectDirectCostsVariablesOperationsIterator = projectDirectCostsVariablesOperations.iterator();
                while(projectDirectCostsVariablesOperationsIterator.hasNext()){
                    Operation i = projectDirectCostsVariablesOperationsIterator.next();

                    if(bankAccountIds != null){
                        if(!bankAccountIds.contains(i.getBankAccount().getId())){
                            projectDirectCostsVariablesOperationsIterator.remove();
                            continue;
                        }
                    }
                    if(projectIds != null){
                        if(i.getProject() != null){
                            if(!projectIds.contains(i.getProject().getId())){
                                projectDirectCostsVariablesOperationsIterator.remove();
                                continue;
                            }
                        }
                    }
                    
                    String name = i.getProject().getName();
                    if (projectAttributes.containsKey(name)) {
                        PLProjectAttributes projectAttribute = new PLProjectAttributes();
                        projectAttribute = projectAttributes.get(name);
                        projectAttribute.setDirectCostsVariables(true);
                        projectAttributes.put(name,projectAttribute);   
                    }
                    else{
                        PLProjectAttributes temp = new PLProjectAttributes();
                        temp.setDirectCostsVariables(true);
                        projectAttributes.put(name, temp);
                    }
                }

                Iterator<Operation> projectDirectCostsConstantsOperationsIterator = projectDirectCostsConstantsOperations.iterator(); 
                while(projectDirectCostsConstantsOperationsIterator.hasNext()){
                    Operation i = projectDirectCostsConstantsOperationsIterator.next();
                    if(bankAccountIds != null){
                        if(!bankAccountIds.contains(i.getBankAccount().getId())){
                            projectDirectCostsConstantsOperationsIterator.remove();
                            continue;
                        }
                    }
                    if(projectIds != null){
                        if(i.getProject() != null){
                            if(!projectIds.contains(i.getProject().getId())){
                                projectDirectCostsConstantsOperationsIterator.remove();
                                continue;
                            }
                        }
                    }

                    String name = ((ExpenseOperation)i).getProjectDirection().getName();
                    if (projectAttributes.containsKey(name)) {
                        PLProjectAttributes projectAttribute = new PLProjectAttributes();
                        projectAttribute = projectAttributes.get(name);
                        projectAttribute.setDirectCostsConstants(true);
                        projectAttributes.put(name,projectAttribute);   
                    }
                    else{
                        PLProjectAttributes temp = new PLProjectAttributes();
                        temp.setDirectCostsConstants(true);
                        projectAttributes.put(name, temp);
                    }
                }

                Iterator<Operation> projectTaxesOperationsIterator = projectTaxesOperations.iterator(); 
                while(projectTaxesOperationsIterator.hasNext()){
                    Operation i = projectTaxesOperationsIterator.next();
                    if(bankAccountIds != null){
                        if(!bankAccountIds.contains(i.getBankAccount().getId())){
                            projectTaxesOperationsIterator.remove();
                            continue;
                        }
                    }
                    if(projectIds != null){
                        if(i.getProject() != null){
                            if(!projectIds.contains(i.getProject().getId())){
                                projectTaxesOperationsIterator.remove();
                                continue;
                            }
                        }
                    }

                    String name = i.getProject().getName();
                    if (projectAttributes.containsKey(name)) {
                        PLProjectAttributes projectAttribute = new PLProjectAttributes();
                        projectAttribute = projectAttributes.get(name);
                        projectAttribute.setTaxes(true);
                        projectAttributes.put(name,projectAttribute);   
                    }
                    else{
                        PLProjectAttributes temp = new PLProjectAttributes();
                        temp.setTaxes(true);
                        projectAttributes.put(name, temp);
                    }
                }


                for(Map.Entry<String, PLProjectAttributes> entry: projectAttributes.entrySet()){
                    ProfitsLossesProjects currentProject = new ProfitsLossesProjects();
                    currentProject.setProjectName(entry.getKey());
                    PLProjectAttributes projectAttribute = entry.getValue();

                    List<BigDecimal> directCostsVariablesSumProject = new ArrayList<BigDecimal>(columnLengthEmpty);
                    List<BigDecimal> directCostsConstantsSumProject = new ArrayList<BigDecimal>(columnLengthEmpty);
                    List<BigDecimal> revenueSumProject = new ArrayList<BigDecimal>(grossProfit);

                    if(projectAttribute.getRevenue() != null && projectAttribute.getRevenue() == true){
                        List<String> revenueNames = new ArrayList<String>();
                        List<List<BigDecimal>> revenue = new ArrayList<List<BigDecimal>>();
                        HashSet<String> articleNames = new HashSet<String>();
                        for(Operation i: projectRevenueOperations){
                            String pName = i.getProject().getName();
                            if(pName.equals(entry.getKey())){
                                String articleName;
                                if(i.getArticle() != null){
                                    articleName = i.getArticle().getName();
                                }
                                else{
                                    articleName = "Статья не указана";
                                }
                                articleNames.add(articleName);
                            }
                        }
                        for(String articleName: articleNames){
                            revenueNames.add(articleName);
                            List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                            BigDecimal sum = BigDecimal.ZERO;
                            for(int j = 0;j < profitsLosses.getColumns().size();j++){
                                BigDecimal cellVal = BigDecimal.ZERO;
                                for(Operation tempOperation: projectRevenueOperations){
                                    if(tempOperation.getProject() != null && tempOperation.getProject().getName().equals(currentProject.getProjectName())){
                                        if(tempOperation.getArticle() != null){
                                            if(tempOperation.getArticle().getName().equals(articleName)){
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                            }
                                        }
                                        else{
                                            if(articleName.equals("Статья не указана")){
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                            }
                                        }
                                    }
                                }
                                cellVals.add(cellVal);
                                sum = sum.add(cellVal);
                                revenueSum.set(j, revenueSum.get(j).add(cellVal));
                                revenueSumProject.set(j,revenueSumProject.get(j).add(cellVal));
                            }
                            cellVals.add(sum);
                            revenue.add(cellVals);
                            revenueSum.set(revenueSum.size() - 1, revenueSum.get(revenueSum.size() - 1).add(sum));
                            revenueSumProject.set(revenueSumProject.size() - 1, revenueSumProject.get(revenueSumProject.size() - 1).add(sum));
                        }
                        currentProject.setRevenue(revenue);
                        currentProject.setRevenueNames(revenueNames);
                        currentProject.setRevenueSum(revenueSumProject);
                    }
                    if(projectAttribute.getDirectCostsVariables() != null && projectAttribute.getDirectCostsVariables() == true){
                        List<String> directCostsVariablesNames = new ArrayList<String>();
                        List<List<BigDecimal>> directCostsVariables = new ArrayList<List<BigDecimal>>();
                        HashSet<String> articleNames = new HashSet<String>();
                        for(Operation i: projectDirectCostsVariablesOperations){
                            String pName = i.getProject().getName();
                            if(pName.equals(entry.getKey())){
                                String articleName;
                                if(i.getArticle() != null){
                                    articleName = i.getArticle().getName();
                                }
                                else{
                                    articleName = "Статья не указана";
                                }
                                articleNames.add(articleName);
                            }
                        }
                        for(String articleName: articleNames){
                            directCostsVariablesNames.add(articleName);
                            List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                            BigDecimal sum = BigDecimal.ZERO;
                            for(int j = 0;j < profitsLosses.getColumns().size();j++){
                                BigDecimal cellVal = BigDecimal.ZERO;
                                for(Operation tempOperation: projectDirectCostsVariablesOperations){
                                    if(tempOperation.getProject() != null && tempOperation.getProject().getName().equals(currentProject.getProjectName())){
                                        if(tempOperation.getArticle() != null){
                                            if(tempOperation.getArticle().getName().equals(articleName)){
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                            }
                                        }
                                        else{
                                            if(articleName.equals("Статья не указана")){
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                            }
                                        }
                                    }
                                }
                                cellVals.add(cellVal);
                                sum = sum.add(cellVal);
                                directCostsSum.set(j, directCostsSum.get(j).add(cellVal));
                                directCostsVariablesSum.set(j, directCostsVariablesSum.get(j).add(cellVal));
                                directCostsVariablesSumProject.set(j, directCostsVariablesSumProject.get(j).add(cellVal));
                            }
                            cellVals.add(sum);
                            directCostsVariables.add(cellVals);
                            directCostsSum.set(directCostsSum.size() - 1, directCostsSum.get(directCostsSum.size() - 1).add(sum));
                            directCostsVariablesSum.set(directCostsVariablesSum.size() - 1, directCostsVariablesSum.get(directCostsVariablesSum.size() - 1).add(sum));
                            directCostsVariablesSumProject.set(directCostsVariablesSumProject.size() - 1, directCostsVariablesSumProject.get(directCostsVariablesSumProject.size() - 1).add(sum));
                        }
                        currentProject.setDirectCostsVariables(directCostsVariables);
                        currentProject.setDirectCostsVariablesNames(directCostsVariablesNames);
                        currentProject.setDirectCostsVariablesSum(directCostsVariablesSumProject);
                    }
                    if(projectAttribute.getDirectCostsConstants() != null && projectAttribute.getDirectCostsConstants() == true){
                        List<String> directCostsConstantsNames = new ArrayList<String>();
                        List<List<BigDecimal>> directCostsConstants = new ArrayList<List<BigDecimal>>();
                        HashSet<String> articleNames = new HashSet<String>();
                        for(Operation i: projectDirectCostsConstantsOperations){
                            String pName = ((ExpenseOperation)i).getProjectDirection().getName();
                            if(pName.equals(entry.getKey())){
                                String articleName;
                                if(i.getArticle() != null){
                                    articleName = i.getArticle().getName();
                                }
                                else{
                                    articleName = "Статья не указана";
                                }
                                articleNames.add(articleName);
                            }
                        }
                        for(String articleName: articleNames){
                            directCostsConstantsNames.add(articleName);
                            List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                            BigDecimal sum = BigDecimal.ZERO;
                            for(int j = 0;j < profitsLosses.getColumns().size();j++){
                                BigDecimal cellVal = BigDecimal.ZERO;
                                for(Operation tempOperation: projectDirectCostsConstantsOperations){
                                    if(((ExpenseOperation)tempOperation).getProjectDirection() != null && ((ExpenseOperation)tempOperation).getProjectDirection().getName().equals(currentProject.getProjectName())){
                                        if(tempOperation.getArticle() != null){
                                            if(tempOperation.getArticle().getName().equals(articleName)){
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                            }
                                        }
                                        else{
                                            if(articleName.equals("Статья не указана")){
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                            }
                                        }
                                    }
                                }
                                cellVals.add(cellVal);
                                sum = sum.add(cellVal);
                                directCostsSum.set(j, directCostsSum.get(j).add(cellVal));
                                directCostsConstantsSum.set(j, directCostsConstantsSum.get(j).add(cellVal));
                                directCostsVariablesSumProject.set(j, directCostsVariablesSumProject.get(j).add(cellVal));
                            }
                            cellVals.add(sum);
                            directCostsConstants.add(cellVals);
                            directCostsSum.set(directCostsSum.size() - 1, directCostsSum.get(directCostsSum.size() - 1).add(sum));
                            directCostsConstantsSum.set(directCostsConstantsSum.size() - 1, directCostsConstantsSum.get(directCostsConstantsSum.size() - 1).add(sum));
                            directCostsVariablesSumProject.set(directCostsVariablesSumProject.size() - 1, directCostsVariablesSumProject.get(directCostsVariablesSumProject.size() - 1).add(sum));
                        }
                        currentProject.setDirectCostsConstants(directCostsConstants);
                        currentProject.setDirectCostsConstantsNames(directCostsConstantsNames);
                        currentProject.setDirectCostsConstantsSum(directCostsConstantsSumProject);
                    }
                    if(projectAttribute.getTaxes() != null && projectAttribute.getTaxes() == true){
                        List<String> taxesNames = new ArrayList<String>();
                        List<List<BigDecimal>> taxes = new ArrayList<List<BigDecimal>>();
                        HashSet<String> articleNames = new HashSet<String>();
                        for(Operation i: projectTaxesOperations){
                            String pName = i.getProject().getName();
                            if(pName.equals(entry.getKey())){
                                String articleName;
                                if(i.getArticle() != null){
                                    articleName = i.getArticle().getName();
                                }
                                else{
                                    articleName = "Статья не указана";
                                }
                                articleNames.add(articleName);
                            }
                        }
                        for(String articleName: articleNames){
                            taxesNames.add(articleName);
                            List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                            BigDecimal sum = BigDecimal.ZERO;
                            for(int j = 0;j < profitsLosses.getColumns().size();j++){
                                BigDecimal cellVal = BigDecimal.ZERO;
                                for(Operation tempOperation: projectTaxesOperations){
                                    if(tempOperation.getProject() != null && tempOperation.getProject().getName().equals(currentProject.getProjectName())){
                                        if(tempOperation.getArticle() != null){
                                            if(tempOperation.getArticle().getName().equals(articleName)){
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                            }
                                        }
                                        else{
                                            if(articleName.equals("Статья не указана")){
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                            }
                                        }
                                    }
                                }
                                cellVals.add(cellVal);
                                sum = sum.add(cellVal);
                                taxesSum.set(j, taxesSum.get(j).add(cellVal));
                            }
                            cellVals.add(sum);
                            taxes.add(cellVals);
                            taxesSum.set(taxesSum.size() - 1, taxesSum.get(taxesSum.size() - 1).add(sum));
                        }
                        currentProject.setTaxes(taxes);
                        currentProject.setTaxesNames(taxesNames);

                    }
                    List<BigDecimal> grossProfitProject = new ArrayList<BigDecimal>(columnLengthEmpty);

                    for(int i = 0;i < grossProfitProject.size();i++){
                        grossProfitProject.set(i, revenueSumProject.get(i).subtract(directCostsConstantsSumProject.get(i)).subtract(directCostsVariablesSumProject.get(i)));
                    }
                    currentProject.setGrossProfit(grossProfitProject);

                    profitsLossesProjects.add(currentProject);
                }
                profitsLosses.setRevenueSum(revenueSum);
                profitsLosses.setDirectCostsSum(directCostsSum);
                profitsLosses.setTaxesSum(taxesSum);
                profitsLosses.setDirectCostsVariablesSum(directCostsVariablesSum);
                profitsLosses.setDirectCostsConstantsSum(directCostsConstantsSum);
                

                //ВАЛОВАЯ ПРИБЫЛЬ
                for(int i = 0;i < revenueSum.size();i++){
                    grossProfit.set(i, revenueSum.get(i).subtract(directCostsSum.get(i)));
                }
                profitsLosses.setGrossProfit(grossProfit);


                //ОПЕРАЦИОННАЯ ПРИБЫЛЬ
                for(int i = 0; i < operatingProfits.size();i++){
                    operatingProfits.set(i, otherIncomeSum.get(i).add(grossProfit.get(i)).subtract(indirectCostsSum.get(i)));
                }
                profitsLosses.setOperatingProfits(operatingProfits);
                //ЧИСТАЯ ПРИБЫЛЬ

                for(int i = 0; i < netProfit.size();i++){
                    netProfit.set(i, operatingProfits.get(i).subtract(taxesSum.get(i)).subtract(depreciationSum.get(i)));
                }
                profitsLosses.setNetProfit(netProfit);

                //Нераспределенная прибыль
                for(int i = 0; i < retainedEarnings.size();i++){
                    retainedEarnings.set(i, netProfit.get(i).subtract(withdrawalOfProfitsSum.get(i)));
                }
                profitsLosses.setRetainedEarnings(retainedEarnings);
                
                profitsLosses.setProfitsLossesProjects(profitsLossesProjects);

                return profitsLosses;
            }
            default: 
            {
                throw new IllegalArgumentException("Invalid type.");
            }
        }
    }

    // private ProfitsLosses emptyArticle(){

    // }

    private List<String> columns(DateRangeInput dateRange){
        List<String> cols = new ArrayList<String>();
        LocalDate startDate = dateRange.getStartDate();
        LocalDate endDate = dateRange.getEndDate();
        
        while (!startDate.isAfter(endDate)) {
            cols.add(startDate.getMonth().toString() + " " + startDate.getYear());
            startDate = startDate.plusMonths(1);
        }
        return cols;
    }

    private List<DateRangeInput> datesFromCols(DateRangeInput dateRange){
        List<DateRangeInput> out = new ArrayList<DateRangeInput>();
        LocalDate startDate = dateRange.getStartDate();
        LocalDate endDate = dateRange.getEndDate();

        while (!startDate.isAfter(endDate)) {
            LocalDate endOfMonth = startDate;
            endOfMonth = endOfMonth.plusDays((startDate.lengthOfMonth() - startDate.getDayOfMonth()));
            if(endOfMonth.isAfter(endDate)){
                endOfMonth = endDate;
            }
            out.add(
                new DateRangeInput(
                    startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    endOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
            );
            startDate = endOfMonth.plusDays(1);
        }
        return out;
    }

    // private List<BigDecimal> getOrDefault(List<BigDecimal> list, List<BigDecimal> defaultList) {
    //     return list != null ? list : new ArrayList<>(defaultList);
    // }
}