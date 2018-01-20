package com.alokhin.autoservice.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, Integer> {
    AccountEntity findByLogin(String login);
}
