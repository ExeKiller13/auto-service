package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.exception.VerificationTokenNotFoundException;
import com.alokhin.autoservice.persistence.dao.VerificationTokenRepository;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.VerificationTokenEntity;
import com.alokhin.autoservice.service.VerificationTokenService;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationTokenEntity save(VerificationTokenEntity entity) {
        return verificationTokenRepository.save(entity);
    }

    @Override
    public VerificationTokenEntity findByToken(String token) throws VerificationTokenNotFoundException {
        VerificationTokenEntity entity = verificationTokenRepository.findByToken(token);
        if (entity == null) {
            throw new VerificationTokenNotFoundException(String.format("The verification token %s doesn't exist", token));
        }
        return entity;
    }

    @Override
    public void delete(VerificationTokenEntity entity) {
        verificationTokenRepository.delete(entity);
    }

    @Override
    public VerificationTokenEntity findByAccountEntity(AccountEntity accountEntity) throws VerificationTokenNotFoundException {
        VerificationTokenEntity entity = verificationTokenRepository.findByAccountEntity(accountEntity);
        if (entity == null) {
            throw new VerificationTokenNotFoundException(String.format("The verification token for username %s doesn't exist", accountEntity.getLogin()));
        }
        return entity;
    }
}
