package com.ubm.ubmweb.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;


@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
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
    private UUID firstShipmentId;

    @Column(name = "earliest_shipment")
    private LocalDate earliestShipment;


    public LocalDate getEarliestShipment() {
        return earliestShipment;
    }

    public void setEarliestShipment(LocalDate earliestShipment) {
        this.earliestShipment = earliestShipment;
    }

    public UUID getFirstShipmentId() {
        return firstShipmentId;
    }

    public void setFirstShipmentId(UUID firstShipmentId) {
        this.firstShipmentId = firstShipmentId;
    }

    public String getDspCurrency() {
        return dspCurrency;
    }

    public void setDspCurrency(String dspCurrency) {
        this.dspCurrency = dspCurrency;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
