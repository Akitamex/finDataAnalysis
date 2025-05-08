package com.ubm.ubmweb.graphql.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateRangeInput {
    private String startDate;
    private String endDate;
    private DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter outputFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    // Constructor
    public DateRangeInput(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters for String values
    public String getStartDateAsString() {
        return startDate;
    }

    public String getEndDateAsString() {
        return endDate;
    }

    // Setters for String values
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    // Methods to get LocalDate instances
    public LocalDate getStartDate() {
        return parseDate(startDate);
    }

    public LocalDate getEndDate() {
        return parseDate(endDate);
    }

    // Helper method to parse String to LocalDate
    private LocalDate parseDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in the format YYYY-MM-DD");
        }
    }

    public void parseTimeFrame(String timeframeString){
        if(timeframeString == null){
            return;
        }
        String[] dates = timeframeString.split("-");
        LocalDate first = LocalDate.parse(dates[0], inputFormatter);
        LocalDate second = LocalDate.parse(dates[1], inputFormatter);

        this.startDate = first.format(outputFormatter);
        this.endDate = second.format(outputFormatter);
    }
}
