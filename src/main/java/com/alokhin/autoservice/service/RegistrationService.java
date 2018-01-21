package com.alokhin.autoservice.service;

import com.alokhin.autoservice.domain.VerificationTokenResponse;
import com.alokhin.autoservice.exception.VerificationTokenNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.PasswordResetTokenEntity;
import com.alokhin.autoservice.persistence.model.entity.VerificationTokenEntity;

public interface RegistrationService {

    VerificationTokenEntity createVerificationToken(AccountEntity accountEntity);

    VerificationTokenEntity updateVerificationToken(AccountEntity accountEntity);

    PasswordResetTokenEntity createPasswordResetToken(AccountEntity accountEntity);

    PasswordResetTokenEntity updatePasswordResetToken(AccountEntity accountEntity);

    VerificationTokenResponse validateVerificationToken(String token) throws VerificationTokenNotFoundException;
}
