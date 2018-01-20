package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alokhin.autoservice.persistence.dao.AccountRepository;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.RoleEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        AccountEntity accountEntity = accountRepository.findByLogin(login);
        if (accountEntity == null) {
            throw new UsernameNotFoundException(String.format("The username %s doesn't exist", login));
        }
        List<GrantedAuthority> authorities =
            accountEntity.getRoles().stream().map(RoleEntity::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        boolean enabled = accountEntity.getEnabled().booleanValue();
        return new User(accountEntity.getLogin(), accountEntity.getPassword(), enabled, enabled, enabled,
                        enabled, authorities);
    }
}
