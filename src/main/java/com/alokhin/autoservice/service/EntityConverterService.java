package com.alokhin.autoservice.service;

import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;
import com.alokhin.autoservice.persistence.model.entity.RoleEntity;
import com.alokhin.autoservice.web.dto.AccountDto;
import com.alokhin.autoservice.web.dto.CarDto;
import com.alokhin.autoservice.web.dto.RoleDto;

public interface EntityConverterService {

    RoleEntity toEntity(RoleDto dto);

    RoleDto toDto(RoleEntity entity);

    AccountEntity toEntity(AccountDto dto);

    AccountDto toDto(AccountEntity entity);

    CarEntity toEntity(CarDto dto);

    CarDto toDto(CarEntity entity);
}
