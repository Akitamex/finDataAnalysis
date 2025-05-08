package com.ubm.ubmweb.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "login_history")
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginHistory extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "device", nullable = false)
    private String device;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;
}
