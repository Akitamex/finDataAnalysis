package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.*;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.models.CashFlow;
import com.ubm.ubmweb.models.CashFlowByActivity;
import com.ubm.ubmweb.models.CashFlowProjectData;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CashFlowQueryResolver implements GraphQLQueryResolver{
    
    private final OperationQueryResolver operationQueryResolver;

    private final BankAccountQueryResolver bankAccountQueryResolver;

    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    
    public CashFlow cashFlow(Long companyId, Long userId, String type, String timeframe, String grouping, List<Long> bankAccountIds, List<Long> projectIds){

        Boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to the company with id:" + companyId);
        }

        CashFlow cashFlow = new CashFlow();
        cashFlow.setType(type);
        cashFlow.setTimeframe(timeframe);
        cashFlow.setGrouping(grouping);
        cashFlow.setBankAccountIds(bankAccountIds);
        cashFlow.setProjectIds(projectIds);
        String first;
        DateRangeInput dateRange = new DateRangeInput("","");
        dateRange.parseTimeFrame(timeframe);
        cashFlow.setColumns(columns(grouping, dateRange));
        List<String> rows = new ArrayList<String>();
        List<String> initialBalanceRows = new ArrayList<String>();
        List<List<BigDecimal>> initialBalance = new ArrayList<List<BigDecimal>>();
        List<List<BigDecimal>> finalBalance = new ArrayList<List<BigDecimal>>();
        List<List<BigDecimal>> income = new ArrayList<List<BigDecimal>>();
        List<String> incomeRows = new ArrayList<String>();
        List<List<BigDecimal>> expense = new ArrayList<List<BigDecimal>>();
        List<String> expenseRows = new ArrayList<String>();
        List<DateRangeInput> rangesFromCols = new ArrayList<DateRangeInput>();
        rangesFromCols = datesFromCols(dateRange, grouping);
        List<Operation> incomeOperations = operationQueryResolver.operations(companyId, userId, dateRange, Collections.emptyList(), 
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), projectIds,
                Collections.emptyList(), List.of("INCOME"), Collections.emptyList(), bankAccountIds, null);
        List<Operation> expenseOperations = operationQueryResolver.operations(companyId, userId, dateRange, Collections.emptyList(), 
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), projectIds,
                Collections.emptyList(), List.of("EXPENSE"), Collections.emptyList(), bankAccountIds, null);
        List<Operation> transferOperations = operationQueryResolver.operations(companyId, userId, null, Collections.emptyList(), 
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), projectIds,
                Collections.emptyList(), List.of("TRANSFER"), Collections.emptyList(), null, null);
        List<Operation> inOperations = operationQueryResolver.operations(companyId, userId, null, Collections.emptyList(), 
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), projectIds,
                Collections.emptyList(), List.of("INCOME"), Collections.emptyList(), bankAccountIds, null);
        List<Operation> exOperations = operationQueryResolver.operations(companyId, userId, null, Collections.emptyList(), 
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), projectIds,
                Collections.emptyList(), List.of("EXPENSE"), Collections.emptyList(), bankAccountIds, null);
        
        List<Operation> allOperations = new ArrayList<Operation>();
        allOperations.addAll(inOperations);
        allOperations.addAll(exOperations);
        allOperations.addAll(transferOperations);
        List<String> transfer = new ArrayList<String>();
        transfer.add("Зачисления");
        transfer.add("Списания");
        cashFlow.setTransfer(transfer);
        List<BigDecimal> transferIn = new ArrayList<BigDecimal>();
        List<BigDecimal> transferOut = new ArrayList<BigDecimal>();
        List<BigDecimal> transferSum = new ArrayList<BigDecimal>();
        List<BigDecimal> saldo = new ArrayList<BigDecimal>();
        List<BigDecimal> incomeSum = new ArrayList<BigDecimal>();
        List<BigDecimal> expenseSum = new ArrayList<BigDecimal>();
        HashSet<String> projectNames = new HashSet<String>();

        List<BankAccount> bankAccounts = bankAccountQueryResolver.bankAccounts(companyId, userId, null, null, null, null, null, null, null, Collections.emptyList(),bankAccountIds);
        
        for(int i = 0;i < bankAccounts.size();i++){
            initialBalanceRows.add(bankAccounts.get(i).getName());
        }
        cashFlow.setInitialBalanceRows(initialBalanceRows);
        cashFlow.setFinalBalanceRows(initialBalanceRows);

        List<BigDecimal> beginning = new ArrayList<BigDecimal>();
        List<BigDecimal> ending = new ArrayList<BigDecimal>();
        for(int i = 0;i <= cashFlow.getColumns().size();i++){
            transferIn.add(BigDecimal.ZERO);
            transferOut.add(BigDecimal.ZERO);
            beginning.add(BigDecimal.ZERO);
            ending.add(BigDecimal.ZERO);
        }
        

        for(String i : initialBalanceRows){
            List<BigDecimal> cellValsInit = new ArrayList<BigDecimal>();
            List<BigDecimal> cellValsFin = new ArrayList<BigDecimal>();

            for(int j = 0; j < cashFlow.getColumns().size();j++){
                BigDecimal cellValInit = BigDecimal.ZERO;
                BigDecimal cellValFin = BigDecimal.ZERO;
                for(int k = 0; k < allOperations.size();k++){
                    Operation tempOperation = allOperations.get(k);
                    if(!tempOperation.getDate().isBefore(dateRange.getStartDate())){
                        if(tempOperation.getProject() != null){
                        projectNames.add(tempOperation.getProject().getName());
                        }
                        else{
                            projectNames.add("Без проекта");
                        }
                    }
                    if(tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate())){
                        if(tempOperation.getType().equals("TRANSFER")){
                            if(((TransferOperation)tempOperation).getToBankAccount().getName().equals(i)){
                                cellValInit = cellValInit.add(tempOperation.getBalance());
                                cellValFin = cellValFin.add(tempOperation.getBalance());
                            }
                            if(((TransferOperation)tempOperation).getFromBankAccount().getName().equals(i)){
                                cellValInit = cellValInit.subtract(tempOperation.getBalance());
                                cellValFin = cellValFin.subtract(tempOperation.getBalance());
                            }
                        }
                        else if(tempOperation.getBankAccount().getName().equals(i)){
                            if(tempOperation.getType().equals("INCOME")){
                                cellValInit = cellValInit.add(tempOperation.getBalance());
                                cellValFin = cellValFin.add(tempOperation.getBalance());
                            }
                            else if(tempOperation.getType().equals("EXPENSE")){
                                cellValInit = cellValInit.subtract(tempOperation.getBalance());
                                cellValFin = cellValFin.subtract(tempOperation.getBalance());
                            }
                        }
                    }
                    else if(!tempOperation.getDate().isBefore(rangesFromCols.get(j).getStartDate())
                         && !tempOperation.getDate().isAfter(rangesFromCols.get(j).getEndDate())){
                            if(tempOperation.getType().equals("TRANSFER")){
                                if(((TransferOperation)tempOperation).getToBankAccount().getName().equals(i)){
                                    cellValFin = cellValFin.add(tempOperation.getBalance());
                                    transferIn.set(j, transferIn.get(j).add(tempOperation.getBalance()));
                                    transferIn.set(cashFlow.getColumns().size(), transferIn.get(cashFlow.getColumns().size()).add(tempOperation.getBalance()));
                                }
                                if(((TransferOperation)tempOperation).getFromBankAccount().getName().equals(i)){
                                    cellValFin = cellValFin.subtract(tempOperation.getBalance());
                                    transferOut.set(j, transferOut.get(j).subtract(tempOperation.getBalance()));
                                    transferOut.set(cashFlow.getColumns().size(), transferOut.get(cashFlow.getColumns().size()).subtract(tempOperation.getBalance()));
                                }
                            }
                            else if(tempOperation.getBankAccount().getName().equals(i)){
                                if(tempOperation.getType().equals("INCOME")){
                                    cellValFin = cellValFin.add(tempOperation.getBalance());
                                }
                                else if(tempOperation.getType().equals("EXPENSE")){
                                    cellValFin = cellValFin.subtract(tempOperation.getBalance());
                                }
                            }
                        }
                }
                cellValsInit.add(cellValInit);
                cellValsFin.add(cellValFin);
                beginning.set(j, beginning.get(j).add(cellValInit));
                ending.set(j, ending.get(j).add(cellValFin));
            }
            cellValsInit.add(cellValsInit.get(0));
            cellValsFin.add(cellValsFin.get(cellValsFin.size() - 1));
            initialBalance.add(cellValsInit);
            finalBalance.add(cellValsFin);
            
            beginning.set(cashFlow.getColumns().size(), beginning.get(cashFlow.getColumns().size()).add(cellValsInit.get(0)));
            ending.set(cashFlow.getColumns().size(), ending.get(cashFlow.getColumns().size()).add(cellValsFin.get(cellValsFin.size() - 1)));
        }
        cashFlow.setTransferIn(transferIn);
        cashFlow.setTransferOut(transferOut);
        
        cashFlow.setInitialBalance(initialBalance);
        cashFlow.setFinalBalance(finalBalance);
        cashFlow.setInitialBalanceSum(beginning);
        cashFlow.setFinalBalanceSum(ending);
        
        switch(type){
            case "article":
            {
                first = "СТАТЬЯ";
                cashFlow.setFirst(first);

                rows.add("Деньги на начало периода");
                rows.add("Поступления");
                rows.add("Выплаты");
                rows.add("Переводы между счетами");
                rows.add("Сальдо");
                rows.add("Деньги на конец периода");
                cashFlow.setRows(rows);

                HashSet<String> articlesIncome = new HashSet<String>();
                for(int i = 0;i < incomeOperations.size();i++){
                    Operation tempOperation = incomeOperations.get(i);
                    String temp;
                    if(tempOperation.getArticle() == null){
                        temp = "Статья не указана";
                    }
                    else{
                        temp = tempOperation.getArticle().getName();
                    }
                    articlesIncome.add(temp);
                }

                HashSet<String> articlesExpense = new HashSet<String>();
                for(int i = 0;i < expenseOperations.size();i++){
                    Operation tempOperation = expenseOperations.get(i);
                    String temp;
                    if(tempOperation.getArticle() == null){
                        temp = "Статья не указана";
                    }
                    else{
                        temp = tempOperation.getArticle().getName();
                    }
                    articlesExpense.add(temp);
                }
        

                for(String i : articlesIncome){// Поступления
                    incomeRows.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < incomeOperations.size();j++){
                            Operation tempOperation = incomeOperations.get(j);
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Статья не указана")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    income.add(cellVals);
                }
                cashFlow.setIncomeRows(incomeRows);
                cashFlow.setIncome(income);

                for(String i : articlesExpense){// Выплаты
                    expenseRows.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < expenseOperations.size();j++){
                            Operation tempOperation = expenseOperations.get(j);
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Статья не указана")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    expense.add(cellVals);
                }
                cashFlow.setExpenseRows(expenseRows);
                cashFlow.setExpense(expense);

                BigDecimal saldoTotal = BigDecimal.ZERO;
                BigDecimal incomeTotal = BigDecimal.ZERO;
                BigDecimal expenseTotal = BigDecimal.ZERO;
                for(int i = 0;i < cashFlow.getColumns().size();i++){
                    BigDecimal sumIn = BigDecimal.ZERO;
                    BigDecimal sumEx = BigDecimal.ZERO;
                    transferSum.add(transferIn.get(i).add(transferOut.get(i)));
                    for(int j = 0;j < incomeRows.size();j++){
                        sumIn = sumIn.add(income.get(j).get(i));
                    }
                    for(int j = 0;j < expenseRows.size();j++){
                        sumEx = sumEx.add(expense.get(j).get(i));
                    }
                    incomeSum.add(sumIn);
                    expenseSum.add(sumEx);
                    incomeTotal = incomeTotal.add(sumIn);
                    expenseTotal = expenseTotal.add(sumEx);
                    BigDecimal sum = sumIn.add(sumEx).add(transferIn.get(i)).add(transferOut.get(i));
                    saldo.add(sum);
                    saldoTotal = saldoTotal.add(sum);
                }
                transferSum.add(transferIn.get(cashFlow.getColumns().size()).add(transferOut.get(cashFlow.getColumns().size())));
                incomeSum.add(incomeTotal);
                expenseSum.add(expenseTotal);
                saldo.add(saldoTotal);
                cashFlow.setIncomeSum(incomeSum);
                cashFlow.setExpenseSum(expenseSum);
                cashFlow.setTransferSum(transferSum);
                cashFlow.setSaldo(saldo);

                return cashFlow;
            }
            case "activity":
            {
                first = "ВИД ДЕЯТЕЛЬНОСТИ";
                CashFlowByActivity activity = new CashFlowByActivity();
                cashFlow.setFirst(first);
                rows.add("Деньги на начало периода");
                rows.add("Операционная");

                HashSet<String> articlesIncomeOne = new HashSet<String>();
                HashSet<String> articlesIncomeTwo = new HashSet<String>();
                HashSet<String> articlesIncomeThree = new HashSet<String>();
                List<BigDecimal> firstIncomeSum = new ArrayList<BigDecimal>();
                List<BigDecimal> secondIncomeSum = new ArrayList<BigDecimal>();
                List<BigDecimal> thirdIncomeSum = new ArrayList<BigDecimal>();
                List<BigDecimal> firstExpenseSum = new ArrayList<BigDecimal>();
                List<BigDecimal> secondExpenseSum = new ArrayList<BigDecimal>();
                List<BigDecimal> thirdExpenseSum = new ArrayList<BigDecimal>();
                


                for(int i = 0;i <= cashFlow.getColumns().size();i++){
                    firstIncomeSum.add(BigDecimal.ZERO);
                    firstExpenseSum.add(BigDecimal.ZERO);
                    secondIncomeSum.add(BigDecimal.ZERO);
                    secondExpenseSum.add(BigDecimal.ZERO);
                    thirdIncomeSum.add(BigDecimal.ZERO);
                    thirdExpenseSum.add(BigDecimal.ZERO);
                }
                
                for(int i = 0;i < incomeOperations.size();i++){
                    Operation tempOperation = incomeOperations.get(i);
                    String temp;
                    if(tempOperation.getArticle() == null){
                        temp = "Статья не указана";
                        articlesIncomeOne.add(temp);
                    }
                    else{
                        if(tempOperation.getArticle().getCashFlowType() == 1){
                            temp = tempOperation.getArticle().getName();
                            articlesIncomeOne.add(temp);
                        }
                        if(tempOperation.getArticle().getCashFlowType() == 2){
                            temp = tempOperation.getArticle().getName();
                            articlesIncomeTwo.add(temp);
                        }
                        if(tempOperation.getArticle().getCashFlowType() == 3){
                            temp = tempOperation.getArticle().getName();
                            articlesIncomeThree.add(temp);
                        }
                    }
                }

                HashSet<String> articlesExpenseOne = new HashSet<String>();
                HashSet<String> articlesExpenseTwo = new HashSet<String>();
                HashSet<String> articlesExpenseThree = new HashSet<String>();
                
                for(int i = 0;i < expenseOperations.size();i++){
                    Operation tempOperation = expenseOperations.get(i);
                    String temp;
                    if(tempOperation.getArticle() == null){
                        temp = "Статья не указана";
                        articlesExpenseOne.add(temp);
                    }
                    else{
                        if(tempOperation.getArticle().getCashFlowType() == 1){
                            temp = tempOperation.getArticle().getName();
                            articlesExpenseOne.add(temp);
                        }
                        if(tempOperation.getArticle().getCashFlowType() == 2){
                            temp = tempOperation.getArticle().getName();
                            articlesExpenseTwo.add(temp);
                        }
                        if(tempOperation.getArticle().getCashFlowType() == 3){
                            temp = tempOperation.getArticle().getName();
                            articlesExpenseThree.add(temp);
                        }
                    }
                }

                rows.add("Инвестиционная");
                rows.add("Финансовая");
                rows.add("Сальдо");
                rows.add("Деньги на конец периода");

                cashFlow.setRows(rows);

                List<String> firstIncomeArticles = new ArrayList<String>(articlesIncomeOne);
                List<String> firstExpenseArticles = new ArrayList<String>(articlesExpenseOne);
                List<String> secondIncomeArticles = new ArrayList<String>(articlesIncomeTwo);
                List<String> secondExpenseArticles = new ArrayList<String>(articlesExpenseTwo);
                List<String> thirdIncomeArticles = new ArrayList<String>(articlesIncomeThree);
                List<String> thirdExpenseArticles = new ArrayList<String>(articlesExpenseThree);

                List<List<BigDecimal>> firstIncome = new ArrayList<List<BigDecimal>>();
                List<List<BigDecimal>> firstExpense = new ArrayList<List<BigDecimal>>();
                List<List<BigDecimal>> secondIncome = new ArrayList<List<BigDecimal>>();
                List<List<BigDecimal>> secondExpense = new ArrayList<List<BigDecimal>>();
                List<List<BigDecimal>> thirdIncome = new ArrayList<List<BigDecimal>>();
                List<List<BigDecimal>> thirdExpense = new ArrayList<List<BigDecimal>>();
                
                
                activity.setFirstIncomeArticles(firstIncomeArticles);
                activity.setFirstExpenseArticles(firstExpenseArticles);
                activity.setSecondIncomeArticles(secondIncomeArticles);
                activity.setSecondExpenseArticles(secondExpenseArticles);
                activity.setThirdIncomeArticles(thirdIncomeArticles);
                activity.setThirdExpenseArticles(thirdExpenseArticles);

                for(String i : firstIncomeArticles){
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < incomeOperations.size();j++){
                            Operation tempOperation = incomeOperations.get(j);
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Статья не указана")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    firstIncome.add(cellVals);
                }
                for(String i : firstExpenseArticles){
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < expenseOperations.size();j++){
                            Operation tempOperation = expenseOperations.get(j);
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Статья не указана")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    firstExpense.add(cellVals);
                }
                for(String i : secondIncomeArticles){
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < incomeOperations.size();j++){
                            Operation tempOperation = incomeOperations.get(j);
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    secondIncome.add(cellVals);
                }
                for(String i : secondExpenseArticles){
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < expenseOperations.size();j++){
                            Operation tempOperation = expenseOperations.get(j);
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    secondExpense.add(cellVals);
                }
                for(String i : thirdIncomeArticles){
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < incomeOperations.size();j++){
                            Operation tempOperation = incomeOperations.get(j);
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    thirdIncome.add(cellVals);
                }
                for(String i : thirdExpenseArticles){
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < expenseOperations.size();j++){
                            Operation tempOperation = expenseOperations.get(j);
                            if(tempOperation.getArticle() != null){
                                if(tempOperation.getArticle().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    thirdExpense.add(cellVals);
                }
                List<BigDecimal> firstSum = new ArrayList<BigDecimal>();
                List<BigDecimal> secondSum = new ArrayList<BigDecimal>();
                List<BigDecimal> thirdSum = new ArrayList<BigDecimal>();
                
                for(int i = 0;i <= cashFlow.getColumns().size();i++){
                    for(int j = 0; j < firstIncome.size();j++){
                        firstIncomeSum.set(i, firstIncomeSum.get(i).add(firstIncome.get(j).get(i)));
                    }
                    for(int j = 0; j < firstExpense.size();j++){
                        firstExpenseSum.set(i, firstExpenseSum.get(i).add(firstExpense.get(j).get(i)));
                    }
                    for(int j = 0; j < secondIncome.size();j++){
                        secondIncomeSum.set(i, secondIncomeSum.get(i).add(secondIncome.get(j).get(i)));
                    }
                    for(int j = 0; j < secondExpense.size();j++){
                        secondExpenseSum.set(i, secondExpenseSum.get(i).add(secondExpense.get(j).get(i)));
                    }
                    for(int j = 0; j < thirdIncome.size();j++){
                        thirdIncomeSum.set(i, thirdIncomeSum.get(i).add(thirdIncome.get(j).get(i)));
                    }
                    for(int j = 0; j < thirdExpense.size();j++){
                        thirdExpenseSum.set(i, thirdExpenseSum.get(i).add(thirdExpense.get(j).get(i)));
                    }
                    saldo.add(firstIncomeSum.get(i).add(firstExpenseSum.get(i)).add(secondIncomeSum.get(i)).add(secondExpenseSum.get(i)).add(thirdIncomeSum.get(i)).add(thirdExpenseSum.get(i)));
                    firstSum.add(firstIncomeSum.get(i).add(firstExpenseSum.get(i)));
                    secondSum.add(secondIncomeSum.get(i).add(secondExpenseSum.get(i)));
                    thirdSum.add(thirdIncomeSum.get(i).add(thirdExpenseSum.get(i)));
                }
                cashFlow.setSaldo(saldo);
                activity.setFirstSum(firstSum);
                activity.setSecondSum(secondSum);
                activity.setThirdSum(thirdSum);
                activity.setFirstIncomeSum(firstIncomeSum);
                activity.setFirstExpenseSum(firstExpenseSum);
                activity.setSecondIncomeSum(secondIncomeSum);
                activity.setSecondExpenseSum(secondExpenseSum);
                activity.setThirdIncomeSum(thirdIncomeSum);
                activity.setThirdExpenseSum(thirdExpenseSum);
                
                activity.setFirstIncome(firstIncome);
                activity.setFirstExpense(firstExpense);
                activity.setSecondIncome(secondIncome);
                activity.setSecondExpense(secondExpense);
                activity.setThirdIncome(thirdIncome);
                activity.setThirdExpense(thirdExpense);
                cashFlow.setActivityData(activity);

                return cashFlow;
            }
            case "bank":
            {
                first = "СЧЕТ";
                cashFlow.setFirst(first);


                rows.add("Деньги на начало периода");
                rows.add("Поступления");
                rows.add("Выплаты");
                rows.add("Переводы между счетами");
                rows.add("Сальдо");
                rows.add("Деньги на конец периода");
                cashFlow.setRows(rows);

                HashSet<String> banksIncome = new HashSet<String>();
                for(int i = 0;i < incomeOperations.size();i++){
                    String temp = incomeOperations.get(i).getBankAccount().getName();
                    banksIncome.add(temp);
                }

                HashSet<String> banksExpense = new HashSet<String>();
                for(int i = 0;i < expenseOperations.size();i++){
                    String temp = expenseOperations.get(i).getBankAccount().getName();
                    banksExpense.add(temp);
                }

                for(String i : banksIncome){// Поступления
                    incomeRows.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < incomeOperations.size();j++){
                            Operation tempOperation = incomeOperations.get(j);
                            if(tempOperation.getBankAccount().getName().equals(i)){
                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                    cellVal = cellVal.add(tempOperation.getBalance());
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    income.add(cellVals);
                }
                cashFlow.setIncomeRows(incomeRows);
                cashFlow.setIncome(income);

                for(String i : banksExpense){// Выплаты
                    expenseRows.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < expenseOperations.size();j++){
                            Operation tempOperation = expenseOperations.get(j);
                                if(tempOperation.getBankAccount().getName().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    expense.add(cellVals);
                }
                cashFlow.setExpenseRows(expenseRows);
                cashFlow.setExpense(expense);


                BigDecimal saldoTotal = BigDecimal.ZERO;
                BigDecimal incomeTotal = BigDecimal.ZERO;
                BigDecimal expenseTotal = BigDecimal.ZERO;
                for(int i = 0;i < cashFlow.getColumns().size();i++){
                    BigDecimal sumIn = BigDecimal.ZERO;
                    BigDecimal sumEx = BigDecimal.ZERO;
                    transferSum.add(transferIn.get(i).add(transferOut.get(i)));
                    for(int j = 0;j < incomeRows.size();j++){
                        sumIn = sumIn.add(income.get(j).get(i));
                    }
                    for(int j = 0;j < expenseRows.size();j++){
                        sumEx = sumEx.add(expense.get(j).get(i));
                    }
                    incomeSum.add(sumIn);
                    expenseSum.add(sumEx);
                    incomeTotal = incomeTotal.add(sumIn);
                    expenseTotal = expenseTotal.add(sumEx);
                    BigDecimal sum = sumIn.add(sumEx).add(transferIn.get(i)).add(transferOut.get(i));
                    saldo.add(sum);
                    saldoTotal = saldoTotal.add(sum);
                }
                transferSum.add(transferIn.get(cashFlow.getColumns().size()).add(transferOut.get(cashFlow.getColumns().size())));
                incomeSum.add(incomeTotal);
                expenseSum.add(expenseTotal);
                saldo.add(saldoTotal);
                cashFlow.setIncomeSum(incomeSum);
                cashFlow.setExpenseSum(expenseSum);
                cashFlow.setTransferSum(transferSum);
                cashFlow.setSaldo(saldo);

                return cashFlow;
            }
            case "counterparty":
            {
                first = "КОНТРАГЕНТ";
                cashFlow.setFirst(first);

                rows.add("Деньги на начало периода");
                rows.add("Поступления");
                rows.add("Выплаты");
                rows.add("Переводы между счетами");
                rows.add("Сальдо");
                rows.add("Деньги на конец периода");
                cashFlow.setRows(rows);

                HashSet<String> counterpartiesIncome = new HashSet<String>();
                for(int i = 0;i < incomeOperations.size();i++){
                    Operation tempOperation = incomeOperations.get(i);
                    String temp;
                    if(tempOperation.getCounterparty() == null){
                        temp = "Контрагент не указан";
                    }
                    else{
                        temp = tempOperation.getCounterparty().getTitle();
                    }
                    counterpartiesIncome.add(temp);
                }

                HashSet<String> counterpartiesExpense = new HashSet<String>();
                for(int i = 0;i < expenseOperations.size();i++){
                    Operation tempOperation = expenseOperations.get(i);
                    String temp;
                    if(tempOperation.getCounterparty() == null){
                        temp = "Контрагент не указан";
                    }
                    else{
                        temp = tempOperation.getCounterparty().getTitle();
                    }
                    counterpartiesExpense.add(temp);
                }

                for(String i : counterpartiesIncome){// Поступления
                    incomeRows.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < incomeOperations.size();j++){
                            Operation tempOperation = incomeOperations.get(j);
                            if(tempOperation.getCounterparty() != null){
                                if(tempOperation.getCounterparty().getTitle().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Контрагент не указан")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.add(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    income.add(cellVals);
                }
                cashFlow.setIncomeRows(incomeRows);
                cashFlow.setIncome(income);

                for(String i : counterpartiesExpense){// Выплаты
                    expenseRows.add(i);
                    List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for(int p = 0; p < cashFlow.getColumns().size();p++){
                        BigDecimal cellVal = BigDecimal.ZERO;
                        for(int j = 0;j < expenseOperations.size();j++){
                            Operation tempOperation = expenseOperations.get(j);
                            if(tempOperation.getCounterparty() != null){
                                if(tempOperation.getCounterparty().getTitle().equals(i)){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                            }
                            else{
                                if(i.equals("Контрагент не указан")){
                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                    }
                                }
                            }
                        }
                        cellVals.add(cellVal);
                        sum = sum.add(cellVal);
                    }
                    cellVals.add(sum);
                    expense.add(cellVals);
                }
                cashFlow.setExpenseRows(expenseRows);
                cashFlow.setExpense(expense);

                BigDecimal saldoTotal = BigDecimal.ZERO;
                BigDecimal incomeTotal = BigDecimal.ZERO;
                BigDecimal expenseTotal = BigDecimal.ZERO;
                for(int i = 0;i < cashFlow.getColumns().size();i++){
                    BigDecimal sumIn = BigDecimal.ZERO;
                    BigDecimal sumEx = BigDecimal.ZERO;
                    transferSum.add(transferIn.get(i).add(transferOut.get(i)));
                    for(int j = 0;j < incomeRows.size();j++){
                        sumIn = sumIn.add(income.get(j).get(i));
                    }
                    for(int j = 0;j < expenseRows.size();j++){
                        sumEx = sumEx.add(expense.get(j).get(i));
                    }
                    incomeSum.add(sumIn);
                    expenseSum.add(sumEx);
                    incomeTotal = incomeTotal.add(sumIn);
                    expenseTotal = expenseTotal.add(sumEx);
                    BigDecimal sum = sumIn.add(sumEx).add(transferIn.get(i)).add(transferOut.get(i));
                    saldo.add(sum);
                    saldoTotal = saldoTotal.add(sum);
                }
                transferSum.add(transferIn.get(cashFlow.getColumns().size()).add(transferOut.get(cashFlow.getColumns().size())));
                incomeSum.add(incomeTotal);
                expenseSum.add(expenseTotal);
                saldo.add(saldoTotal);
                cashFlow.setIncomeSum(incomeSum);
                cashFlow.setExpenseSum(expenseSum);
                cashFlow.setTransferSum(transferSum);
                cashFlow.setSaldo(saldo);

                return cashFlow;
            }
            case "project":
            {
                first = "ПРОЕКТ";
                cashFlow.setFirst(first);
                List<CashFlowProjectData> projectsData = new ArrayList<CashFlowProjectData>();
                
                for(int i = 0;i <= cashFlow.getColumns().size();i++){
                    saldo.add(transferIn.get(i).add(transferOut.get(i)));
                }

                rows.add("Деньги на начало периода");

                List<String> pNames = new ArrayList<String>();
                for(String i: projectNames){
                    rows.add(i);
                    pNames.add(i);

                    CashFlowProjectData sampleProject = new CashFlowProjectData();

                    List<List<BigDecimal>> projectIncome = new ArrayList<List<BigDecimal>>();
                    List<List<BigDecimal>> projectExpense = new ArrayList<List<BigDecimal>>();    

                    sampleProject.setProjectName(i);
                    HashSet<String> projectIncomeArticles = new HashSet<String>();
                    for(int j = 0;j < incomeOperations.size();j++){
                        if(i == "Без проекта"){
                            if(incomeOperations.get(j).getProject() == null){
                                if(incomeOperations.get(j).getArticle() == null){
                                    projectIncomeArticles.add("Статья не указана");
                                }
                                else{
                                    projectIncomeArticles.add(incomeOperations.get(j).getArticle().getName());
                                }
                            }
                        }
                        else{
                            if(i.equals(incomeOperations.get(j).getProject().getName())){
                                if(incomeOperations.get(j).getArticle() == null){
                                    projectIncomeArticles.add("Статья не указана");
                                }
                                else{
                                    projectIncomeArticles.add(incomeOperations.get(j).getArticle().getName());
                                }
                            }
                        }
                    }
                    HashSet<String> projectExpenseArticles = new HashSet<String>();
                    for(int j = 0;j < expenseOperations.size();j++){
                        if(i == "Без проекта"){
                            if(expenseOperations.get(j).getProject() == null){
                                if(expenseOperations.get(j).getArticle() == null){
                                    projectExpenseArticles.add("Статья не указана");
                                }
                                else{
                                    projectExpenseArticles.add(expenseOperations.get(j).getArticle().getName());
                                }
                            }
                        }
                        else{
                            if(i.equals(expenseOperations.get(j).getProject().getName())){
                                if(expenseOperations.get(j).getArticle() == null){
                                    projectExpenseArticles.add("Статья не указана");
                                }
                                else{
                                    projectExpenseArticles.add(expenseOperations.get(j).getArticle().getName());
                                }
                            }
                        }
                    }
                    List<String> incomeArticles = new ArrayList<String>(projectIncomeArticles);
                    List<String> expenseArticles = new ArrayList<String>(projectExpenseArticles);
                    sampleProject.setProjectIncomeRows(incomeArticles);
                    sampleProject.setProjectExpenseRows(expenseArticles);
                    List<BigDecimal> projectIncomeSum = new ArrayList<BigDecimal>();
                    for(int j = 0;j <= cashFlow.getColumns().size();j++){
                        projectIncomeSum.add(BigDecimal.ZERO);
                    }
                    List<BigDecimal> projectExpenseSum = new ArrayList<BigDecimal>(projectIncomeSum);
                    List<BigDecimal> projectTotal = new ArrayList<BigDecimal>(projectIncomeSum);

                    for(String j : incomeArticles){// Поступления
                        List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                        BigDecimal sum = BigDecimal.ZERO;
                        for(int p = 0; p < cashFlow.getColumns().size();p++){
                            BigDecimal cellVal = BigDecimal.ZERO;

                            for(int k = 0;k < incomeOperations.size();k++){
                                Operation tempOperation = incomeOperations.get(k);
                                if(i.equals("Без проекта")){
                                    if(tempOperation.getProject() == null){
                                        if(!j.equals("Статья не указана")){
                                            if(tempOperation.getArticle() != null)
                                                if(j.equals(tempOperation.getArticle().getName())){
                                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                                        cellVal = cellVal.add(tempOperation.getBalance());
                                                    }
                                                }
                                        }
                                        else{
                                            if(tempOperation.getArticle() == null)
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                                    cellVal = cellVal.add(tempOperation.getBalance());
                                                }
                                        }
                                    }
                                }
                                else{
                                    if(tempOperation.getProject() != null){
                                        if(i.equals(tempOperation.getProject().getName())){
                                            if(!j.equals("Статья не указана")){
                                                if(tempOperation.getArticle() != null)
                                                    if(j.equals(tempOperation.getArticle().getName())){
                                                        if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                                        && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                                            cellVal = cellVal.add(tempOperation.getBalance());
                                                        }
                                                    }
                                            }
                                            else{
                                                if(tempOperation.getArticle() == null)
                                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                                        cellVal = cellVal.add(tempOperation.getBalance());
                                                    }
                                            }
                                        }
                                    }
                                }
                            }
                            cellVals.add(cellVal);
                            sum = sum.add(cellVal);
                            projectIncomeSum.set(p, projectIncomeSum.get(p).add(cellVal));
                            projectTotal.set(p, projectTotal.get(p).add(cellVal));
                            saldo.set(p, saldo.get(p).add(cellVal));
                        }
                        cellVals.add(sum);
                        projectIncome.add(cellVals);
                        projectIncomeSum.set(projectIncomeSum.size() - 1, projectIncomeSum.get(projectIncomeSum.size() - 1).add(sum));
                        projectTotal.set(projectTotal.size() - 1, projectTotal.get(projectTotal.size() - 1).add(sum));
                        saldo.set(saldo.size() - 1, saldo.get(saldo.size() - 1).add(sum));
                    }

                    for(String j : expenseArticles){// Выплаты
                        List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
                        BigDecimal sum = BigDecimal.ZERO;
                        for(int p = 0; p < cashFlow.getColumns().size();p++){
                            BigDecimal cellVal = BigDecimal.ZERO;
                            for(int k = 0;k < expenseOperations.size();k++){
                                Operation tempOperation = expenseOperations.get(k);
                                if(i.equals("Без проекта")){
                                    if(tempOperation.getProject() == null){
                                        if(!j.equals("Статья не указана")){
                                            if(tempOperation.getArticle() != null)
                                                if(j.equals(tempOperation.getArticle().getName())){
                                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                                    }
                                                }
                                        }
                                        else{
                                            if(tempOperation.getArticle() == null)
                                                if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                                && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                                    cellVal = cellVal.subtract(tempOperation.getBalance());
                                                }
                                        }
                                    }
                                }
                                else{
                                    if(tempOperation.getProject() != null){
                                        if(i.equals(tempOperation.getProject().getName())){
                                            if(!j.equals("Статья не указана")){
                                                if(tempOperation.getArticle() != null)
                                                    if(j.equals(tempOperation.getArticle().getName())){
                                                        if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                                        && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                                            cellVal = cellVal.subtract(tempOperation.getBalance());
                                                        }
                                                    }
                                            }
                                            else{
                                                if(tempOperation.getArticle() == null)
                                                    if(!tempOperation.getDate().isBefore(rangesFromCols.get(p).getStartDate()) 
                                                    && !tempOperation.getDate().isAfter(rangesFromCols.get(p).getEndDate())){
                                                        cellVal = cellVal.subtract(tempOperation.getBalance());
                                                    }
                                            }
                                        }
                                    }
                                }
                            }
                            cellVals.add(cellVal);
                            sum = sum.add(cellVal);
                            projectExpenseSum.set(p, projectExpenseSum.get(p).add(cellVal));
                            projectTotal.set(p, projectTotal.get(p).add(cellVal));
                            saldo.set(p, saldo.get(p).add(cellVal));
                        }
                        cellVals.add(sum);
                        projectExpense.add(cellVals);
                        projectExpenseSum.set(projectExpenseSum.size() - 1, projectExpenseSum.get(projectExpenseSum.size() - 1).add(sum));
                        projectTotal.set(projectTotal.size() - 1, projectTotal.get(projectTotal.size() - 1).add(sum));
                        saldo.set(saldo.size() - 1, saldo.get(saldo.size() - 1).add(sum));
                    }

                    sampleProject.setProjectIncome(projectIncome);
                    sampleProject.setProjectExpense(projectExpense);
                    sampleProject.setProjectIncomeSum(projectIncomeSum);
                    sampleProject.setProjectExpenseSum(projectExpenseSum);
                    sampleProject.setProjectTotal(projectTotal);
                    projectsData.add(sampleProject);
                }

                rows.add("Переводы между счетами");
                rows.add("Сальдо");
                rows.add("Деньги на конец периода");

                cashFlow.setProjectData(projectsData);
                cashFlow.setSaldo(saldo);
                cashFlow.setRows(rows);

                return cashFlow;
            }
            default:
            {
                throw new IllegalArgumentException("Invalid type.");
            }
        }
        
    }

    private List<String> columns(String grouping,  DateRangeInput dateRange){
        List<String> cols = new ArrayList<String>();
        LocalDate startDate = dateRange.getStartDate();
        LocalDate endDate = dateRange.getEndDate();
        switch(grouping){
            case "day":
                while (!startDate.isAfter(endDate)) {
                    cols.add(startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    startDate = startDate.plusDays(1);
                }
                return cols;

            case "week":
                while (!startDate.isAfter(endDate)) {
                    LocalDate weekEndDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    if (weekEndDate.isAfter(endDate)) {
                        weekEndDate = endDate;
                    }
                    String weekRange = startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                            weekEndDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    cols.add(weekRange);
                    startDate = weekEndDate.plusDays(1);
                }
                return cols;

            case "month":
                while (!startDate.isAfter(endDate)) {
                    cols.add(startDate.getMonth().toString() + " " + startDate.getYear());
                    startDate = startDate.plusMonths(1);
                }
                return cols;

            case "quarter":
                while (!startDate.isAfter(endDate)) {
                    int quarter = (startDate.getMonthValue() - 1) / 3 + 1;
                    int year = startDate.getYear();
                    LocalDate quarterStart = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
                    LocalDate quarterEnd = LocalDate.of(year, quarter * 3, 1).withDayOfMonth(LocalDate.of(year, quarter * 3, 1).lengthOfMonth());
                    String quarterRange = quarterStart.getMonth().toString() + " - " + quarterEnd.getMonth().toString() + " " + quarterStart.getYear();
                    cols.add(quarterRange);
                    startDate = quarterEnd.plusDays(1);
                }
                return cols;
            default:
                throw new IllegalArgumentException("Empty grouping");
        }
    }

    private List<DateRangeInput> datesFromCols(DateRangeInput dateRange, String grouping){
        List<DateRangeInput> out = new ArrayList<DateRangeInput>();
        LocalDate startDate = dateRange.getStartDate();
        LocalDate endDate = dateRange.getEndDate();

        switch(grouping){
            case "day":
                while (!startDate.isAfter(endDate)) {
                    out.add(
                        new DateRangeInput(
                            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            )
                    );
                    startDate = startDate.plusDays(1);
                }
                return out;
            case "week":
                while (!startDate.isAfter(endDate)) {
                    LocalDate weekEndDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    if (weekEndDate.isAfter(endDate)) {
                        weekEndDate = endDate;
                    }
                    out.add(
                        new DateRangeInput(
                            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            weekEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        )
                    );
                    startDate = weekEndDate.plusDays(1);
                }
                return out;
            case "month":
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

            case "quarter":
                while (!startDate.isAfter(endDate)) {
                    int quarter = (startDate.getMonthValue() - 1) / 3 + 1;
                    int year = startDate.getYear();
                    LocalDate quarterStart = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
                    LocalDate quarterEnd = LocalDate.of(year, quarter * 3, 1).withDayOfMonth(LocalDate.of(year, quarter * 3, 1).lengthOfMonth());
                    if(quarterStart.isBefore(startDate)){
                        quarterStart = startDate;
                    }
                    if(quarterEnd.isAfter(endDate)){
                        quarterEnd = endDate;
                    }
                    out.add(
                        new DateRangeInput(
                            quarterStart.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            quarterEnd.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        )
                    );
                    startDate = quarterEnd.plusDays(1);
                }
                return out;
            default:
                throw new IllegalArgumentException("Empty grouping");
        }
    }
}