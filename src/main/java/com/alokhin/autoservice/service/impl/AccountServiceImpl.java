package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.exception.AccountAlreadyExistException;
import com.alokhin.autoservice.exception.AccountNotFoundException;
import com.alokhin.autoservice.persistence.dao.AccountRepository;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.RoleEntity;
import com.alokhin.autoservice.service.AccountService;
import com.alokhin.autoservice.web.dto.CreateAccountDto;

import java.util.Collections;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountEntity save(AccountEntity entity) {
        return accountRepository.save(entity);
    }

    @Override
    public AccountEntity findByLogin(String login) throws AccountNotFoundException {
        AccountEntity entity = accountRepository.findByLogin(login);
        if (entity == null) {
            throw new AccountNotFoundException(String.format("The account with username %s doesn't exist", login));
        }
        return entity;
    }

    @Override
    public AccountEntity createAccount(CreateAccountDto createAccountDto) throws AccountAlreadyExistException {
        if (accountRepository.findByLogin(createAccountDto.getEmail()) != null) {
            throw new AccountAlreadyExistException(String.format("The account with username %s already exist", createAccountDto.getEmail()));
        }
        RoleEntity userRole = RoleEntity.builder().name("USER").build();
        AccountEntity newAccount = AccountEntity.builder().login(createAccountDto.getEmail())
                                                .password(createAccountDto.getPassword())
                                                .roles(Collections.singletonList(userRole)).build();
        return save(newAccount);
    }

    @Override
    public AccountEntity changeAccountPassword(String login, String newPassword) throws AccountNotFoundException {
        AccountEntity entity = findByLogin(login);
        entity.setPassword(newPassword);
        return save(entity);
    }
}
