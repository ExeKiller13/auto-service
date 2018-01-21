package com.alokhin.autoservice.persistence.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@Builder
@Table (name = "account", uniqueConstraints = {@UniqueConstraint (columnNames = {"login"})})
public class AccountEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String login;

    @NotNull
    private String password;

    /**
     * Roles are being eagerly loaded here because
     * they are a fairly small collection of items for this example.
     */
    @NotNull
    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable (name = "role", joinColumns = @JoinColumn (name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn (name = "role_id", referencedColumnName = "id"))
    private List<RoleEntity> roles;

    @Builder.Default
    @NotNull
    private Boolean enabled = false;

    @Tolerate
    public AccountEntity() {
    }

    public void enable() {
        this.enabled = true;
    }
}
