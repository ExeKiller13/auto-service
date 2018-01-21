package com.alokhin.autoservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private String login;
    private List<String> role;
    private Boolean enabled;
}
