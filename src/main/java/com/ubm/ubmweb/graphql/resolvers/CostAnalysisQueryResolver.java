package com.ubm.ubmweb.graphql.resolvers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Operation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.models.CostAnalysis;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CostAnalysisQueryResolver implements GraphQLQueryResolver{
    private final OperationsRepository operationsRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    public CostAnalysis costAnalysis(Long companyId, Long userId, String timeframe, String grouping, List<Long> bankAccountIds, List<Long> projectIds, List<Long> articleIds){
        Boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId,userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User does not have acces to company with id: " + companyId);
        }
        
        CostAnalysis costAnalysis = new CostAnalysis();
        DateRangeInput dateRange = new DateRangeInput("","");
        dateRange.parseTimeFrame(timeframe);
        costAnalysis.setColumns(columns(grouping, dateRange));
        List<DateRangeInput> rangesFromCols = new ArrayList<DateRangeInput>();
        rangesFromCols = datesFromCols(dateRange, grouping);
        List<String> rows = new ArrayList<String>();

        List<Operation> costAnalysisOperations = operationsRepository.findCostAnalysis(companyId, dateRange.getStartDate(), dateRange.getEndDate());
        List<Operation> allOperations = new ArrayList<Operation>(costAnalysisOperations);
        HashSet<String> articleSet = new HashSet<String>();
        List<List<BigDecimal>> entries = new ArrayList<List<BigDecimal>>();

        List<BigDecimal> expenses = new ArrayList<BigDecimal>(Collections.nCopies(costAnalysis.getColumns().size(), BigDecimal.ZERO));
        List<BigDecimal> revenue = new ArrayList<BigDecimal>();
        List<BigDecimal> shareOfExpenses = new ArrayList<BigDecimal>(Collections.nCopies(costAnalysis.getColumns().size(), BigDecimal.ZERO));


        for(int i = 0; i < costAnalysis.getColumns().size();i++){
            List<Operation> revenueOperations = operationsRepository.findCARevenue(companyId, rangesFromCols.get(i).getStartDate(), rangesFromCols.get(i).getEndDate());
            BigDecimal cellVal = revenueOperations.stream()
                         .map(Operation::getBalance)
                         .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            revenue.add(cellVal);
        }
        


        for(Operation i: costAnalysisOperations){
            if(i.getArticle() == null){
                //idk how, but just in case
                allOperations.remove(i);
                continue;
            }
            if(!bankAccountIds.isEmpty()){
                if(!bankAccountIds.contains(i.getBankAccount().getId())){
                    allOperations.remove(i);
                    continue;
                }
            }
            if(!projectIds.isEmpty()){
                if(i.getProject() == null){
                    allOperations.remove(i);
                    continue;
                }
                else{
                    if(!projectIds.contains(i.getProject().getId())){
                        allOperations.remove(i);
                        continue;
                    }
                }
            }
            if(!articleIds.isEmpty()){
                if(!articleIds.contains(i.getArticle().getId())){
                    allOperations.remove(i);
                    continue;
                }
            }
            
            articleSet.add(i.getArticle().getName());
        }


        for(String articleName: articleSet){
            rows.add(articleName);
            List<BigDecimal> cellVals = new ArrayList<BigDecimal>();
            for(int i = 0;i < costAnalysis.getColumns().size();i++){
                BigDecimal cellVal = BigDecimal.ZERO;
                for(Operation o: allOperations){
                    if(o.getArticle().getName().equals(articleName)){
                        if(!o.getDate().isBefore(rangesFromCols.get(i).getStartDate()) 
                        && !o.getDate().isAfter(rangesFromCols.get(i).getEndDate())){
                            cellVal = cellVal.add(o.getBalance());
                        }
                    }
                }
                cellVals.add(cellVal);
                expenses.set(i, expenses.get(i).add(cellVal));
            }
            entries.add(cellVals);
        }

        for(int i = 0;i < shareOfExpenses.size();i++){
            if(revenue.get(i).compareTo(BigDecimal.ZERO) != 0) { // Avoid division by zero
                shareOfExpenses.set(i, expenses.get(i).divide(revenue.get(i), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            } else {
                shareOfExpenses.set(i, BigDecimal.ZERO);
            }
        }

        entries.add(expenses);
        rows.add("Расходы");
        entries.add(revenue);
        rows.add("Выручка");
        entries.add(shareOfExpenses);
        rows.add("Доля расходов");
        return costAnalysis;
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
