package com.alokhin.autoservice.service;

import com.alokhin.autoservice.exception.AccountAlreadyExistException;
import com.alokhin.autoservice.exception.AccountNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.web.dto.CreateAccountDto;

public interface AccountService {

    AccountEntity save(AccountEntity entity);

    void delete(AccountEntity entity);

    AccountEntity findByLogin(String login) throws AccountNotFoundException;

    AccountEntity createAccount(CreateAccountDto createAccountDto) throws AccountAlreadyExistException;

    AccountEntity changeAccountPassword(String login, String newPassword) throws AccountNotFoundException;
}
