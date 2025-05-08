package com.ubm.ubmweb.model;

import jakarta.persistence.*;


@Entity
@DiscriminatorValue("INCOME")
public class IncomeOperation extends Operation {

}
