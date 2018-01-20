package com.alokhin.autoservice.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.VerificationTokenEntity;

import java.util.Date;
import java.util.stream.Stream;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Long> {
    VerificationTokenEntity findByToken(String token);

    VerificationTokenEntity findByAccountEntity(AccountEntity accountEntity);

    Stream<VerificationTokenEntity> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query ("delete from VerificationTokenEntity t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}
