package com.ubm.ubmweb.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;


@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Column(name = "type", nullable = false)
    private String type;// Товар или Услуга

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "unit", nullable = true)
    private String unit;

    @Column(name = "vendor_code", nullable = true)
    private String vendorCode;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "default_price", nullable = true)
    private BigDecimal defaultSellingPrice;

    @Column(name = "default_currency", nullable = false)
    private String dspCurrency;

    @Column(name = "first_shipment_id")
    private Long firstShipmentId;

    @Column(name = "earliest_shipment")
    private LocalDate earliestShipment;


    public LocalDate getEarliestShipment() {
        return earliestShipment;
    }

    public void setEarliestShipment(LocalDate earliestShipment) {
        this.earliestShipment = earliestShipment;
    }

    public Long getFirstShipmentId() {
        return firstShipmentId;
    }

    public void setFirstShipmentId(Long firstShipmentId) {
        this.firstShipmentId = firstShipmentId;
    }

    public String getDspCurrency() {
        return dspCurrency;
    }

    public void setDspCurrency(String dspCurrency) {
        this.dspCurrency = dspCurrency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDefaultSellingPrice() {
        return defaultSellingPrice;
    }

    public void setDefaultSellingPrice(BigDecimal defaultSellingPrice) {
        this.defaultSellingPrice = defaultSellingPrice;
    }

    

}
