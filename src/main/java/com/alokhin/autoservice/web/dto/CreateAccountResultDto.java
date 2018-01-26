package com.alokhin.autoservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountResultDto {

    private AccountDto account;
    private MessageDto messageDto;
}
