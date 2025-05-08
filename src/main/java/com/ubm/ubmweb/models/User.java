package com.ubm.ubmweb.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import com.ubm.ubmweb.entities.UserCompanyRelationship;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity{
    @NotBlank(message = "First name is mandatory")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(name = "last_name")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid")
    @NotBlank(message = "Phone Number is mandatory")
    @Column(name = "phone_number")
    private String phone;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should have at least 8 characters")
    @Column(name = "password")
    private String password;
    
    @Column(name = "receive_non_urgent_emails")
    private boolean receiveNonUrgentEmails = true;

    @Column(name = "photo")
    private String photo;

    @NotBlank(message = "Country name is mandatory")
    @Column(name = "country")
    private String country;

    @NotBlank(message = "Timezone name is mandatory")
    @Column(name = "timezone")
    private String timezone;

    @Column(name = "biography")
    private String biography;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserCompanyRelationship> userCompanies = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserNotificationRelationship> userNotifications = new HashSet<>();

    public void addUserCompany(UserCompanyRelationship userCompany) {
        userCompanies.add(userCompany);
        userCompany.setUser(this);
    }

    public void removeUserCompany(UserCompanyRelationship userCompany) {
        userCompanies.remove(userCompany);
        userCompany.setUser(null);
    }

    public void addUserNotification(UserNotificationRelationship userNotification) {
        userNotifications.add(userNotification);
        userNotification.setUser(this);
    }

    public void removeUserNotification(UserNotificationRelationship userNotification) {
        userNotifications.remove(userNotification);
        userNotification.setUser(null);
    }

}
