package com.alokhin.autoservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.domain.VerificationTokenResponse;
import com.alokhin.autoservice.exception.PasswordResetTokenNotFoundException;
import com.alokhin.autoservice.exception.VerificationTokenNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.PasswordResetTokenEntity;
import com.alokhin.autoservice.persistence.model.entity.VerificationTokenEntity;
import com.alokhin.autoservice.service.AccountService;
import com.alokhin.autoservice.service.PasswordResetTokenService;
import com.alokhin.autoservice.service.RegistrationService;
import com.alokhin.autoservice.service.VerificationTokenService;

import static com.alokhin.autoservice.domain.VerificationTokenResponse.*;
import static com.alokhin.autoservice.util.RegistrationUtil.generateToken;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private final AccountService accountService;

    private final VerificationTokenService verificationTokenService;

    private final PasswordResetTokenService passwordResetTokenService;

    @Autowired
    public RegistrationServiceImpl(PasswordResetTokenService passwordResetTokenService, VerificationTokenService verificationTokenService,
                                   AccountService accountService) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.verificationTokenService = verificationTokenService;
        this.accountService = accountService;
    }

    @Override
    public VerificationTokenEntity createVerificationToken(AccountEntity accountEntity) {
        return verificationTokenService.save(new VerificationTokenEntity(generateToken(), accountEntity));
    }

    @Override
    public VerificationTokenEntity updateVerificationToken(AccountEntity accountEntity) {
        VerificationTokenEntity verificationTokenEntity;
        try {
            verificationTokenEntity = verificationTokenService.findByAccountEntity(accountEntity);
            verificationTokenEntity.updateToken(generateToken());
        } catch (VerificationTokenNotFoundException ignored) {
            logger.warn("The verification token for username {} doesn't exists. Creating new token", accountEntity.getLogin());
            verificationTokenEntity = createVerificationToken(accountEntity);
        }
        return verificationTokenService.save(verificationTokenEntity);
    }

    @Override
    public PasswordResetTokenEntity createPasswordResetToken(AccountEntity accountEntity) {
        return passwordResetTokenService.save(new PasswordResetTokenEntity(generateToken(), accountEntity));
    }

    @Override
    public PasswordResetTokenEntity updatePasswordResetToken(AccountEntity accountEntity) {
        PasswordResetTokenEntity passwordResetTokenEntity;
        try {
            passwordResetTokenEntity = passwordResetTokenService.findByAccountEntity(accountEntity);
            passwordResetTokenEntity.updateToken(generateToken());
        } catch (PasswordResetTokenNotFoundException ignored) {
            logger.warn("The password reset token for username {} doesn't exists. Creating new token", accountEntity.getLogin());
            passwordResetTokenEntity = createPasswordResetToken(accountEntity);
        }
        return passwordResetTokenService.save(passwordResetTokenEntity);
    }

    @Override
    public VerificationTokenResponse validateVerificationToken(String token) throws VerificationTokenNotFoundException {
        VerificationTokenEntity verificationToken = verificationTokenService.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        AccountEntity accountEntity = verificationToken.getAccountEntity();
        if (verificationToken.isExpired().booleanValue()) {
            verificationTokenService.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        accountEntity.enable();
        accountService.save(accountEntity);
        return TOKEN_VALID;
    }
}
