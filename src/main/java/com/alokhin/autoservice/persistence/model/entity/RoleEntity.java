package com.alokhin.autoservice.persistence.model.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table (name = "role")
public class RoleEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String name;

    private String description;
}
