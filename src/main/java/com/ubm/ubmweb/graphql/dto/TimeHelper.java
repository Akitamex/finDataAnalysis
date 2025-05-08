package com.ubm.ubmweb.graphql.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeHelper {

    private String dateString;
    private LocalDate date;

    private DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;
    private DateTimeFormatter in = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public String getDateString() {
        return dateString;
    }
    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String strToStrISO(String dateString){
        LocalDate parsed = LocalDate.parse(dateString, in);
        return parsed.format(iso);
    }

    public LocalDate strToDateISO(String dateString){
        LocalDate parsed = LocalDate.parse(dateString, in);
        dateString = parsed.format(iso);
        return LocalDate.parse(dateString, iso);        
    }
    
}
