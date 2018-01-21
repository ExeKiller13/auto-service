package com.alokhin.autoservice.service;

import com.alokhin.autoservice.exception.VerificationTokenNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.VerificationTokenEntity;

public interface VerificationTokenService {

    VerificationTokenEntity save(VerificationTokenEntity entity);

    VerificationTokenEntity findByToken(String token) throws VerificationTokenNotFoundException;

    void delete(VerificationTokenEntity entity);

    VerificationTokenEntity findByAccountEntity(AccountEntity accountEntity) throws VerificationTokenNotFoundException;
}
