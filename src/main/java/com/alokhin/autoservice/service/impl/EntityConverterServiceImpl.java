package com.alokhin.autoservice.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.exception.AccountNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;
import com.alokhin.autoservice.service.AccountService;
import com.alokhin.autoservice.service.EntityConverterService;
import com.alokhin.autoservice.web.dto.AccountDto;
import com.alokhin.autoservice.web.dto.CarDto;

@Service
public class EntityConverterServiceImpl implements EntityConverterService {

    private final ModelMapper mapper;

    private final AccountService accountService;

    @Autowired
    public EntityConverterServiceImpl(ModelMapper mapper, AccountService accountService) {
        this.mapper = mapper;
        this.accountService = accountService;
    }

    @Override
    public AccountEntity toEntity(AccountDto dto) {
        return mapper.map(dto, AccountEntity.class);
    }

    @Override
    public AccountDto toDto(AccountEntity entity) {
        return mapper.map(entity, AccountDto.class);

    }

    @Override
    public CarDto toDto(CarEntity entity) {
        CarDto dto = mapper.map(entity, CarDto.class);
        if (entity.getAccountEntity() != null) {
            dto.setLogin(entity.getAccountEntity().getLogin());
        }
        return dto;
    }

    @Override
    public CarEntity toEntity(CarDto dto) {
        CarEntity entity = mapper.map(dto, CarEntity.class);
        AccountEntity accountEntity = null;
        try {
            accountEntity = accountService.findByLogin(dto.getLogin());
        } catch (AccountNotFoundException ignored) {
        }
        entity.setAccountEntity(accountEntity);
        return entity;
    }
}
