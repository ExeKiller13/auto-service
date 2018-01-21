package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.exception.AccountNotFoundException;
import com.alokhin.autoservice.persistence.dao.AccountRepository;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.service.AccountService;

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
}
