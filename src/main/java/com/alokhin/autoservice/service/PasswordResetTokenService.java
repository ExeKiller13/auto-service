package com.alokhin.autoservice.service;

import com.alokhin.autoservice.exception.PasswordResetTokenNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenService {

    PasswordResetTokenEntity findByToken(String token) throws PasswordResetTokenNotFoundException;

    PasswordResetTokenEntity findByAccountEntity(AccountEntity accountEntity) throws PasswordResetTokenNotFoundException;

    PasswordResetTokenEntity save(PasswordResetTokenEntity entity);

    void delete(PasswordResetTokenEntity entity);
}
