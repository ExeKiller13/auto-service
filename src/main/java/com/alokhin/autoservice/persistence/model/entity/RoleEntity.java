package com.alokhin.autoservice.persistence.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table (name = "role")
@Builder
public class RoleEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String name;

    private String description;

    @Tolerate
    public RoleEntity() {
    }

    public Boolean isAdmin() {
        return "ADMIN".equals(name);
    }
}
