package com.alokhin.autoservice.service;

import com.alokhin.autoservice.exception.AccountNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;

public interface AccountService {

    AccountEntity findByLogin(String login) throws AccountNotFoundException;
}
