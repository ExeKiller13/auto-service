package com.alokhin.autoservice.persistence.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "verification_token")
public class VerificationTokenEntity {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne (targetEntity = AccountEntity.class, fetch = FetchType.EAGER)
    @JoinColumn (nullable = false, name = "account_id")
    private AccountEntity accountEntity;

    @Builder.Default
    private Date expiryDate = calculateExpiryDate(EXPIRATION);

    public VerificationTokenEntity(String token, AccountEntity accountEntity) {
        this.token = token;
        this.accountEntity = accountEntity;
    }

    private static Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public void updateToken(String newToken) {
        this.token = newToken;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public Boolean isExpired() {
        return this.expiryDate.getTime() >= new Date().getTime();
    }
}