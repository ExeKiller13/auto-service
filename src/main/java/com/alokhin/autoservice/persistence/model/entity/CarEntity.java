package com.alokhin.autoservice.persistence.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@Table (name = "car")
public class CarEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private Integer year;

    @NotNull
    private Integer price;

    @Builder.Default
    @NotNull
    private Boolean enabled = false;

    @JoinColumn (name = "account_id")
    @ManyToOne
    private AccountEntity accountEntity;

    private String description;

    private String imageUrl;

    @Tolerate
    public CarEntity() {
    }
}
