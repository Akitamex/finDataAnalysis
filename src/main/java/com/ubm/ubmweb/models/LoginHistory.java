package com.ubm.ubmweb.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device", nullable = false)
    private String device;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;
}
