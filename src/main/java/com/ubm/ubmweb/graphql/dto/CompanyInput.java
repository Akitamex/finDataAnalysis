package com.ubm.ubmweb.graphql.dto;

import com.ubm.ubmweb.model.Company;

import lombok.Data;

@Data
public class CompanyInput {
    private String name;
    private String shortUrl;
    private String description;
    private String logoUrl;
    private String brandingReports;
    private String brandingEmails;
    private String twitterProfile;
    private String facebookProfile;
    private String linkedinProfile;

    public static Company companyInputToCompany(CompanyInput input, Company company) {
        if (input.getName() != null) company.setName(input.getName());
        if (input.getShortUrl() != null) company.setShortUrl(input.getShortUrl());
        if (input.getDescription() != null) company.setDescription(input.getDescription());
        if (input.getLogoUrl() != null) company.setLogoUrl(input.getLogoUrl());
        if (input.getBrandingReports() != null) {
            String brandingReports = input.getBrandingReports().toLowerCase();
            if (brandingReports.equals("true") || brandingReports.equals("false")) {
                company.setBrandingReports(brandingReports);
            } else {
                throw new IllegalArgumentException("Branding Reports can only be true or false");
            }
        }
        if (input.getBrandingEmails() != null) {            
            String brandingEmails = input.getBrandingEmails().toLowerCase();
            if (brandingEmails.equals("true") || brandingEmails.equals("false")) {
                company.setBrandingEmails(brandingEmails);
            } else {
                throw new IllegalArgumentException("Branding Emails can only be true or false");
            }
        }
        if (input.getTwitterProfile() != null) company.setTwitterProfile(input.getTwitterProfile());
        if (input.getFacebookProfile() != null) company.setFacebookProfile(input.getFacebookProfile());
        if (input.getLinkedinProfile() != null) company.setLinkedinProfile(input.getLinkedinProfile());
        return company;
    }
}
