package com.ubm.ubmweb.entities;

import jakarta.persistence.*;


@Entity
@DiscriminatorValue("INCOME")
public class IncomeOperation extends Operation {

}
