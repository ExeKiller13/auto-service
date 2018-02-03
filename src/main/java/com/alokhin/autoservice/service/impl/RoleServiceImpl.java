package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.exception.RoleNotFoundException;
import com.alokhin.autoservice.persistence.dao.RoleRepository;
import com.alokhin.autoservice.persistence.model.entity.RoleEntity;
import com.alokhin.autoservice.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleEntity findByName(String name) throws RoleNotFoundException {
        RoleEntity entity = roleRepository.findByName(name);
        if (entity == null) {
            throw new RoleNotFoundException(String.format("%s role not found", name));
        }
        return entity;
    }

    @Override
    public RoleEntity save(RoleEntity entity) {
        return roleRepository.save(entity);
    }
}
