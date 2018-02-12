package com.alokhin.autoservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.domain.VerificationTokenResponse;
import com.alokhin.autoservice.exception.AccountAlreadyActivatedException;
import com.alokhin.autoservice.exception.AccountNotActivatedException;
import com.alokhin.autoservice.exception.PasswordResetTokenNotFoundException;
import com.alokhin.autoservice.exception.VerificationTokenNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.PasswordResetTokenEntity;
import com.alokhin.autoservice.persistence.model.entity.VerificationTokenEntity;
import com.alokhin.autoservice.service.*;

import static com.alokhin.autoservice.domain.VerificationTokenResponse.*;
import static com.alokhin.autoservice.util.RegistrationUtil.generateToken;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Value ("${support.email}")
    private String from;

    private final AccountService accountService;

    private final VerificationTokenService verificationTokenService;

    private final PasswordResetTokenService passwordResetTokenService;

    private final MailService mailService;

    @Autowired
    public RegistrationServiceImpl(PasswordResetTokenService passwordResetTokenService, VerificationTokenService verificationTokenService,
                                   AccountService accountService, MailService mailService) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.verificationTokenService = verificationTokenService;
        this.accountService = accountService;
        this.mailService = mailService;
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
    public void forgotPassword(AccountEntity accountEntity, String path) throws AccountNotActivatedException {
        if (!accountEntity.getEnabled().booleanValue()) {
            throw new AccountNotActivatedException(String.format("Account with username %s isn't activated", accountEntity.getLogin()));
        }
        PasswordResetTokenEntity token = updatePasswordResetToken(accountEntity);
        String url = path + "/reset?email=" + accountEntity.getLogin() + "&token=" + token.getToken();
        mailService.sendMailMessage(from, accountEntity.getLogin(), "Reset Password",
                                    String.format("Please reset password following by link: \r\n %s", url));
    }

    @Override
    public VerificationTokenResponse validateVerificationToken(String token) throws AccountAlreadyActivatedException {
        try {
            VerificationTokenEntity verificationToken = verificationTokenService.findByToken(token);
            AccountEntity accountEntity = verificationToken.getAccountEntity();
            if (accountEntity.getEnabled().booleanValue()) {
                throw new AccountAlreadyActivatedException(String.format("Account with username %s is already activated", accountEntity.getLogin()));
            }
            if (verificationToken.isExpired().booleanValue()) {
                accountService.delete(accountEntity);
                verificationTokenService.delete(verificationToken);
                return TOKEN_EXPIRED;
            }
            accountEntity.enable();
            accountService.save(accountEntity);
            return TOKEN_VALID;
        } catch (VerificationTokenNotFoundException ignored) {
            return TOKEN_INVALID;
        }
    }

    @Override
    public VerificationTokenResponse validatePasswordResetToken(AccountEntity accountEntity, String token) {
        try {
            PasswordResetTokenEntity passwordResetToken = passwordResetTokenService.findByToken(token);
            if (!accountEntity.equals(passwordResetToken.getAccountEntity())) {
                return TOKEN_INVALID;
            }
            if (passwordResetToken.isExpired().booleanValue()) {
                return TOKEN_EXPIRED;
            }
            return TOKEN_VALID;
        } catch (PasswordResetTokenNotFoundException ignored) {
            return TOKEN_INVALID;
        }
    }

    @Override
    public AccountEntity updatePasswordByResetToken(String token, String password) throws PasswordResetTokenNotFoundException {
        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenService.findByToken(token);
        AccountEntity accountEntity = passwordResetTokenEntity.getAccountEntity();
        passwordResetTokenService.delete(passwordResetTokenEntity);
        return accountService.changeAccountPassword(accountEntity, password);
    }
}
