package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.exception.PasswordResetTokenNotFoundException;
import com.alokhin.autoservice.persistence.dao.PasswordResetTokenRepository;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.PasswordResetTokenEntity;
import com.alokhin.autoservice.service.PasswordResetTokenService;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public PasswordResetTokenEntity findByToken(String token) throws PasswordResetTokenNotFoundException {
        PasswordResetTokenEntity entity = passwordResetTokenRepository.findByToken(token);
        if (entity == null) {
            throw new PasswordResetTokenNotFoundException(String.format("The password reset token %s doesn't exist", token));
        }
        return entity;
    }

    @Override
    public PasswordResetTokenEntity findByAccountEntity(AccountEntity accountEntity) throws PasswordResetTokenNotFoundException {
        PasswordResetTokenEntity entity = passwordResetTokenRepository.findByAccountEntity(accountEntity);
        if (entity == null) {
            throw new PasswordResetTokenNotFoundException(String.format("The password reset token for username %s doesn't exist", accountEntity.getLogin()));
        }
        return entity;
    }

    @Override
    public PasswordResetTokenEntity save(PasswordResetTokenEntity entity) {
        return passwordResetTokenRepository.save(entity);
    }
}
