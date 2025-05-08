package com.ubm.ubmweb.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentDetailsDTO {

    @NotNull
    private Long companyId;

    @NotBlank
    @Size(max = 255)
    private String cardName;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/([0-9]{2})$", message = "Expiration date must be in the format MM/YY")
    private String cardExpiration;

    @NotBlank
    @Size(min = 16, max = 19)
    @Pattern(regexp = "^[0-9]+$", message = "Card number must be numeric")
    private String cardNumber;

    @NotBlank
    @Size(max = 255)
    private String billingAddress;

    @NotBlank
    @Size(max = 255)
    private String city;

    @Size(max = 255)
    private String state;

    @NotBlank
    @Size(max = 20)
    private String postalCode;

    @NotBlank
    @Size(max = 255)
    private String country;
}