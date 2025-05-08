package com.ubm.ubmweb.compositeKey;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserCompanyId implements Serializable {
    
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "company_id")
    private UUID companyId;
    
    public UserCompanyId() {}

    public UserCompanyId(UUID companyId, UUID userId) {
        this.companyId = companyId;
        this.userId = userId;
    }

    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCompanyId)) return false;
        UserCompanyId that = (UserCompanyId) o;
        return Objects.equals(companyId, that.companyId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, userId);
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}