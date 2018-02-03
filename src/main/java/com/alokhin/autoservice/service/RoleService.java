package com.alokhin.autoservice.service;

import com.alokhin.autoservice.exception.RoleNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.RoleEntity;

public interface RoleService {

    RoleEntity findByName(String name) throws RoleNotFoundException;

    RoleEntity save(RoleEntity entity);
}
