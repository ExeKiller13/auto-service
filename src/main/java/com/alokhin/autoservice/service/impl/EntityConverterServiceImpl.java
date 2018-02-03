package com.alokhin.autoservice.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.exception.AccountNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;
import com.alokhin.autoservice.persistence.model.entity.RoleEntity;
import com.alokhin.autoservice.service.AccountService;
import com.alokhin.autoservice.service.EntityConverterService;
import com.alokhin.autoservice.web.dto.AccountDto;
import com.alokhin.autoservice.web.dto.CarDto;
import com.alokhin.autoservice.web.dto.RoleDto;

import java.util.stream.Collectors;

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
    public RoleEntity toEntity(RoleDto dto) {
        return mapper.map(dto, RoleEntity.class);
    }

    @Override
    public RoleDto toDto(RoleEntity entity) {
        return mapper.map(entity, RoleDto.class);
    }

    @Override
    public AccountEntity toEntity(AccountDto dto) {
        AccountEntity entity = mapper.map(dto, AccountEntity.class);
        entity.setRoles(dto.getRoles().stream().map(this::toEntity).collect(Collectors.toList()));
        return entity;
    }

    @Override
    public AccountDto toDto(AccountEntity entity) {
        AccountDto dto = mapper.map(entity, AccountDto.class);
        dto.setRoles(entity.getRoles().stream().map(this::toDto).collect(Collectors.toList()));
        return dto;
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
